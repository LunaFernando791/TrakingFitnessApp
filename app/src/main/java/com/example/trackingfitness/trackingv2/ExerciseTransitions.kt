package com.example.trackingfitness.trackingv2

object ExerciseTransitions {
    val exerciseTransitions = mapOf(
        "lunges" to mapOf("start" to "start", "left_end" to "lunge_left_end", "right_end" to "lunge_right_end"),
        "squats" to mapOf("start" to "start", "end" to "squats_end"),
        "deadlifts" to mapOf("start" to "start", "end" to "deadlift_end"),
        "pushups" to mapOf("start" to "pushup_up", "end" to "pushup_down"),
        "shoulder_press" to mapOf("start" to "shoulder_press_start", "end" to "shoulder_press_end"),
        "lateral_raise" to mapOf("start" to "lateral_raise_start", "end" to "lateral_raise_end"),
//        "bench_dips" to mapOf("start" to "bench_dip_start", "end" to "bench_dip_end"),
//        "leg_raises" to mapOf("start" to "leg_raise_start", "end" to "leg_raise_end"),
//        "crunches" to mapOf("start" to "crunch_start", "end" to "crunch_end")
    )

    val unilateralExercises = setOf("lunges")
}