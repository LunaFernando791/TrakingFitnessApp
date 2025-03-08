package com.example.trackingfitness.trackingv2

import java.util.*
import kotlin.collections.HashMap

//class EMASmoother(
//    private val windowSize: Int = 7,  // Reducir el tamaño de ventana para mayor reacción
//    private val alpha: Float = 0.3f,  // Hacer que se adapte más rápido a cambios
//    private val resetThreshold: Float = 0.2f  // Resetear más rápido tras pausas
//) {
//    private val window = ArrayDeque<FloatArray>(windowSize)
//    private var lastUpdateTime = System.currentTimeMillis()
//    private val classConfidences = HashMap<Int, Float>()
//
//    fun smooth(newValues: FloatArray): FloatArray {
//        val currentTime = System.currentTimeMillis()
//        if (currentTime - lastUpdateTime > resetThreshold * 1000) {
//            window.clear() // 🔥 Reinicia si la pausa es larga
//        }
//        lastUpdateTime = currentTime
//
//        if (window.size >= windowSize) {
//            window.removeFirst()
//        }
//        window.add(newValues.copyOf())
//
//        // 🔥 Aplicar suavizado exponencial a cada clase
//        for (i in newValues.indices) {
//            classConfidences[i] = alpha * newValues[i] + (1 - alpha) * (classConfidences[i] ?: newValues[i])
//        }
//
//        return classConfidences.values.toFloatArray()
//    }
//}

class EMASmoother(
    private val windowSize: Int = 7,
    private val alpha: Float = 0.3f,
    private val resetThreshold: Float = 0.2f // En segundos
) {
    private val window: LinkedList<FloatArray> = LinkedList()
    private var lastInputTime: Long = System.currentTimeMillis()
    private val classConfidences: MutableMap<Int, Float> = HashMap()

    fun getSmoothedResult(predictions: FloatArray): Map<Int, Float> {
        val currentTime = System.currentTimeMillis()

        // 🔹 Si ha pasado más tiempo del threshold, reseteamos la ventana
        if ((currentTime - lastInputTime) / 1000.0 > resetThreshold) {
            window.clear()
        }
        lastInputTime = currentTime

        // 🔹 Añadir predicciones a la ventana deslizante
        window.add(predictions.clone())
        if (window.size > windowSize) {
            window.removeFirst()
        }

        // 🔹 Aplicar EMA a cada clase
        for (idx in predictions.indices) {
            val previousConfidence = classConfidences.getOrDefault(idx, 0f)
            classConfidences[idx] = alpha * predictions[idx] + (1 - alpha) * previousConfidence
        }

        return classConfidences
    }
}
