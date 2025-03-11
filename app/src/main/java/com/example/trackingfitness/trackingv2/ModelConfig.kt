package com.example.trackingfitness.trackingv2

object ModelConfig {
    var currentModel: String = "legs"

    val models = listOf("legs", "shoulders", "chest", "dips")
//    val models = listOf("legs", "shoulders", "chest", "dips", "leg-raises")
//    val models = listOf("legs", "crunches", "curl-bicep", "dead-rows", "v-ups", "flexibility", "glute-bridge", "hip-thrust", "bw-skullcrusher")

    private val modelFiles = mapOf(
        "legs" to "pose_classification_model_legs-mejorado2.tflite",
        "shoulders" to "pose_classification_model_shoulders2.tflite",
        "chest" to "pose_classification_model_chest.tflite",
        "dips" to "pose_classification_model_bench-dips.tflite",
//        "leg-raises" to "pose_classification_model_leg-raises.tflite",
//        "crunches" to "pose_classification_model_crunches.tflite",
//        "curl-bicep" to "pose_classification_model_biceps.tflite",
//        "dead-rows" to "pose_classification_model_dead-rows2.tflite",
//        "v-ups" to "pose_classification_model_v-ups.tflite",
//        "flexibility" to "pose_classification_model_flexibility.tflite",
//        "glute-bridge" to "pose_classification_model_glute-bridge.tflite",
//        "hip-thrust" to "pose_classification_model_hip-thrust.tflite",
//        "bw-skullcrusher" to "pose_classification_model_bw-skullcrusher.tflite"
    )

    private val labelFiles = mapOf(
        "legs" to "labels_legs.txt",
        "shoulders" to "labels_shoulders.txt",
        "chest" to "labels_chest.txt",
        "dips" to "labels_bench-dips.txt",
//        "leg-raises" to "labels_legs-raises.txt",
//        "crunches" to "labels_crunches.txt",
//        "curl-bicep" to "labels_biceps.txt",
//        "dead-rows" to "labels_dead-rows.txt",
//        "v-ups" to "labels_v-ups.txt",
//        "flexibility" to "labels_flexibility.txt",
//        "glute-bridge" to "labels_glute-bridge.txt",
//        "hip-thrust" to "labels_hip-thrust.txt",
//        "bw-skullcrusher" to "labels_bw-skullcrusher.txt"
    )

    private val exercisesByModel = mapOf(
        "legs" to listOf("squats", "lunges", "deadlifts", "wall-sit"),
        "shoulders" to listOf("shoulder_press", "lateral_raise"),
//        "chest" to listOf("shoulder_taps", "pushups", "diamond_pushups", "wide_pushups"),
        "chest" to listOf("shoulder_taps", "pushups"),
        "dips" to listOf("bench_dips"),
//        "leg-raises" to listOf("leg_raises"),
//        "crunches" to listOf("crunches"),
//        "curl-bicep" to listOf("curl_bicep"),
//        "dead-rows" to listOf("dead_rows"),
//        "v-ups" to listOf("v_ups"),
//        "flexibility" to listOf("cobra_stretch"),
//        "glute-bridge" to listOf("glute_bridge"),
//        "hip-thrust" to listOf("hip_thrust"),
//        "bw-skullcrusher" to listOf("bw_skullcrusher")
    )

    fun getModelFile(): String {
        return modelFiles[currentModel] ?: "pose_classification_model_legs-mejorado.tflite"
    }

    fun getLabelFile(): String {
        return labelFiles[currentModel] ?: "labels_legs2.txt"
    }

    fun getExercisesForModel(): List<String> {
        return exercisesByModel[currentModel] ?: listOf("squats")
    }

    // Función para verificar si un ejercicio está entrenado
    fun isExerciseTrained(exercise: String): Boolean {
        return exercisesByModel.values.flatten().contains(exercise)
    }
}
