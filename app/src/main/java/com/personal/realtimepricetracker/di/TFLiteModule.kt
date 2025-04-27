package com.personal.realtimepricetracker.di

import android.content.Context
import android.util.Log
import com.personal.realtimepricetracker.data.model.DailyData
import com.personal.realtimepricetracker.data.model.PredictorFeedData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.flex.FlexDelegate
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TFLiteModule {
    @Provides
    @Singleton
    fun provideTFLiteStockPredictor(context: Context): TFLiteStockPredictor {
        return TFLiteStockPredictor(context)
    }
}

class TFLiteStockPredictor @Inject constructor(private val context: Context) {
    private val interpreter: Interpreter by lazy {
        val model = loadModelFile(context)
        val options = Interpreter.Options()
            .addDelegate(FlexDelegate())
            .setNumThreads(4)
        val interpreter = Interpreter(model, options)
        // Log expected input shape for debugging
        val inputShape = interpreter.getInputTensor(0).shape()
        Log.d("TFLiteStockPredictor", "Expected input shape: ${inputShape.contentToString()}")
        interpreter
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("stock_price_lstm_multi.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.length)
    }

    fun predictNextPrice(previousPrices: List<PredictorFeedData>): Float {
        // Ensure exactly 5 time steps (pad or trim as needed)
        val requiredWindowSize = 5
        val paddedPrices = if (previousPrices.size < requiredWindowSize) {
            val lastPrice = if (previousPrices.isNotEmpty()) previousPrices.last() else null
            val padding = List(requiredWindowSize - previousPrices.size) {
                lastPrice ?: PredictorFeedData(
                    date = "",
                    open = 0.0f,
                    high = 0.0f,
                    low = 0.0f,
                    close = 0.0f,
                    volume = 0
                )
            }
            previousPrices + padding
        } else {
            previousPrices.takeLast(requiredWindowSize)
        }

        // Normalize features (open, high, low, close, volume)
        val scaledFeatures = listOf(
            paddedPrices.map { it.open },
            paddedPrices.map { it.high },
            paddedPrices.map { it.low },
            paddedPrices.map { it.close },
            paddedPrices.map { it.volume.toFloat() }
        ).map { feature ->
            val min = feature.minOrNull() ?: 0f
            val max = feature.maxOrNull() ?: 1f
            feature.map { (it - min) / (max - min) }.toFloatArray()
        }

        // Prepare input with shape [1, windowSize, numFeatures] (e.g., [1, 5, 5])
        val windowSize = requiredWindowSize
        val numFeatures = 5
        val input = Array(1) { Array(windowSize) { FloatArray(numFeatures) } }
        for (i in 0 until windowSize) {
            for (f in 0 until numFeatures) {
                input[0][i][f] = scaledFeatures[f][i]
                Log.d("TFLiteStockPredictor", "input[0][$i][$f] = ${input[0][i][f]}")
            }
        }

        val output = Array(1) { FloatArray(1) }
        interpreter.run(input, output)
        Log.d("TFLiteStockPredictor", "output[0][0] = ${output[0][0]}")

        // Denormalize the predicted close price
        val closeMin = paddedPrices.map { it.close }.minOrNull() ?: 0f
        val closeMax = paddedPrices.map { it.close }.maxOrNull() ?: 1f
        val scaledPrediction = output[0][0]
        val predictedValue = scaledPrediction * (closeMax - closeMin) + closeMin
        Log.d("TFLiteStockPredictor", "PredictedPrice = $predictedValue")
        return predictedValue
    }

    fun close() {
        interpreter.close()
    }
}