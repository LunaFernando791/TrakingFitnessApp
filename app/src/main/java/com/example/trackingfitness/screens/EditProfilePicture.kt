package com.example.trackingfitness.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.viewModel.ImageViewModel
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun EditProfilePicture(
    imageViewModel: ImageViewModel,
    navController: NavController,
    userSessionManager: UserSessionManager
){
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxSize()
            ,
    ) {
        BodyContentEditProfileIcon(navController, imageViewModel, userSessionManager)
    }
}

@Composable
fun BodyContentEditProfileIcon(
    navController: NavController,
    imageViewModel: ImageViewModel,
    userSessionManager: UserSessionManager
) {
    val images by imageViewModel.images.observeAsState(emptyList())
    var isOverlayVisible by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf("") }
    var selectedImageId by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        BackButton(
            navController = navController,
            ruta = "profileScreen",
            modifier = Modifier
            .padding(end = 275.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Edita tú ícono actual")
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        10.dp,
                        shape = RoundedCornerShape(200.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(200.dp))
                    .width(180.dp)
                    .height(180.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(200.dp)
                    )
            ) {
                // Observa la imagen del LiveData
                val profileImage by userSessionManager.profileImage.observeAsState()
                if (profileImage != null) {
                    Image(
                        bitmap = profileImage!!.asImageBitmap(),
                        contentDescription = "Imagen de perfil",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Cargando imagen...",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        LaunchedEffect(Unit) {
            imageViewModel.fetchImages()
        }
        Text(text = "Íconos disponibles")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Definir 3 columnas
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            items(images) { image ->
                ImageCard(
                    imageUrl = image.image_url,
                    onClick = {
                        // Al seleccionar una imagen, mostramos el overlay
                        selectedImageUrl = image.image_url
                        selectedImageId = image.id.toString()
                        isOverlayVisible = true
                    }
                )
            }
        }
    }
    // Si el overlay es visible, mostramos la capa encima de la galería
    if (isOverlayVisible) {
        FullScreenOverlay(
            userSessionManager,
            navController,
            imageUrl = selectedImageUrl,
            imageId = selectedImageId,
            onDismiss = { isOverlayVisible = false }, // Cerrar overlay
            onConfirm = {
                // Confirmar el cambio de ícono (puedes llamar a la función de actualización aquí)
                imageViewModel.onImageSelected(selectedImageId,selectedImageUrl)
                isOverlayVisible = false
            }
        )
    }
}


@Composable
fun ImageCard(imageUrl: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(500.dp))
            .clickable { onClick() },
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
        )
    }
}

@Composable
fun FullScreenOverlay(
    userSessionManager: UserSessionManager,
    navController: NavController,
    imageUrl: String,
    imageId: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)) // Fondo semitransparente
            .clickable { onDismiss() }, // Cerrar al tocar el fondo
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(200.dp))
                    .size(250.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    userSessionManager.updateIcon("$imageId.png")
                    navController.navigate("profileScreen")
                }
            ) {
                Text(text = "Confirmar cambio de ícono")
            }
        }
    }
}