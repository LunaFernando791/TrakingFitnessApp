package com.example.trackingfitness.trackingv2

object ExerciseTransitions {
    val exerciseTransitions = mapOf(
        // ABS
        "crunches" to mapOf("start" to "crunch_start", "end" to "crunch_end"),
        "leg-raises" to mapOf("start" to "leg_raise_start", "end" to "leg_raise_end"),
        "v-ups" to mapOf("start" to "v_up_start", "end" to "v_up_end"),

        // BACK
        "dead-row" to mapOf("start" to "dead_row_start", "end" to "dead_row_end"),

        // BICEPS
        "bicep-curl" to mapOf("start" to "bicep_curl_start", "end" to "bicep_curl_end"),
        "concentration-curl" to mapOf(
            "start" to "concentration-curl_start",
            "left_end" to "concentration-curl_left_end",
            "right_end" to "concentration-curl_right_end"
        ),

        // CHEST
        "push-ups" to mapOf("start" to "pushups_up", "end" to "pushups_down"),
        "archer-push-ups" to mapOf(
            "left_end" to "archer-pushup-left_end",
            "right_end" to "archer-pushup-right_end",
            "start" to "pushups_up"
        ),
        "shoulder-taps" to mapOf(
            "left_end" to "shoulder_tap_left",
            "right_end" to "shoulder_tap_right",
            "start" to "pushups_up"
        ),

        // CORE
        "plank_downward" to mapOf("start" to "plank_downward_start", "end" to "plank_downward_end"),
        "russian-twists" to mapOf(
            "start" to "russian-twists_start",
            "left_end" to "russian-twists-left_end",
            "right_end" to "russian-twists-right_end"
        ),
        "plank" to mapOf("position" to "plank"),

        // FLEXIBILITY
        "cobra-stretch" to mapOf("start" to "cobra_stretch_start", "end" to "cobra_stretch_end"),

        // GLUTES
        "glute-bridge" to mapOf("start" to "glute_bridge_start", "end" to "glute_bridge_end"),
        "hip-thrust" to mapOf("start" to "hip_thrust_start", "end" to "hip_thrust_end"),
        "good-morning" to mapOf("start" to "good_morning_start", "end" to "good_morning_end"),

        // HARMSTRING
        "nordic-curl" to mapOf("start" to "nordic_curl_start", "end" to "nordic_curl_end"),
        "towel-hamstring" to mapOf("start" to "towel_harmstring_start", "end" to "towel_harmstring_end"),

        // HIP ABDUCTOR
        "lateral-leg-raises" to mapOf(
            "start" to "lateral_leg_raise_start",
            "left_end" to "lateral_legraise_left_end",
            "right_end" to "lateral_legraise_right_end"
        ),
        "stand-hip-abductor" to mapOf(
            "start" to "stand-hipabduction_start",
            "left_end" to "stand-hipabduction_left_end",
            "right_end" to "stand-hipabduction_right_end"
        ),

        // LEGS
        "squats" to mapOf("start" to "start", "end" to "squats_end"),
        "lunges" to mapOf(
            "start" to "start",
            "left_end" to "lunge_left_down",
            "right_end" to "lunge_right_down"
        ),
        "deadlifts" to mapOf("start" to "start", "end" to "deadlift_end"),
        "wall-sit" to mapOf("position" to "squats_end"),

        // SHOULDERS
        "lateral-raise" to mapOf("start" to "lateral_raise_start", "end" to "lateral_raise_end"),
        "pike-pushup" to mapOf("start" to "pike-pushup_start", "end" to "pike-pushup_end"),
        "rear-delt-fly" to mapOf("start" to "rear-delt-fly_start", "end" to "rear-delt-fly_end"),
        "snow-angels" to mapOf("start" to "snow-angels_start", "end" to "snow-angels_end"),

        // TRICEPS
        "bench-dip" to mapOf("start" to "bench_dip_start", "end" to "bench_dip_end"),
        "overhead-extension" to mapOf("start" to "overhead-extension_start", "end" to "overhead-extension_end"),
        "reverse-plank-dip" to mapOf("start" to "reverse-plank-dip_start", "end" to "reverse-plank-dip_end"),
        "tricep-kickback" to mapOf("start" to "tricep-kickback_start", "end" to "tricep-kickback_end"),
        "tricep-extension-floor" to mapOf("start" to "tricep_extension_floor_start", "end" to "tricep_extension_floor_end"),
        "wall-tricep-extension" to mapOf("start" to "wall-tricep-extension_start", "end" to "wall-tricep-extension_end")
    )

    val unilateralExercises = setOf(
        "lunges",
        "archer-push-ups",
        "shoulder-taps",
        "concentration-curl",
        "russian-twists",
        "lateral-leg-raises",
        "stand-hip-abductor"
    )

    val isometricExercises = setOf(
        "plank_downward",
        "wall-sit",
        "cobra-stretch",
        "plank"
    )
}
