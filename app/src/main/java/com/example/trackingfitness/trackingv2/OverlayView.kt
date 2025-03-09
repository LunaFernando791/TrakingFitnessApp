// OverlayView.kt
package com.example.trackingfitness.trackingv2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var poseClass: String = ""
    private var pointPaint = Paint()
    private var linePaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
    }

    fun clear() {
        results = null
        poseClass = ""
        pointPaint.reset()
        linePaint.reset()
        textPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color = Color.WHITE
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL

        textPaint.color = Color.GREEN
        textPaint.textSize = TEXT_SIZE * 1.5f  // Hacerlo más grande
        textPaint.style = Paint.Style.FILL
        textPaint.setShadowLayer(10f, 5f, 5f, Color.BLACK)  // Agregar sombra negra
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { poseLandmarkerResult ->
            for (landmark in poseLandmarkerResult.landmarks()) {
                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor,
                        normalizedLandmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )
                }

                // Draw connections between landmarks (skeleton)
                PoseLandmarker.POSE_LANDMARKS.forEach {
                    canvas.drawLine(
                        poseLandmarkerResult.landmarks().get(0).get(it!!.start()).x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.start()).y() * imageHeight * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.end()).x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.end()).y() * imageHeight * scaleFactor,
                        linePaint
                    )
                }
            }

            // 🔹 Dibujar una sombra negra detrás del texto (para un mejor efecto de sombreado)
//            val shadowOffset = 5f
//            textPaint.color = Color.BLACK
//            canvas.drawText(
//                "Ejercicio: $poseClass",
//                TEXT_POSITION_X + shadowOffset,
//                TEXT_POSITION_Y + shadowOffset,
//                textPaint
//            )
//
//            // 🔹 Dibujar el texto principal en color verde
//            textPaint.color = Color.GREEN
//            canvas.drawText(
//                "Ejercicio: $poseClass",
//                TEXT_POSITION_X,
//                TEXT_POSITION_Y,
//                textPaint
//            )
        }
    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        poseClass: String,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.LIVE_STREAM
    ) {
        results = poseLandmarkerResults
        this.poseClass = poseClass
        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
        private const val TEXT_POSITION_X = 50F
        private const val TEXT_POSITION_Y = 230F
        private const val TEXT_SIZE = 50F
    }
}
