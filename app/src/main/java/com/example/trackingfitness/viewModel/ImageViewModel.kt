package com.example.trackingfitness.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.ImageResponse
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.launch

class ImageViewModel() : ViewModel() {
    private val apiService: UserService = RetrofitInstance.api

    private val _images = MutableLiveData<List<ImageResponse>>()
    val images: LiveData<List<ImageResponse>> get() = _images

    fun fetchImages(
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.getImages()
                Log.d("ImageViewModel", "Response: $response")
                if (response.isSuccessful && response.body() != null) {
                    _images.value = response.body()
                } else {
                    Log.e("ImageViewModel", "Error fetching images: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ImageViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun onImageSelected(imageId: String, imageUrl: String) {
        Log.d("SelectedImage", "Imagen seleccionada: $imageId")
    }
}