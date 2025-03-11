package com.example.trackingfitness.trackingv2

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import android.media.MediaPlayer

class CameraScreenV2 : AppCompatActivity(), PoseLandmarkerHelper.LandmarkerListener {

    private lateinit var cameraHelper: CameraHelper
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private lateinit var backgroundExecutor: ExecutorService
    private var userSessionManager: UserSessionManager? = null

    private lateinit var poseClassTextView: TextView
    private lateinit var repCountTextView: TextView
    private lateinit var setCountTextView: TextView
    private lateinit var generalTimerTextView: TextView
    private lateinit var statusTextView: TextView

    private var selectedExercise: String = "wall sit"
    private var lastState: String? = null
    private var currentState: String? = null
    private var generalTimeMillis: Long = 0
    private val generalHandler = Handler(Looper.getMainLooper())

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

    private var routineStarted = false
    private var routineEnded = false
    private var isResting = true

    private var isPaused = false
    private var pauseStartTime = 0L
    private val pauseDuration = 3000L

    private var startPoseTime = 0L
    private var generalTimer: Handler? = null

    private var isometricStartTime: Long = 0L
    private val isometricHoldDuration = 5000L
    private var isometricTotalTime: Long = 0L


    private var mediaPlayer: MediaPlayer? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_screen_v2)

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        poseClassTextView = findViewById(R.id.poseClassText)
        repCountTextView = findViewById(R.id.repetitionCount)
        setCountTextView = findViewById(R.id.setCount)
        generalTimerTextView = findViewById(R.id.generalTimer)
        statusTextView = findViewById(R.id.statusText)

        repCountTextView.visibility = View.INVISIBLE
        setCountTextView.visibility = View.INVISIBLE
        statusTextView.visibility = View.VISIBLE
        generalTimerTextView.visibility = View.INVISIBLE

        mediaPlayer = MediaPlayer.create(this, R.raw.beep)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        if(selectedExercise in ExerciseTransitions.isometricExercises){
                                    Log.d("Isométrico","el ejercicio es isométrico")
                                }else{
                                    Log.d("Isométrico", "No és jaja")
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

                    val currentExercise = exercises?.firstOrNull { it.status == "actual" }

                    Log.d("cuerpo current", currentExercise.toString())
                    if (currentExercise != null) {
                        userSessionManager?.showExercise(token, currentExercise.exercise_id) { exercise ->
                            runOnUiThread {
                                findViewById<TextView>(R.id.exerciseName).text = "Ejercicio: ${exercise.name}"
                                repetitionCount = 0
                                findViewById<TextView>(R.id.repetitionCount).text = "Reps: 0"
//                                selectedExercise = exercise.name.lowercase()
//                                if(selectedExercise in ExerciseTransitions.isometricExercises){
//                                    Log.d("Isométrico","el ejercicio es isométrico")
//                                }else{
//                                    Log.d("Isométrico", "No és jaja")
//                                }
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

        if(selectedExercise in ExerciseTransitions.isometricExercises){
            repCountTextView.text = "Tiempo: 0s"
        }
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

            if(currentSet > maxSets){
                btnNextExercise.isEnabled = true
                hideCounters()
            }

            // Acciones de los botones mi fercho
            btnSwitchCamera.setOnClickListener {
                cameraHelper.toggleCamera()
                bottomSheetDialog.dismiss()
            }
            btnRotateCamera.setOnClickListener {
                cameraHelper.rotateCamera(this)
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
            val overlayView = findViewById<OverlayView>(R.id.overlay)
            val detectedPose = resultBundle.poseClass
            poseClassTextView.text = detectedPose

            // 🔹 Mostrar landmarks si la rutina está en curso
            overlayView.setResults(
                resultBundle.results.first(),
                resultBundle.poseClass,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )

            // 🔹 Si la rutina ha finalizado, aplicar `clear()`
            if (routineEnded) {
                poseClassTextView.text = "¡Ejercicio completado!"
                hideCounters()
                overlayView.clear()
                return@runOnUiThread
            }

            // 🔹 Rutina aún no ha empezado: Validar "x_pose"
            if (!routineStarted) {
                if (detectedPose == "x_pose") {
                    if (startPoseTime == 0L) {
                        startPoseTime = System.currentTimeMillis()
                    }

                    val elapsedTime = System.currentTimeMillis() - startPoseTime

                    statusTextView.text = "Iniciando en: ${3 - elapsedTime / 1000} s"

                    if (elapsedTime >= 3000) {
                        isResting = false
                        routineStarted = true
//                        startGeneralTimer()
                        showCounters()
                        statusTextView.text = "Ejercicio iniciado!"
                        Handler(Looper.getMainLooper()).postDelayed({
                            statusTextView.visibility = View.INVISIBLE
                        }, 1000)
                    }
                } else {
                    startPoseTime = 0L
                    statusTextView.text = "Mantén 'x_pose' para comenzar"
                }
                return@runOnUiThread
            }

            // 🔹 Si está en descanso, aplicar `setPausedMode()`
            if (isResting) {
                overlayView.clear()
                hideCounters()
                return@runOnUiThread
            } else{
                showCounters()
            }

            // 🔹 Manejo de pausa/reanudación con "x_pose"
//            if (detectedPose == "x_pose" && !isResting) {
//                if (pauseStartTime == 0L) {
//                    pauseStartTime = System.currentTimeMillis()
//                }
//
//                val elapsedTime = System.currentTimeMillis() - pauseStartTime
//                val remainingTime = (pauseDuration - elapsedTime) / 1000
//
//                if (isPaused) {
//                    statusTextView.text = "Reanudando en: $remainingTime s"
//                } else {
//                    statusTextView.text = "Pausando en: $remainingTime s"
//                }
//                statusTextView.visibility = View.VISIBLE
//
//                if (elapsedTime >= pauseDuration) {
//                    isPaused = !isPaused
//                    pauseStartTime = 0L
//
//                    if (isPaused) {
//                        statusTextView.text = "Rutina en pausa"
//                    } else {
//                        statusTextView.text = "Rutina reanudada"
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            statusTextView.visibility = View.INVISIBLE
//                        }, 1000)
//                    }
//                }
//                return@runOnUiThread
//            } else {
//                pauseStartTime = 0L
//                statusTextView.visibility = View.INVISIBLE
//            }
//
//
//            if (isPaused) {
//                overlayView.setPausedMode(resultBundle.results.first())
//                poseClassTextView.text = "PAUSADO"
//                return@runOnUiThread
//            }

            if (selectedExercise in ExerciseTransitions.isometricExercises) {
                handleIsometricExercise(detectedPose)
                return@runOnUiThread
            }
            handleRepetitiveExercise(detectedPose)

            findViewById<TextView>(R.id.repetitionCount).text = "Reps: $repetitionCount"
            findViewById<OverlayView>(R.id.overlay).invalidate()
        }
    }

    private fun handleRepetitiveExercise(detectedPose: String) {
        val exerciseFilter = exerciseTransitions[selectedExercise]

        if (exerciseFilter != null && detectedPose in exerciseFilter.values) {
            lastState = currentState
            currentState = detectedPose

            val isUnilateral = selectedExercise in unilateralExercises
            val isValidTransition = if (isUnilateral) {
                (lastState == exerciseFilter["left_end"] && currentState == exerciseFilter["start"]) ||
                        (lastState == exerciseFilter["right_end"] && currentState == exerciseFilter["start"])
            } else {
                lastState == exerciseFilter["end"] && currentState == exerciseFilter["start"]
            }

            if (isValidTransition) {
                mediaPlayer?.start() // 🔊 Sonido de repetición
                repetitionCount++
                repCountTextView.text = "Reps: $repetitionCount / $repsPerSet"

                if (repetitionCount >= repsPerSet) {
                    repetitionCount = 0
                    currentSet++

                    if (currentSet > maxSets) {
                        routineEnded = true
                        statusTextView.text = "¡Ejercicio terminado!"
                    } else {
                        setCountTextView.text = "Set: $currentSet / $maxSets"
                        startRestPeriod()
                    }
                }
            }
        }
    }

    private fun handleIsometricExercise(detectedPose: String) {
        val exerciseFilter = ExerciseTransitions.exerciseTransitions[selectedExercise]
        val isometricPose = exerciseFilter?.get("position") ?: selectedExercise

        if (!routineStarted || routineEnded || isPaused || isResting) {
            return
        }

        if (detectedPose == isometricPose) {
            if (isometricStartTime == 0L) {
                isometricStartTime = System.currentTimeMillis()
            }

            val elapsedTime = (System.currentTimeMillis() - isometricStartTime) / 1000
            isometricTotalTime += 1 // Acumular tiempo total en segundos

            repCountTextView.visibility = View.VISIBLE
            repCountTextView.text = "Tiempo: $elapsedTime s"

            // **✅ Cuando llegue a 5 segundos, completar el set y pausar el conteo**
            if (elapsedTime >= isometricHoldDuration / 1000) {
                mediaPlayer?.start() // 🔊 Sonido indicando que el set se completó
                currentSet++

                setCountTextView.text = "Set: $currentSet / $maxSets"

                isometricStartTime = 0L

                if (currentSet > maxSets) {
                    routineEnded = true
                    statusTextView.text = "¡Rutina terminada!"
                    isometricTotalTime = 0L
                } else {
                    repCountTextView.text = "Tiempo: 0 s"
                    isometricTotalTime = 0L
                    isResting = true
                    startRestPeriod()
                }
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    handleIsometricExercise(detectedPose)
                }, 1000)
            }
        } else {
            isometricStartTime = 0L
        }
    }

    private fun startGeneralTimer() {
        if (generalTimer != null) return // 🔥 Evita crear múltiples instancias

        generalTimer = Handler(Looper.getMainLooper())

        generalTimer?.post(object : Runnable {
            override fun run() {
                if (!routineEnded) { // 🔥 Permitir que el cronómetro se reanude correctamente
                    if (!isPaused) {
                        generalTimeMillis += 1000
                        val minutes = (generalTimeMillis / 1000) / 60
                        val seconds = (generalTimeMillis / 1000) % 60
                        generalTimerTextView.text = String.format("%02d:%02d", minutes, seconds)
                    }
                    generalTimer?.postDelayed(this, 1000) // 🔥 Sigue actualizando el cronómetro
                }
            }
        })
    }

    private fun startRestPeriod() {
        isResting = true
        val timerTextView = findViewById<TextView>(R.id.timerTextView)

        timerTextView.visibility = View.VISIBLE
        statusTextView.text = "Descanso..."

        val restDuration = 5 // 🔥 Duración en segundos
        val restHandler = Handler(Looper.getMainLooper())

        var remainingTime = restDuration

        restHandler.post(object : Runnable {
            override fun run() {
                if (remainingTime > 0) {
                    timerTextView.text = "Descanso: $remainingTime s"
                    remainingTime--
                    restHandler.postDelayed(this, 1000)
                } else {
                    isResting = false
                    timerTextView.visibility = View.GONE
                    setCountTextView.text = "Set: $currentSet/$maxSets"
                }
            }
        })
    }

    private fun advanceToNextExercise() {
        val token = userSessionManager?.getUserSession()?.token ?: ""
        lifecycleScope.launch {
            userSessionManager?.updateExerciseState(token)
            delay(1500)  // Pequeño delay para que el backend procese la actualización
            userSessionManager?.getMyExercises()
        }
        resetExerciseState()
    }

    private fun resetExerciseState() {
        // 🔄 Reiniciar variables
        repetitionCount = 0
        currentSet = 1
        routineStarted = false
        routineEnded = false
        isResting = false
        startPoseTime = 0L


        // 🔄 Resetear UI
        repCountTextView.text = "Reps: 0/$repsPerSet"
        setCountTextView.text = "Set: $currentSet/$maxSets"

        statusTextView.text = "Mantén 'x_pose' para comenzar"
        statusTextView.visibility = View.VISIBLE

        resetPoseLandmarker()
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
        generalHandler.removeCallbacksAndMessages(null) // Detener el cronómetro al cerrar la actividad
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onError(error: String, errorCode: Int) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    fun filterTrainedExercises(exercisesFromDB: List<String>): List<String> {
        return exercisesFromDB.filter { ModelConfig.isExerciseTrained(it) }
    }

    private fun showCounters() {
        repCountTextView.visibility = View.VISIBLE
        setCountTextView.visibility = View.VISIBLE

    }

    private fun hideCounters() {
        repCountTextView.visibility = View.INVISIBLE
        setCountTextView.visibility = View.INVISIBLE

    }

}