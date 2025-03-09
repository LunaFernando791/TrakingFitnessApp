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
import com.example.trackingfitness.R
import com.example.trackingfitness.viewModel.UserSessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mediapipe.tasks.vision.core.RunningMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

class CameraScreenV2 : AppCompatActivity(), PoseLandmarkerHelper.LandmarkerListener {

    private lateinit var cameraHelper: CameraHelper
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private lateinit var backgroundExecutor: ExecutorService
    private var userSessionManager: UserSessionManager? = null

    private lateinit var poseClassTextView: TextView
    private lateinit var repCountTextView: TextView
    private lateinit var setCountTextView: TextView

    private var selectedExercise: String = "squats"
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

    private var repetitionCount = 0
    private var currentSet = 1
    private val maxSets = 3
    private val repsPerSet = 3
    private var restTimerActive = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_screen_v2)

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        poseClassTextView = findViewById(R.id.poseClassText)
        repCountTextView = findViewById(R.id.repetitionCount)
        setCountTextView = findViewById(R.id.setCount)

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

        backgroundExecutor = Executors.newSingleThreadExecutor()
        cameraHelper = CameraHelper(this, backgroundExecutor) { image -> detectPose(image) }

        initializePoseLandmarker()
        setupUI()
    }

    // EL QUE SIRVE BIEN
    private fun setupUI() {
        val token = userSessionManager?.getUserSession()?.token ?: ""

        lifecycleScope.launch {
            userSessionManager?.getMyExercises()

            userSessionManager?.myExercises
                ?.drop(1)         // Ignora la primera emisi贸n (valor inicial vac铆o)
                ?.collect { myExerciseResponse ->
                    val exercises = myExerciseResponse?.selectedExercises

//                    if (totalExercisesCount == 0) {
//                        totalExercisesCount = exercises!!.size
//                        exerciseCounter = exercises.count { it.status == "completado" }
//                        Log.d("CameraScreenV2", "Total ejercicios: $totalExercisesCount")
//                    }

                    val currentExercise = exercises?.firstOrNull { it.status == "actual" }

                    Log.d("cuerpo current", currentExercise.toString())
                    if (currentExercise != null) {
                        userSessionManager?.showExercise(token, currentExercise.exercise_id) { exercise ->
                            runOnUiThread {
                                findViewById<TextView>(R.id.exerciseName).text = "Ejercicio: ${exercise.name}"
                                repetitionCount = 0
                                findViewById<TextView>(R.id.repetitionCount).text = "Reps: 0"
//                                selectedExercise = exercise.name
                                resetPoseLandmarker() // 🔥 Forzar actualización del modelo
                            }
                            Log.d("CameraScreenV2", "Ejercicio cargado correctamente: ${exercise.name}")
                        }
                    } else {
                        Log.d("CameraScreenV2", "No se encontr贸 ejercicio actual.")
                        navigateToExerciseListScreen()
                    }
                }
        }

        setupExerciseSpinner()
        setupModelSpinner()
        setupDropdownMenu()
    }

    private fun navigateToExerciseListScreen() {
        runOnUiThread {
            Toast.makeText(this@CameraScreenV2, "Rutina completada", Toast.LENGTH_SHORT).show()
            val intent = Intent().apply {
                putExtra("navigateTo", "exerciseListScreen")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
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

            if (currentSet > maxSets) {
                btnNextExercise.isEnabled = true
            }

            // Acciones de los botones mi fercho
            btnSwitchCamera.setOnClickListener {
                cameraHelper.toggleCamera()
                bottomSheetDialog.dismiss()
            }
            btnRotateCamera.setOnClickListener {
                cameraHelper.rotateCamera(this) // 馃攧 Llama la funci贸n
                bottomSheetDialog.dismiss()
            }
            btnPauseRoutine.setOnClickListener {
                val intent = Intent().apply {
                    putExtra("navigateTo", "exerciseListScreen")
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                setResult(Activity.RESULT_OK, intent)
                finish()
                bottomSheetDialog.dismiss()
            }
            btnNextExercise.setOnClickListener {
//                val token = userSessionManager?.getUserSession()?.token ?: ""
//
//                lifecycleScope.launch {
//                    userSessionManager?.updateExerciseState(token)
//                    delay(1000)  // peque帽o delay para backend
//                    userSessionManager?.getMyExercises() // actualizaci贸n expl铆cita para forzar emisi贸n nueva
//                }
//
                currentSet = 0
                repetitionCount = 0
                btnNextExercise.isEnabled = false
                findViewById<TextView>(R.id.setCount).text = "Set: $currentSet/$maxSets"
                advanceToNextExercise()
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
            val overlayView = findViewById<OverlayView>(R.id.overlay)

            if (restTimerActive) {
                overlayView.clear()
                poseClassTextView.text = "Descansando..."
                poseClassTextView.visibility = View.VISIBLE
                repCountTextView.visibility = View.INVISIBLE
                setCountTextView.visibility = View.INVISIBLE
                return@runOnUiThread
            }

            if (currentSet > maxSets) {
                overlayView.clear()
                poseClassTextView.text = "Ejercicio Completado"
                poseClassTextView.visibility = View.INVISIBLE
                repCountTextView.visibility = View.INVISIBLE
                setCountTextView.visibility = View.INVISIBLE
                return@runOnUiThread
            }

            findViewById<OverlayView>(R.id.overlay).setResults(
                resultBundle.results.first(),
                resultBundle.poseClass,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )
            poseClassTextView.text = "Pose: ${resultBundle.poseClass}"
            poseClassTextView.visibility = View.VISIBLE
            repCountTextView.visibility = View.VISIBLE
            setCountTextView.visibility = View.VISIBLE

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

            if (repetitionCount >= repsPerSet) {
                onSetCompleted()
            }

            findViewById<TextView>(R.id.repetitionCount).text = "Reps: $repetitionCount"
            findViewById<OverlayView>(R.id.overlay).invalidate()
        }
    }

    private fun onSetCompleted() {
        if (currentSet <= maxSets) {
            currentSet++
            repetitionCount = 0
            findViewById<TextView>(R.id.setCount).text = "Set: $currentSet/$maxSets"

            // Iniciar cronómetro de descanso de 15 segundos
            restTimerActive = true
            startRestTimer()
        }
    }

    private fun startRestTimer() {
        lifecycleScope.launch {
            var secondsRemaining = 5
            val timerTextView: TextView = findViewById(R.id.timerTextView)

            timerTextView.visibility = View.VISIBLE
            while (secondsRemaining > 0) {
                timerTextView.text = "Descanso: $secondsRemaining s"
                delay(1000)
                secondsRemaining--
            }

            timerTextView.visibility = View.GONE
            restTimerActive = false
            Toast.makeText(this@CameraScreenV2, "¡Inicia el set $currentSet!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun advanceToNextExercise() {
        val token = userSessionManager?.getUserSession()?.token ?: ""
        lifecycleScope.launch {
            userSessionManager?.updateExerciseState(token)
            delay(1500)  // Pequeño delay para que el backend procese la actualización
            userSessionManager?.getMyExercises()
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