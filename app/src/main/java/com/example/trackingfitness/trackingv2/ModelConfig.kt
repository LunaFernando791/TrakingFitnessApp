package com.example.trackingfitness.trackingv2

object ModelConfig {
    var currentModel: String = "legs"

    val models = listOf("legs", "tricep", "back",
        "abs", "biceps",
        "core","flexibility", "glutes",
        "harmstring", "hip-abductor","shoulders",
      )

    private val modelFiles = mapOf(
        "abs" to "pose_classification_model_abs.tflite",
        "back" to "pose_classification_model_back.tflite",
        "biceps" to "pose_classification_model_biceps.tflite",
        "chest" to "pose_classification_model_chest.tflite",
        "core" to "pose_classification_model_core.tflite",
        "flexibility" to "pose_classification_model_flexibility.tflite",
        "glutes" to "pose_classification_model_glutes.tflite",
        "harmstring" to "pose_classification_model_harmstring.tflite",
        "hip-abductor" to "pose_classification_model_hip-abductor.tflite",
        "legs" to "pose_classification_model_legs.tflite",
        "shoulders" to "pose_classification_model_shoulder-nopress.tflite",
        "tricep" to "pose_classification_model_tricep.tflite",
    )

    private val labelFiles = mapOf(
        "abs" to "labels_abs.txt",
        "back" to "labels_back.txt",
        "biceps" to "labels_biceps.txt",
        "chest" to "labels_chest.txt",
        "core" to "labels_core.txt",
        "flexibility" to "labels_flexibility.txt",
        "glutes" to "labels_glutes.txt",
        "harmstring" to "labels_harmstring.txt",
        "hip-abductor" to "labels_hip-abductor.txt",
        "legs" to "labels_legs.txt",
        "shoulders" to "labels_shoulder-nopress.txt",
        "tricep" to "labels_tricep.txt",
    )

    private val exercisesByModel = mapOf(
        "abs" to listOf("crunches", "leg-raises", "v-ups"),
        "back" to listOf("dead-row"),
        "biceps" to listOf("bicep-curl", "concentration-curl",),
        "chest" to listOf("push-ups", "archer-push-ups", "shoulder-taps"),
        "core" to listOf("plank_downward", "russian-twists", "plank"),
        "flexibility" to listOf("cobra-stretch"),
        "glutes" to listOf("glute-bridge", "hip-thrust", "good-morning"),
        "harmstring" to listOf("nordic-curl", "towel-harmstring"),
        "hip-abductor" to listOf("stand-hip-abductor", "lateral-leg-raises"),
        "legs" to listOf("squats", "lunges", "deadlifts", "wall-sit"),
        "shoulders" to listOf("lateral-raise", "pike-pushup", "rear-delt-fly", "snow-angels"),
        "tricep" to listOf("bench-dip","overhead-extension", "reverse-plank-dip", "tricep-kickback", "tricep-extension-floor", "wall-tricep-extension")
    )

    fun getModelFile(): String {
        return modelFiles[currentModel] ?: "pose_classification_model_legs.tflite"
    }

    fun getLabelFile(): String {
        return labelFiles[currentModel] ?: "labels_legs.txt"
    }

    fun getExercisesForModel(): List<String> {
        return exercisesByModel[currentModel] ?: listOf("squats")
    }

    // Función para verificar si un ejercicio está entrenado
    fun isExerciseTrained(exercise: String): Boolean {
        return exercisesByModel.values.flatten().contains(exercise)
    }
}
