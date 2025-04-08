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

    private var exerciseName: String = "squats"

    private val linePaintGreen = Paint().apply {
        color = Color.GREEN
        strokeWidth = LANDMARK_STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    private val linePaintRed = Paint().apply {
        color = Color.RED
        strokeWidth = LANDMARK_STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    private val linePaintWhite = Paint().apply {
        color = Color.WHITE
        strokeWidth = LANDMARK_STROKE_WIDTH
        style = Paint.Style.STROKE
    }

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

    fun clearResults() {
        this.results = null
        invalidate()
    }

    fun setPausedMode(poseLandmarkerResults: PoseLandmarkerResult) {
        results = poseLandmarkerResults
        poseClass = "" // 🔥 No mostrar el nombre de la pose en pausa
        invalidate()  // Forzar redibujado
    }

    private fun initPaints() {
        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_POINT_WIDTH
        pointPaint.style = Paint.Style.FILL

        linePaint.color = Color.GREEN
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        textPaint.color = Color.GREEN
        textPaint.textSize = TEXT_SIZE * 1.5f  // Hacerlo más grande
        textPaint.style = Paint.Style.FILL
        textPaint.setShadowLayer(10f, 5f, 5f, Color.BLACK)  // Agregar sombra negra
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { poseLandmarkerResult ->

            val transition = ExerciseTransitions.exerciseTransitions[exerciseName] ?: emptyMap()

            val dynamicPaint = when (poseClass) {
                transition["start"],
                transition["end"],
                transition["left_end"],
                transition["right_end"],
                transition["position"] -> linePaintGreen
                "x_pose" -> linePaintWhite
                else -> linePaintRed
            }

            for (landmark in poseLandmarkerResult.landmarks()) {
                PoseLandmarker.POSE_LANDMARKS.forEach {
                    canvas.drawLine(
                        poseLandmarkerResult.landmarks().get(0).get(it!!.start()).x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.start()).y() * imageHeight * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.end()).x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.end()).y() * imageHeight * scaleFactor,
                        dynamicPaint  // ← ahora usa el color correcto
                    )
                }

                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor,
                        normalizedLandmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )
                }
            }
        }
    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        poseClass: String,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.LIVE_STREAM,
        exerciseName: String // 🔥 nuevo
    ) {
        results = poseLandmarkerResults
        this.poseClass = poseClass
        this.imageHeight = imageHeight
        this.imageWidth = imageWidth
        this.exerciseName = exerciseName  // 👈 guardamos el nombre del ejercicio

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
        private const val LANDMARK_POINT_WIDTH = 18F
        private const val TEXT_SIZE = 50F
    }
}
