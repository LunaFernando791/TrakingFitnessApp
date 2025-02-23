/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.trackingfitness.tracking.posedetector.classification;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.WorkerThread;

import com.google.common.base.Preconditions;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PoseClassifierProcessor {
  private static final String TAG = "PoseClassifierProcessor";
  private static final String MODEL_PATH = "model_names/pose_classification_model.tflite";

  private Interpreter tfliteInterpreter;
  private Map<String, String> currentExerciseTransitions;
  public String selectedExercise = "pushups"; // Ejercicio inicial por defecto
  private int repetitionCount = 0; // Contador de repeticiones
  private String lastState = null; // Estado previo del ejercicio
  private String currentState = null; // Estado actual del ejercicio

  public PoseClassifierProcessor(Context context, boolean isStreamMode) {
    loadTFLiteModel(context);
    setSelectedExercise(selectedExercise);
  }

  private void loadTFLiteModel(Context context) {
    try {
      tfliteInterpreter = new Interpreter(FileUtil.loadMappedFile(context, MODEL_PATH));
    } catch (IOException e) {
      Log.e(TAG, "Error loading TensorFlow Lite model", e);
    }
  }

  public void setSelectedExercise(String exercise) {
    selectedExercise = exercise;
    switch (exercise) {
      case "pushups":
        currentExerciseTransitions = Map.of("up", "pushups_up", "down", "pushups_down");
        break;
      case "squats":
        currentExerciseTransitions = Map.of("up", "squats_up", "down", "squats_down");
        break;
      default:
        throw new IllegalArgumentException("Ejercicio no soportado: " + exercise);
    }
    Log.d(TAG, "Ejercicio seleccionado: " + selectedExercise);
  }

  public List<String> getPoseResult(Pose pose, int deviceRotation) {
    float[][] landmarks = extractLandmarks(pose, deviceRotation);
    if (landmarks == null) {
      return List.of("Sin resultados");
    }

    float[][] output = new float[1][4];
    tfliteInterpreter.run(landmarks, output);

    int maxIndex = 0;
    for (int i = 1; i < output[0].length; i++) {
      if (output[0][i] > output[0][maxIndex]) {
        maxIndex = i;
      }
    }

    String predictedClass = getClassLabel(maxIndex);
    Log.d(TAG, "Clase predicha: " + predictedClass);

    if (currentExerciseTransitions.containsValue(predictedClass)) {
      lastState = currentState;
      currentState = predictedClass;

      if (lastState != null &&
              lastState.equals(currentExerciseTransitions.get("down")) &&
              currentState.equals(currentExerciseTransitions.get("up"))) {
        repetitionCount++;
        Log.d(TAG, "Repetición contada: " + repetitionCount);
      }
    }

    return List.of(predictedClass);
  }

  public int getRepetitionCount() {
    return repetitionCount;
  }

  private float[][] extractLandmarks(Pose pose, int deviceRotation) {
    if (pose == null || pose.getAllPoseLandmarks().isEmpty()) {
      return null;
    }

    float[] landmarks = new float[99]; // 33 landmarks * 3 coordenadas
    List<PoseLandmark> poseLandmarks = pose.getAllPoseLandmarks();

    for (int i = 0; i < poseLandmarks.size(); i++) {
      PoseLandmark landmark = poseLandmarks.get(i);
      landmarks[i * 3] = landmark.getPosition3D().getX();
      landmarks[i * 3 + 1] = landmark.getPosition3D().getY();
      landmarks[i * 3 + 2] = landmark.getPosition3D().getZ();
    }
    return normalizeLandmarks(adjustForDeviceRotation(landmarks, deviceRotation));
  }

  private float[] adjustForDeviceRotation(float[] landmarks, int rotation) {
    float[] adjustedLandmarks = landmarks.clone();
    for (int i = 0; i < 33; i++) {
      float x = adjustedLandmarks[i * 3];
      float y = adjustedLandmarks[i * 3 + 1];
      float z = adjustedLandmarks[i * 3 + 2]; // Incluye z para consistencia

      switch (rotation) {
        case Surface.ROTATION_90:
          adjustedLandmarks[i * 3] = -y;
          adjustedLandmarks[i * 3 + 1] = x;
          adjustedLandmarks[i * 3 + 2] = z; // z no rota
          break;
        case Surface.ROTATION_180:
          adjustedLandmarks[i * 3] = -x;
          adjustedLandmarks[i * 3 + 1] = -y;
          adjustedLandmarks[i * 3 + 2] = z;
          break;
        case Surface.ROTATION_270:
          adjustedLandmarks[i * 3] = y;
          adjustedLandmarks[i * 3 + 1] = -x;
          adjustedLandmarks[i * 3 + 2] = z;
          break;
      }
    }
    return adjustedLandmarks;
  }


  private float[][] normalizeLandmarks(float[] landmarks) {
    float[][] reshaped = new float[33][3];
    for (int i = 0; i < 33; i++) {
      reshaped[i][0] = landmarks[i * 3];
      reshaped[i][1] = landmarks[i * 3 + 1];
      reshaped[i][2] = landmarks[i * 3 + 2];
    }

    // Calcular el centro de las caderas
    float[] leftHip = reshaped[23];
    float[] rightHip = reshaped[24];
    float[] center = {(leftHip[0] + rightHip[0]) / 2, (leftHip[1] + rightHip[1]) / 2, (leftHip[2] + rightHip[2]) / 2};

    // Ajustar landmarks al centro
    for (int i = 0; i < reshaped.length; i++) {
      reshaped[i][0] -= center[0];
      reshaped[i][1] -= center[1];
      reshaped[i][2] -= center[2];
    }

    // Calcular tamaño del torso
    float[] leftShoulder = reshaped[11];
    float[] rightShoulder = reshaped[12];
    float torsoSize = (float) Math.sqrt(Math.pow((leftShoulder[0] + rightShoulder[0]) / 2 - center[0], 2) +
            Math.pow((leftShoulder[1] + rightShoulder[1]) / 2 - center[1], 2) +
            Math.pow((leftShoulder[2] + rightShoulder[2]) / 2 - center[2], 2));

    // Escalar landmarks por el tamaño del torso
    for (int i = 0; i < reshaped.length; i++) {
      reshaped[i][0] /= torsoSize;
      reshaped[i][1] /= torsoSize;
      reshaped[i][2] /= torsoSize;
    }

    // Aplanar de nuevo para cumplir con el formato de entrada del modelo
    float[][] normalized = new float[1][99];
    for (int i = 0; i < 33; i++) {
      normalized[0][i * 3] = reshaped[i][0];
      normalized[0][i * 3 + 1] = reshaped[i][1];
      normalized[0][i * 3 + 2] = reshaped[i][2];
    }
    return normalized;
  }


  private String getClassLabel(int index) {
    String[] classLabels = {"pushups_down", "pushups_up", "squats_down", "squats_up"};
    return classLabels[index];
  }
}
