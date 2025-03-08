package com.example.trackingfitness.trackingv2

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import androidx.lifecycle.lifecycleScope
import com.example.trackingfitness.MainActivity
import com.example.trackingfitness.R
import com.example.trackingfitness.screens.MyExercisesScreen
import com.example.trackingfitness.viewModel.UserSessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mediapipe.tasks.vision.core.RunningMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraScreenV2 : AppCompatActivity(), PoseLandmarkerHelper.LandmarkerListener {

    private lateinit var cameraHelper: CameraHelper
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private lateinit var backgroundExecutor: ExecutorService
    private var userSessionManager: UserSessionManager? = null

    private var selectedExercise: String = "squats"
    private var repetitionCount = 0
    private var lastState: String? = null
    private var currentState: String? = null

    private var currentModel: String
        get() = ModelConfig.currentModel
        set(value) {
            ModelConfig.currentModel = value
        }

    private val models = ModelConfig.models

    private val exerciseTransitions = ExerciseTransitions.exerciseTransitions
    private val unilateralExercises = ExerciseTransitions.unilateralExercises

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_screen_v2)

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
            } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        userSessionManager = UserSessionManager(applicationContext)

        val idExercise = intent.getIntExtra("EXERCISE_ID", -1) // 🔥 Recibir ID
//        Log.d("CameraScreenV2", "ID del ejercicio recibido: $idExercise")

        val token = intent.getStringExtra("USER_TOKEN") ?: ""
//        Log.d("TokenCheck", "Token actual: $token")

        if (token.isNotEmpty()) {
            userSessionManager?.showExercise(token, idExercise) { exercise ->
                runOnUiThread {
                    findViewById<TextView>(R.id.exerciseName).text = "Ejercicio: ${exercise.name}"
                }
                Log.d("CameraScreenV2", "Ejercicio recibido y UI actualizada: ${exercise.name}")
            }
        } else {
            Log.e("TokenCheck", "El token está vacío. No se puede hacer la petición.")
        }

        backgroundExecutor = Executors.newSingleThreadExecutor()
        cameraHelper = CameraHelper(this, backgroundExecutor) { image -> detectPose(image) }

        setupUI()
        initializePoseLandmarker()
    }

    private fun setupUI() {
        setupExerciseSpinner()
        setupModelSpinner()
        setupDropdownMenu()
    }

    private fun setupExerciseSpinner() {
        val exerciseSpinner: Spinner = findViewById(R.id.exerciseSpinner)
        val exercises = getExercisesForCurrentModel()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exercises)
        exerciseSpinner.adapter = adapter

        exerciseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedExercise = exercises[position]
                repetitionCount = 0
                findViewById<TextView>(R.id.repetitionCount).text = "Reps: 0"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupModelSpinner() {
        val modelSpinner: Spinner = findViewById(R.id.modelSpinner)
        val modelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, models)
        modelSpinner.adapter = modelAdapter

        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentModel = models[position]
                resetPoseLandmarker()
                setupExerciseSpinner()  // Recargar lista de ejercicios al cambiar modelo
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDropdownMenu() {
        val fabMenu: FloatingActionButton = findViewById(R.id.fabMenu)

        fabMenu.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
            bottomSheetDialog.setContentView(view)

            val btnSwitchCamera = view.findViewById<Button>(R.id.btnSwitchCamera)
            val btnRotateCamera = view.findViewById<Button>(R.id.btnRotateCamera)
            val btnPauseRoutine = view.findViewById<Button>(R.id.btnPauseRoutine)
            val btnNextExercise = view.findViewById<Button>(R.id.btnNextExercise)
            val token = intent.getStringExtra("USER_TOKEN") ?: ""

            // Acciones de los botones mi fercho
            btnSwitchCamera.setOnClickListener {
                cameraHelper.toggleCamera()
                bottomSheetDialog.dismiss()
            }
            btnRotateCamera.setOnClickListener {
                cameraHelper.rotateCamera(this) // 🔄 Llama la función
                bottomSheetDialog.dismiss()
            }
            btnPauseRoutine.setOnClickListener {
                val intent = Intent().apply {
                    putExtra("navigateTo", "myExercisesScreen")
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 🔥 Borra la pila de actividades
                setResult(Activity.RESULT_OK, intent)
                finish()
                bottomSheetDialog.dismiss()
            }
            btnNextExercise.setOnClickListener {
                // Función para avanzar al siguiente ejercicio
                userSessionManager?.updateExerciseState(token)
                val intent = Intent().apply {
                    putExtra("navigateTo", "myExercisesScreen")
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 🔥 Borra la pila de actividades
                setResult(Activity.RESULT_OK, intent)
                finish()
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun getExercisesForCurrentModel(): List<String> {
        return ModelConfig.getExercisesForModel()
    }

    private fun initializePoseLandmarker() {
        val modelFile = ModelConfig.getModelFile()
        val labelFile = ModelConfig.getLabelFile()

        try {
            assets.open(modelFile).close()
            assets.open(labelFile).close()
            Log.d("Modelo",modelFile)
            Log.d("Labels",labelFile)
        } catch (e: Exception) {
            Log.e("ModelCheck", "Error al cargar archivos de modelo: ${e.message}")
            return
        }

        poseLandmarkerHelper = PoseLandmarkerHelper(
            context = this,
            runningMode = RunningMode.LIVE_STREAM,
            modelFile = modelFile,
            labelFile = labelFile,
            poseLandmarkerHelperListener = this
        )
    }

    private fun detectPose(imageProxy: ImageProxy) {
        if (!this::poseLandmarkerHelper.isInitialized) {
            imageProxy.close()
            return
        }

        poseLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = cameraHelper.isFrontCamera()
        )
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        runOnUiThread {
//            findViewById<TextView>(R.id.inferenceTimeVal).text =
//                String.format("%d ms", resultBundle.inferenceTime)

            findViewById<OverlayView>(R.id.overlay).setResults(
                resultBundle.results.first(),
                resultBundle.poseClass,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )

            val isUnilateral = selectedExercise in unilateralExercises
            val exerciseFilter = exerciseTransitions[selectedExercise]

            // Solo evaluar la variante si el ejercicio seleccionado es "diamond_pushups"
//            if (selectedExercise in listOf("diamond_pushups", "pushups", "wide_pushups")) {
//                val pushupType = poseLandmarkerHelper.classifyPushupType(resultBundle.landmarksList)
//                findViewById<TextView>(R.id.exerciseType).text = "Variante: $pushupType"
//            }

            if (exerciseFilter != null && resultBundle.poseClass in exerciseFilter.values) {
                lastState = currentState
                currentState = resultBundle.poseClass

                if (isUnilateral) {
                    if ((lastState == exerciseFilter["left_end"] && currentState == exerciseFilter["start"]) ||
                        (lastState == exerciseFilter["right_end"] && currentState == exerciseFilter["start"])) {
                        repetitionCount++
                    }
                } else {
                    if (lastState == exerciseFilter["end"] && currentState == exerciseFilter["start"]) {
                        repetitionCount++
                    }
                }
            }

            findViewById<TextView>(R.id.repetitionCount).text = "Reps: $repetitionCount"
            findViewById<OverlayView>(R.id.overlay).invalidate()
        }
    }

    private fun resetPoseLandmarker() {
        backgroundExecutor.execute {
            if (this::poseLandmarkerHelper.isInitialized) {
                poseLandmarkerHelper.clearPoseLandmarker()
            }
            initializePoseLandmarker()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundExecutor.execute { poseLandmarkerHelper.clearPoseLandmarker() }
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }

    override fun onError(error: String, errorCode: Int) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    fun filterTrainedExercises(exercisesFromDB: List<String>): List<String> {
        return exercisesFromDB.filter { ModelConfig.isExerciseTrained(it) }
    }

}