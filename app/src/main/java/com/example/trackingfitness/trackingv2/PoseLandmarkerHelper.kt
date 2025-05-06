// PoseLandmarkerHelper.kt
package com.example.trackingfitness.trackingv2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow
import kotlin.math.sqrt

//class PoseLandmarkerHelper(
//    var minPoseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE,
//    var minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE,
//    var minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE,
//    var currentModel: Int = MODEL_POSE_LANDMARKER_HEAVY,
//    var currentDelegate: Int = DELEGATE_GPU,
//    var runningMode: RunningMode = RunningMode.LIVE_STREAM,
//    val context: Context,
//    val modelFile: String = "pose_classification_model_legs.tflite",

//    val poseLandmarkerHelperListener: LandmarkerListener? = null,
//)
class PoseLandmarkerHelper(
    var minPoseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE,
    var minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE,
    var minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE,
    var currentModel: Int = MODEL_POSE_LANDMARKER_HEAVY,
    var currentDelegate: Int = DELEGATE_GPU,
    var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    val modelFile: String = "pose_classification_model_legs.tflite",
    val labelFile: String = "labels_legs.txt",
    val poseLandmarkerHelperListener: LandmarkerListener? = null,
    var routineStarted: Boolean = false
) {

    private var poseLandmarker: PoseLandmarker? = null
    private lateinit var tfliteInterpreter: Interpreter
    private lateinit var labelEncoder: List<String>  // No la inicializamos aquí directamente//
    private val emaSmoother = EMASmoother()


    init {
        setupPoseLandmarker()
        setupTFLiteInterpreter()
    }

    fun clearPoseLandmarker() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    fun loadLabelsFromAssets(context: Context, filename: String): List<String> {
        val inputStream = context.assets.open(filename)
        return inputStream.bufferedReader().readLines()
    }

    private fun setupPoseLandmarker() {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionBuilder.setDelegate(Delegate.GPU)
        }

        val modelName =
            when (currentModel) {
                MODEL_POSE_LANDMARKER_FULL -> "pose_landmarker_full.task"
                MODEL_POSE_LANDMARKER_LITE -> "pose_landmarker_lite.task"
                MODEL_POSE_LANDMARKER_HEAVY -> "pose_landmarker_heavy.task"
                else -> "pose_landmarker_full.task"
            }

        baseOptionBuilder.setModelAssetPath(modelName)

        when (runningMode) {
            RunningMode.LIVE_STREAM -> {
                if (poseLandmarkerHelperListener == null) {
                    throw IllegalStateException(
                        "poseLandmarkerHelperListener must be set when runningMode is LIVE_STREAM."
                    )
                }
            }
            else -> {}
        }

        try {
            val baseOptions = baseOptionBuilder.build()
            val optionsBuilder =
                PoseLandmarker.PoseLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                    .setMinTrackingConfidence(minPoseTrackingConfidence)
                    .setMinPosePresenceConfidence(minPosePresenceConfidence)
                    .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }

            val options = optionsBuilder.build()
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for details"
            )
            Log.e(TAG, "MediaPipe failed to load the task with error: ${e.message}")
        } catch (e: RuntimeException) {
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for details", GPU_ERROR
            )
            Log.e(TAG, "Pose Landmarker failed to load model with error: ${e.message}")
        }
    }

    private fun setupTFLiteInterpreter() {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd(modelFile)
        val inputStream = fileDescriptor.createInputStream()
        val byteBuffer = ByteBuffer.allocateDirect(fileDescriptor.length.toInt()).order(ByteOrder.nativeOrder())
        inputStream.channel.read(byteBuffer)
        byteBuffer.rewind()

        tfliteInterpreter = Interpreter(byteBuffer)

        labelEncoder = loadLabelsFromAssets(context, labelFile)
    }

    fun detectLiveStream(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean
    ) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalArgumentException(
                "Attempting to call detectLiveStream while not using RunningMode.LIVE_STREAM"
            )
        }

        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer =
            Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            if (isFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    imageProxy.width.toFloat(),
                    imageProxy.height.toFloat()
                )
            }
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        detectAsync(mpImage, frameTime)
    }

    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long) {
        poseLandmarker?.detectAsync(mpImage, frameTime)
    }

    private fun returnLivestreamResult(
        result: PoseLandmarkerResult,
        input: MPImage
    ) {
        if (shouldSkipFrame(result)) {
            poseLandmarkerHelperListener?.onResults(
                ResultBundle(
                    results = emptyList(),
                    poseClass = "",
                    inferenceTime = 0L,
                    inputImageHeight = input.height,
                    inputImageWidth = input.width,
                    landmarksList = emptyList(),
                )
            )
            return
        }

        val landmarks = result.landmarks().firstOrNull()?.map {
            listOf(it.x(), it.y(), it.z())
        }?.flatten()?.toFloatArray()

        if (landmarks != null) {
            val normalizedLandmarks = normalizeLandmarks(landmarks)

            val rawPredictions = classifyPoseProbabilities(normalizedLandmarks)

            val smoothedConfidences = emaSmoother.getSmoothedResult(rawPredictions)

//            val smoothedClassIdx = smoothedConfidences.maxByOrNull { it.value }?.key ?: 0
//            val smoothedPoseClass = getPoseLabel(smoothedClassIdx) // Convertir índice a etiqueta

            val landmarksList = normalizedLandmarks.toList().chunked(3).map { it.toFloatArray() }

            val poseClassFinal = if (!routineStarted && isXPose(landmarksList)) {
                "x_pose"
            } else {
                val smoothedClassIdx = smoothedConfidences.maxByOrNull { it.value }?.key ?: 0
                getPoseLabel(smoothedClassIdx)
            }

            poseLandmarkerHelperListener?.onResults(
                ResultBundle(
                    listOf(result),
//                    smoothedPoseClass, // 🔥 Ahora usamos la predicción suavizada
                    poseClassFinal,
                    SystemClock.uptimeMillis() - result.timestampMs(),
                    input.height,
                    input.width,
                    landmarksList
                )
            )
        }
    }

    private fun shouldSkipFrame(result: PoseLandmarkerResult): Boolean {
        val poseLandmarks = result.landmarks().firstOrNull() ?: return true

        // Umbral dinámico según el modelo activo
        val isChestModel = ModelConfig.currentModel == "chest" || ModelConfig.currentModel == "core"
        val shoulderThreshold = if (isChestModel) 0.8f else 0.22f

        val leftShoulder = poseLandmarks.getOrNull(11)
        val rightShoulder = poseLandmarks.getOrNull(12)
        if (leftShoulder != null && rightShoulder != null) {
            val shoulderDist = kotlin.math.abs(leftShoulder.x() - rightShoulder.x())
            if (shoulderDist > shoulderThreshold) return true
        }

        // Verificar si hay landmarks fuera del frame (x/y no están en [0, 1])
        val outOfBounds = poseLandmarks.count {
            it.x() !in 0f..1f || it.y() !in 0f..1f
        }
        val ratioOutOfBounds = outOfBounds.toFloat() / poseLandmarks.size
        if (ratioOutOfBounds > 0.2f) return true  // si más del 20% están fuera

        return false
    }

    private fun normalizeLandmarks(landmarks: FloatArray): FloatArray {
        if (landmarks.size < 99) return landmarks

        val reshapedLandmarks = landmarks.toList().chunked(3).map { it.toFloatArray() }

        val leftHip = reshapedLandmarks.getOrNull(23) ?: return landmarks
        val rightHip = reshapedLandmarks.getOrNull(24) ?: return landmarks

        // Validación de calidad
        if (leftHip[1] < 0.1 || rightHip[1] < 0.1) return landmarks // Si las caderas están demasiado arriba, ignora el frame

        val center = floatArrayOf(
            (leftHip[0] + rightHip[0]) / 2,
            (leftHip[1] + rightHip[1]) / 2,
            (leftHip[2] + rightHip[2]) / 2
        )

        reshapedLandmarks.forEach {
            it[0] -= center[0]
            it[1] -= center[1]
            it[2] -= center[2]
        }

        val leftShoulder = reshapedLandmarks.getOrNull(11) ?: return landmarks
        val rightShoulder = reshapedLandmarks.getOrNull(12) ?: return landmarks

        val torsoSize = sqrt(
            ((leftShoulder[0] + rightShoulder[0]) / 2 - center[0]).pow(2) +
                    ((leftShoulder[1] + rightShoulder[1]) / 2 - center[1]).pow(2) +
                    ((leftShoulder[2] + rightShoulder[2]) / 2 - center[2]).pow(2)
        ).coerceAtLeast(1e-6f)

        reshapedLandmarks.forEach {
            it[0] /= torsoSize
            it[1] /= torsoSize
            it[2] /= torsoSize
        }

        return reshapedLandmarks.flatMap { it.toList() }.toFloatArray()
    }

    private fun classifyPoseProbabilities(normalizedLandmarks: FloatArray): FloatArray {
        val inputBuffer = ByteBuffer.allocateDirect(normalizedLandmarks.size * 4).order(ByteOrder.nativeOrder())
        inputBuffer.asFloatBuffer().put(normalizedLandmarks)

        val outputBuffer = ByteBuffer.allocateDirect(labelEncoder.size * 4).order(ByteOrder.nativeOrder())
        tfliteInterpreter.run(inputBuffer, outputBuffer)

        outputBuffer.rewind()
        val probabilities = FloatArray(labelEncoder.size)
        outputBuffer.asFloatBuffer().get(probabilities)

        return probabilities // 🔥 Ahora retorna probabilidades en lugar de una clase
    }

    private fun getPoseLabel(classIndex: Int): String {
        return if (classIndex in labelEncoder.indices) labelEncoder[classIndex] else "Unknown"
    }

    private fun returnLivestreamError(error: RuntimeException) {
        poseLandmarkerHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    fun isXPose(landmarks: List<FloatArray>): Boolean {
        val leftWrist = landmarks.getOrNull(15) ?: return false
        val rightWrist = landmarks.getOrNull(16) ?: return false
        val leftShoulder = landmarks.getOrNull(11) ?: return false
        val rightShoulder = landmarks.getOrNull(12) ?: return false

        val rightToLeftShoulder = distance2D(rightWrist, leftShoulder)
        val leftToRightShoulder = distance2D(leftWrist, rightShoulder)

        val handsCrossedAndClose = rightToLeftShoulder < 0.075f && leftToRightShoulder < 0.075f

        val avgShoulderY = (leftShoulder[1] + rightShoulder[1]) / 2
        val handsAtChestLevel = leftWrist[1] < avgShoulderY + 0.2f && rightWrist[1] < avgShoulderY + 0.2f

        return handsCrossedAndClose && handsAtChestLevel
    }

    private fun distance2D(a: FloatArray, b: FloatArray): Float {
        return sqrt((a[0] - b[0]).pow(2) + (a[1] - b[1]).pow(2))
    }

    companion object {
        const val TAG = "PoseLandmarkerHelper"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.9F
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.9F
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.9F
        const val MODEL_POSE_LANDMARKER_FULL = 0
        const val MODEL_POSE_LANDMARKER_LITE = 1
        const val MODEL_POSE_LANDMARKER_HEAVY = 2
        const val GPU_ERROR = 1
    }

    data class ResultBundle(
        val results: List<PoseLandmarkerResult>,
        val poseClass: String,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
        val landmarksList: List<FloatArray>
    )

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = GPU_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }
}
