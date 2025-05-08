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
import androidx.core.view.isVisible
import com.example.trackingfitness.conection.MyExercise
import com.example.trackingfitness.viewModel.MiniGamesViewModel

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
    private val generalHandler = Handler(Looper.getMainLooper())

    private var currentModel: String
        get() = ModelConfig.currentModel
        set(value) {
            ModelConfig.currentModel = value
        }

    private val models = ModelConfig.models

    private val exerciseTransitions = ExerciseTransitions.exerciseTransitions
    private val unilateralExercises = ExerciseTransitions.unilateralExercises

    private var currentExerciseInfo: MyExercise? = null

    private var repetitionCount = 0
    private var currentSet = 1
    private val maxSets = 3
    private val repsPerSet = 3

    private var routineStarted = false
    private var routineEnded = false
    private var isResting = true

    private var isPaused = false

    private var startPoseTime = 0L

    private var isometricStartTime: Long = 0L
    private val isometricHoldDuration = 5000L
    private var isometricTotalTime: Long = 0L

    private lateinit var stateOverlay: FrameLayout
    private lateinit var stateOverlayText: TextView

    private var finalTimerStarted = false
    private var restHandler: Handler? = null
    private var finalHandler: Handler? = null
    private var preRoutineHandler: Handler? = null

    private var mediaPlayer: MediaPlayer? = null

    private var isChallengeMode = false
    private var lastRepTimestamp: Long = 0L
    private var outOfPoseTime: Long = 0L

    private var isFirstIsometricStart = true

    private val exerciseDescriptions = mapOf(
        "push-ups" to "A basic push up exercise.",
        "wall-sit" to "Lean against a wall and slide down until your thighs are parallel to the floor. Hold the position for as long as possible.",
        "plank" to "Hold your body in a straight line, supporting yourself with your forearms and toes."
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_screen_v2)

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        backgroundExecutor = Executors.newSingleThreadExecutor()
        cameraHelper = CameraHelper(this, backgroundExecutor) { image -> detectPose(image) }

        poseClassTextView = findViewById(R.id.poseClassText)
        repCountTextView = findViewById(R.id.repetitionCount)
        setCountTextView = findViewById(R.id.setCount)
        generalTimerTextView = findViewById(R.id.generalTimer)
        statusTextView = findViewById(R.id.statusText)

        stateOverlay = findViewById(R.id.stateOverlay)
        stateOverlayText = findViewById(R.id.stateOverlayText)

        repCountTextView.visibility = View.INVISIBLE
        setCountTextView.visibility = View.INVISIBLE
        statusTextView.visibility = View.VISIBLE
        generalTimerTextView.visibility = View.INVISIBLE

        mediaPlayer = MediaPlayer.create(this, R.raw.beep)

        isChallengeMode = intent.getBooleanExtra("IS_CHALLENGE", false)
        val exerciseId = intent.getIntExtra("EXERCISE_ID",-1)

        if(!isChallengeMode){
            ModelConfig.currentModel = "legs"
            generalTimerTextView.visibility = View.INVISIBLE
        }

        if (isChallengeMode && exerciseId != -1) {
            // ID → Nombre
            val exerciseMap = mapOf(
                1 to "push-ups",
                2 to "wall-sit",
                3 to "plank"
            )
            val exerciseName = exerciseMap[exerciseId]

            exerciseName?.let { name ->
                selectedExercise = name

                // Mostrar nombre del ejercicio en la interfaz
                findViewById<TextView>(R.id.exerciseName).text = name.replace("-", " ").replaceFirstChar { it.uppercase() }

                // Buscar modelo correspondiente
                for ((model, exercises) in ModelConfig.exercisesByModel) {
                    if (name in exercises) {
                        ModelConfig.currentModel = model
                        currentModel = model
                        break
                    }
                }

                // Reiniciar landmarker con modelo correcto
                resetPoseLandmarker()

//                // Mostrar info
//                currentExerciseInfo = ExerciseInfo(
//                    exercise_name = name.replace("-", " ").replaceFirstChar { it.uppercase() },
//                    description = "Challenge Mode: $name"
//                )
            }
        }

        Log.d("ChallengeMode", isChallengeMode.toString())
        Log.d("ExerciseId", exerciseId.toString())

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

        initializePoseLandmarker()
        setupUI()
        if (isChallengeMode){
            startChallengeTimerChecker()
        }
    }


    private fun setupUI() {
        val token = userSessionManager?.getUserSession()?.token ?: ""

        if(!isChallengeMode){
            lifecycleScope.launch {
                userSessionManager?.getMyExercises()

                userSessionManager?.myExercises
                    ?.drop(1)         // Ignora la primera emisi贸n (valor inicial vac铆o)
                    ?.collect { myExerciseResponse ->
                        val exercises = myExerciseResponse?.selectedExercises

                        val currentExercise = exercises?.firstOrNull { it.status == "actual" }

                        currentExerciseInfo = currentExercise

                        if (currentExercise != null) {
                            userSessionManager?.showExercise(token, currentExercise.exercise_id) { exercise ->
                                runOnUiThread {
                                    findViewById<TextView>(R.id.exerciseName).text = "${exercise.name}"
                                    repetitionCount = 0
                                    findViewById<TextView>(R.id.repetitionCount).text = "Reps: 0/$repsPerSet"
//                                selectedExercise = exercise.name.lowercase()
//                                resetPoseLandmarker() // 🔥 Forzar actualización del modelo
                                }
                                Log.d("CameraScreenV2", "Ejercicio cargado correctamente: ${exercise.name}")
                            }
                        } else {
                            Log.d("CameraScreenV2", "No se encontr贸 ejercicio actual.")
                            navigateToExerciseListScreen()
                        }
                    }
            }
            val defaultExercises = getExercisesForCurrentModel()
            if (defaultExercises.isNotEmpty()) {
                selectedExercise = defaultExercises.first()
                repetitionCount = 0
                repCountTextView.text = "Reps: 0/$repsPerSet"
                Log.d("SetupUI", "Ejercicio inicial (por defecto): $selectedExercise")
            }
        }

        setupDropdownMenu()

        if(selectedExercise in ExerciseTransitions.isometricExercises){
            repCountTextView.text = "Time: 0s"
        }
    }

    private fun navigateToExerciseListScreen() {
        runOnUiThread {
            Toast.makeText(this@CameraScreenV2, "Routine Completed", Toast.LENGTH_SHORT).show()
            val intent = Intent().apply {
                putExtra("navigateTo", "exerciseListScreen")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setupDropdownMenu() {
        val fabMenu: FloatingActionButton = findViewById(R.id.fabMenu)

        fabMenu.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
            bottomSheetDialog.setContentView(view)

            val titleText = view.findViewById<TextView>(R.id.exerciseTitle)
            val descriptionText = view.findViewById<TextView>(R.id.exerciseDescription)

//            currentExerciseInfo?.let {
//                titleText.text = it.exercise_name
//                descriptionText.text = it.description
//            }
            val displayName = selectedExercise.replace("-", " ").replaceFirstChar { it.uppercase() }

            if (!isChallengeMode && currentExerciseInfo != null) {
                titleText.text = currentExerciseInfo?.exercise_name ?: displayName
                descriptionText.text = currentExerciseInfo?.description ?: "No description"
            } else {
                titleText.text = displayName
                descriptionText.text = exerciseDescriptions[selectedExercise] ?: "No description"
            }

            val btnSwitchCamera = view.findViewById<ImageButton>(R.id.btnSwitchCamera)
            val btnRotateCamera = view.findViewById<ImageButton>(R.id.btnRotateCamera)
            val btnPauseRoutine = view.findViewById<ImageButton>(R.id.btnPauseRoutine)
            val btnNextExercise = view.findViewById<ImageButton>(R.id.btnNextExercise)

            if (!isChallengeMode && currentSet > maxSets) {
                btnNextExercise.isEnabled = true
                hideCounters()
            } else {
                btnNextExercise.visibility = View.GONE
            }

            btnSwitchCamera.setOnClickListener {
                cameraHelper.toggleCamera()
                bottomSheetDialog.dismiss()
            }

            btnRotateCamera.setOnClickListener {
                cameraHelper.rotateCamera(this)
                bottomSheetDialog.dismiss()
            }

            btnPauseRoutine.setOnClickListener {
                if(!isChallengeMode){
                    val intent = Intent().apply {
                        putExtra("navigateTo", "exerciseListScreen")
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else{
                    val intent = Intent().apply {
                        putExtra("navigateTo", "minigamesScreen")
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                bottomSheetDialog.dismiss()
            }

            btnNextExercise.setOnClickListener {
                advanceToNextExercise()
                bottomSheetDialog.dismiss()
            }

            val modelSpinner = view.findViewById<Spinner>(R.id.modelSpinner)
            val exerciseSpinner = view.findViewById<Spinner>(R.id.exerciseSpinner)

            val modelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, models)
            modelSpinner.adapter = modelAdapter

            val currentModelIndex = models.indexOf(currentModel)
            if (currentModelIndex != -1) modelSpinner.setSelection(currentModelIndex)

            var currentExercises = getExercisesForCurrentModel()
            val exerciseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currentExercises)
            exerciseSpinner.adapter = exerciseAdapter

            val currentExerciseIndex = currentExercises.indexOf(selectedExercise)
            if (currentExerciseIndex != -1) exerciseSpinner.setSelection(currentExerciseIndex)

            modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedModel = models[position]
                    if (selectedModel != currentModel) {
                        currentModel = selectedModel
                        resetPoseLandmarker()
                    }

                    currentExercises = getExercisesForCurrentModel()
                    val updatedExerciseAdapter = ArrayAdapter(this@CameraScreenV2, android.R.layout.simple_spinner_item, currentExercises)
                    exerciseSpinner.adapter = updatedExerciseAdapter

                    val index = currentExercises.indexOf(selectedExercise)
                    if (index != -1) {
                        exerciseSpinner.setSelection(index)
                    } else {
                        selectedExercise = currentExercises.firstOrNull() ?: ""
                        exerciseSpinner.setSelection(0)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            exerciseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedExercise = currentExercises.getOrNull(position) ?: ""
                    repetitionCount = 0
                    repCountTextView.text = if(!isChallengeMode) "Reps: 0/$repsPerSet" else "Reps: 0"
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
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
            currentModel = PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_FULL,
            modelFile = modelFile,
            labelFile = labelFile,
            poseLandmarkerHelperListener = this,
            routineStarted = routineStarted
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

            poseLandmarkerHelper.routineStarted = routineStarted

            if (resultBundle.results.isEmpty()) {
                if (isChallengeMode && routineStarted && !routineEnded) {
                    findViewById<OverlayView>(R.id.overlay).invalidate()
                }
                findViewById<OverlayView>(R.id.overlay).clearResults()
                return@runOnUiThread
            }

            val poseClass = resultBundle.poseClass
            val overlayView = findViewById<OverlayView>(R.id.overlay)

            poseClassTextView.text = poseClass

            overlayView.setResults(
                resultBundle.results.first(),
                poseClass,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM,
                selectedExercise
            )

            if (routineEnded) {
                hideCounters()
                overlayView.clear()
                return@runOnUiThread
            }

            if (!routineStarted) {
                handlePreRoutine(poseClass)
                return@runOnUiThread
            }

            if (isResting) {
                overlayView.clear()
                hideCounters()
                return@runOnUiThread
            }

            showCounters()

            if (selectedExercise in ExerciseTransitions.isometricExercises) {
                if (isChallengeMode) {
                    handleIsometricChallenge(poseClass)
                } else {
                    handleIsometricExercise(poseClass)
                }
            } else {
                if (isChallengeMode) {
                    handleRepetitiveChallenge(poseClass)
                } else {
                    handleRepetitiveExercise(poseClass)
                }
            }

            overlayView.invalidate()
        }
    }

    private fun handlePreRoutine(poseClass: String) {
        if (poseClass == "x_pose") {
            if (startPoseTime == 0L) {
                startPoseTime = System.currentTimeMillis()
            }

            val elapsedTime = System.currentTimeMillis() - startPoseTime
            showOverlayMessage("inicio", 3 - (elapsedTime / 1000).toInt())

            if (elapsedTime >= 3000) {
                isResting = false
                routineStarted = true
//                if(isChallengeMode && selectedExercise in ExerciseTransitions.isometricExercises){
                if(isChallengeMode){
                    generalTimerTextView.visibility = View.VISIBLE  // ✅ ahora sí
                    generalTimerTextView.text = "Get ready!"
                }
                hideOverlayMessage()
                showCounters()

                preRoutineHandler = Handler(Looper.getMainLooper()).apply {
                    postDelayed({
                        statusTextView.visibility = View.INVISIBLE
                    }, 1000)
                }
            }
        } else {
            startPoseTime = 0L
            hideOverlayMessage()
            statusTextView.text = "Hold 'x_pose' to start"

            preRoutineHandler?.removeCallbacksAndMessages(null)
            preRoutineHandler = null
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
                mediaPlayer?.start()
                repetitionCount++
                lastRepTimestamp = System.currentTimeMillis()  // ⏱️ ← aquí marcamos el tiempo
                repCountTextView.text = "Reps: $repetitionCount/$repsPerSet"

                if (repetitionCount >= repsPerSet) {
                    repetitionCount = 0
                    currentSet++

                    if (currentSet > maxSets) {
                        routineEnded = true
                        statusTextView.text = "Exercise finished!"
                        startFinalExerciseTimer()
                    } else {
                        setCountTextView.text = "Set: $currentSet/$maxSets"
                        startRestPeriod()
                    }
                }
            }
        }
    }

    private fun handleRepetitiveChallenge(detectedPose: String) {
        val exerciseFilter = exerciseTransitions[selectedExercise] ?: return

        if (detectedPose !in exerciseFilter.values) return

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
            mediaPlayer?.start()
            repetitionCount++
            lastRepTimestamp = System.currentTimeMillis()
            repCountTextView.text = "Reps: $repetitionCount"
        }

        if (lastRepTimestamp != 0L) {
            val elapsed = System.currentTimeMillis() - lastRepTimestamp
            if (elapsed > 7000) {
                routineEnded = true
                statusTextView.text = "Minigame finished!"
                startFinalExerciseTimer()
                hideCountersChallenge()
                lastRepTimestamp = 0L
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
            isometricTotalTime += 1

            repCountTextView.visibility = View.VISIBLE
            repCountTextView.text = "Time: $elapsedTime s"

            if (elapsedTime >= isometricHoldDuration / 1000) {
                mediaPlayer?.start()
                currentSet++

                setCountTextView.text = "Set: $currentSet/$maxSets"

                isometricStartTime = 0L

                if (currentSet > maxSets) {
                    routineEnded = true
                    statusTextView.text = "Exercise finished!"
                    startFinalExerciseTimer()
                    isometricTotalTime = 0L
                } else {
                    repCountTextView.text = "Time: 0 s"
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

//    private fun handleIsometricChallenge(detectedPose: String) {
//        val exerciseFilter = exerciseTransitions[selectedExercise]
//        val isometricPose = exerciseFilter?.get("position") ?: selectedExercise
//
//        if (!routineStarted || routineEnded || isPaused || isResting) return
//
//        if (detectedPose == isometricPose) {
//            outOfPoseTime = 0L
//            if (isometricStartTime == 0L) {
//                isometricStartTime = System.currentTimeMillis()
//                generalTimerTextView.text = "⏱️ 04 s"
//            }
//
//            val elapsedTime = ((System.currentTimeMillis() - isometricStartTime) + isometricTotalTime) / 1000
//            repCountTextView.text = "Tiempo: $elapsedTime s"
//
//        } else {
//            if (isometricStartTime != 0L) {
//                isometricTotalTime += (System.currentTimeMillis() - isometricStartTime)
//                isometricStartTime = 0L
//            }
//
//            if (outOfPoseTime == 0L) {
//                outOfPoseTime = System.currentTimeMillis()
//            } else {
//                val elapsed = System.currentTimeMillis() - outOfPoseTime
//                if (elapsed > 4000) {
//                    routineEnded = true
//                    statusTextView.text = "¡Minijuego terminado!"
//                    startFinalExerciseTimer()
//                    outOfPoseTime = 0L
//                }
//            }
//        }
//    }
    private fun handleIsometricChallenge(detectedPose: String) {
        val exerciseFilter = exerciseTransitions[selectedExercise]
        val isometricPose = exerciseFilter?.get("position") ?: selectedExercise

        if (!routineStarted || routineEnded || isPaused || isResting) return

        if (detectedPose == isometricPose) {
            outOfPoseTime = 0L
            if (isometricStartTime == 0L) {
                isometricStartTime = System.currentTimeMillis()
                generalTimerTextView.text = "⏱️ 04 s"
            }

            val elapsedTime = ((System.currentTimeMillis() - isometricStartTime) + isometricTotalTime) / 1000
            repCountTextView.text = "Time: $elapsedTime s"

            if (isFirstIsometricStart) {
                isFirstIsometricStart = false
            }

        } else {
            if (isometricStartTime != 0L) {
                isometricTotalTime += (System.currentTimeMillis() - isometricStartTime)
                isometricStartTime = 0L
            }

            if (outOfPoseTime == 0L) {
                outOfPoseTime = System.currentTimeMillis()
            } else {
                val elapsed = System.currentTimeMillis() - outOfPoseTime
                val outOfPoseThreshold = if (isFirstIsometricStart) 8000L else 4000L

                val remaining = ((outOfPoseThreshold - elapsed) / 1000).coerceAtLeast(0)
                generalTimerTextView.text = String.format("⏱️ %02d s", remaining)

                if (elapsed > outOfPoseThreshold) {
                    routineEnded = true
                    statusTextView.text = "Minigame finished!"
                    startFinalExerciseTimer()
                    outOfPoseTime = 0L
                    generalTimerTextView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun startChallengeTimerChecker() {
        val checkerHandler = Handler(Looper.getMainLooper())

        checkerHandler.post(object : Runnable {
            override fun run() {
                if (isChallengeMode && routineStarted && !routineEnded) {
                    val now = System.currentTimeMillis()

                    // 🏋️ Reto de repeticiones
                    if (selectedExercise !in ExerciseTransitions.isometricExercises && lastRepTimestamp != 0L) {
                        val elapsed = now - lastRepTimestamp
                        if (elapsed > 7000) {
                            routineEnded = true
                            statusTextView.text = "Minigame finished!"
                            startFinalExerciseTimer()
                            lastRepTimestamp = 0L
                            generalTimerTextView.text = "⏱️ 07 s"
                            generalTimerTextView.visibility = View.INVISIBLE
                        } else {
                            val remaining = 7000 - elapsed
                            generalTimerTextView.text = String.format("⏱️ %02d s", (remaining / 1000))
                            generalTimerTextView.visibility = View.VISIBLE
                        }
                    }

                    // 🧘 Reto isométrico
                    if (selectedExercise in ExerciseTransitions.isometricExercises && outOfPoseTime != 0L) {
                        val elapsed = now - outOfPoseTime
                        val outOfPoseThreshold = if (isFirstIsometricStart) 8000L else 4000L

                        val remaining = ((outOfPoseThreshold - elapsed) / 1000).coerceAtLeast(0)
                        generalTimerTextView.text = String.format("⏱️ %02d s", remaining)
                        generalTimerTextView.visibility = View.VISIBLE

                        if (elapsed > outOfPoseThreshold) {
                            routineEnded = true
                            statusTextView.text = "Minigame finished!"
                            startFinalExerciseTimer()
                            outOfPoseTime = 0L
                            generalTimerTextView.text = "00:00"
                            generalTimerTextView.visibility = View.INVISIBLE
                        }
                    }

//                    // 🧘 Reto isométrico
//                    if (selectedExercise in ExerciseTransitions.isometricExercises && outOfPoseTime != 0L) {
//                        val elapsed = now - outOfPoseTime
//                        if (elapsed > 4000) {
//                            routineEnded = true
//                            statusTextView.text = "¡Minijuego terminado!"
//                            startFinalExerciseTimer()
//                            outOfPoseTime = 0L
//                            generalTimerTextView.text = "00:00"
//                            generalTimerTextView.visibility = View.VISIBLE
//                        } else {
//                            val remaining = 4000 - elapsed
//                            generalTimerTextView.text = String.format("⏱️ %02d s", (remaining / 1000))
//                            generalTimerTextView.visibility = View.VISIBLE
//                        }
//                    }
                }

                checkerHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun startRestPeriod() {
        if (isChallengeMode) return  // ← omitir descanso

        isResting = true
        restHandler?.removeCallbacksAndMessages(null)
        restHandler = Handler(Looper.getMainLooper())

        val restDuration = 5 // segundos
        var remainingTime = restDuration

        restHandler?.post(object : Runnable {
            override fun run() {
                if (remainingTime > 0) {
                    showOverlayMessage("descanso", remainingTime, setActual = currentSet - 1, totalSets = maxSets)
                    remainingTime--
                    restHandler?.postDelayed(this, 1000)
                } else {
                    isResting = false
                    repCountTextView.text = "Reps: 0/$repsPerSet"
                    setCountTextView.text = "Set: $currentSet/$maxSets"
                    hideOverlayMessage()
                }
            }
        })
    }

    private fun startFinalExerciseTimer() {
        if (finalTimerStarted) return
        finalTimerStarted = true

        if (isChallengeMode) {
            hideCountersChallenge()
            showOverlayMessage("final_reto")
            return
        }

        finalHandler?.removeCallbacksAndMessages(null)
        finalHandler = Handler(Looper.getMainLooper())

        val finalDuration = 180
        var remainingTime = finalDuration

        finalHandler?.post(object : Runnable {
            override fun run() {
                if (remainingTime > 0) {
                    showOverlayMessage("final", remainingTime)
                    remainingTime--
                    finalHandler?.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun advanceToNextExercise() {
        finalTimerStarted = false
        finalHandler?.removeCallbacksAndMessages(null)
        restHandler?.removeCallbacksAndMessages(null)

        val token = userSessionManager?.getUserSession()?.token ?: ""
        lifecycleScope.launch {
            userSessionManager?.updateExerciseState(token)
            delay(1500)
            userSessionManager?.getMyExercises()
        }
        hideOverlayMessage()
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

        repCountTextView.text = "Reps: 0/$repsPerSet"
        setCountTextView.text = "Set: $currentSet/$maxSets"

        statusTextView.text = "Hold 'x_pose' to start"
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

    private fun showCounters() {
        repCountTextView.visibility = View.VISIBLE
        if(!isChallengeMode){
            setCountTextView.visibility = View.VISIBLE
        }
    }

    private fun hideCounters() {
        repCountTextView.visibility = View.INVISIBLE
        setCountTextView.visibility = View.INVISIBLE
    }

    private fun hideCountersChallenge(){
        repCountTextView.visibility = View.INVISIBLE
        setCountTextView.visibility = View.INVISIBLE
    }

    private fun hideOverlayMessage() {
            stateOverlay.visibility = View.GONE
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent().apply {
//            putExtra("navigateTo", if (isChallengeMode) "minigamesScreen" else "exerciseListScreen")
//        }
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        setResult(RESULT_OK, intent)
//        finish()
//    }


    private fun showOverlayMessage(
        estado: String,
        tiempo: Int = 0,
        setActual: Int = 0,
        totalSets: Int = 0
    ) {

        val overlayLayout = findViewById<LinearLayout>(R.id.stateOverlayLayout)
        val overlayText = findViewById<TextView>(R.id.stateOverlayText)
        val overlayTimer = findViewById<TextView>(R.id.overlayTimer)
        val skipButton = findViewById<Button>(R.id.skipTimerButton)

        val minutos = tiempo / 60
        val segundos = tiempo % 60
        val tiempoFormateado = String.format("%02d:%02d", minutos, segundos)

        when (estado) {
            "inicio" -> {
                overlayText.text = if (isChallengeMode) "Starting challenge!" else "Starting routine!"
                overlayTimer.text = tiempoFormateado
                skipButton.visibility = View.GONE
            }

            "descanso" -> {
                overlayText.text = "¡Set $setActual of $totalSets completed!"
                overlayTimer.text = tiempoFormateado
                skipButton.text = "Skip timer"
                skipButton.visibility = View.VISIBLE
                skipButton.setOnClickListener {
                    restHandler?.removeCallbacksAndMessages(null)
                    isResting = false
                    hideOverlayMessage()
                    setCountTextView.text = "Set: $currentSet/$maxSets"
                }
            }

            "final" -> {
                overlayText.text = "Exercise completed!"
                overlayTimer.text = tiempoFormateado
                skipButton.text = "Next exercise"
                skipButton.visibility = View.VISIBLE
                skipButton.setOnClickListener {
                    advanceToNextExercise()
                }
            }

            "final_reto" -> {
                val summaryRow = findViewById<LinearLayout>(R.id.summaryOverlayRow)
                val exerciseNameText = findViewById<TextView>(R.id.overlayExerciseName)
                val exerciseTotalText = findViewById<TextView>(R.id.overlayExerciseTotal)

                summaryRow.visibility = View.VISIBLE

                exerciseNameText.text = "Exercise: ${selectedExercise.replace("-", " ").replaceFirstChar { it.uppercase() }}"
                exerciseTotalText.text = if (selectedExercise in ExerciseTransitions.isometricExercises) {
                    "Total: ${(isometricTotalTime / 1000)} seconds"
                } else {
                    "Total: $repetitionCount repetitions"
                }

                val aliasInput = findViewById<EditText>(R.id.aliasInput)
                val phoneInput = findViewById<EditText>(R.id.phoneInput)
                overlayTimer.visibility = View.GONE
                overlayText.text = "Challenge finished!\n\nPlease enter your data"
                skipButton.text = "Submit data"
                skipButton.visibility = View.VISIBLE
                aliasInput.visibility = View.VISIBLE
                phoneInput.visibility = View.VISIBLE

                skipButton.setOnClickListener {
                    val inputAlias = aliasInput.text.toString().trim()
                    val inputPhone = "+52" + phoneInput.text.toString().trim().removePrefix("+52")

                    if (inputAlias.isEmpty() || inputPhone.isEmpty()) {
                        Toast.makeText(this@CameraScreenV2, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val token = userSessionManager?.getUserSession()?.token ?: ""
                    val exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
                    val score = if (selectedExercise in ExerciseTransitions.isometricExercises) {
                        (isometricTotalTime / 1000).toInt()
                    } else {
                        repetitionCount
                    }

                    if (token.isNotEmpty() && exerciseId != -1) {
//                        Log.d("MiniGameSubmission", "Alias: $inputAlias")
//                        Log.d("MiniGameSubmission", "Teléfono: $inputPhone")
//                        Log.d("MiniGameSubmission", "Exercise ID: $exerciseId")
//                        Log.d("MiniGameSubmission", "Score: $score")
                        val viewModel = MiniGamesViewModel(application)
                        viewModel.updateMinigameScore(token, inputPhone, exerciseId, score, inputAlias)
                    }

                    val intent = Intent().apply {
                        putExtra("navigateTo", "minigamesScreen")
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
        stateOverlay.visibility = View.VISIBLE
    }
}