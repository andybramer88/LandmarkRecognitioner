package com.example.landmarkrecognitioner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.landmarkrecognitioner.data.TfLiteLandmarkClassifier
import com.example.landmarkrecognitioner.domain.Classification
import com.example.landmarkrecognitioner.presentation.CameraPreview
import com.example.landmarkrecognitioner.presentation.LandmarkImageAnalyzer
import com.example.landmarkrecognitioner.ui.theme.LandmarkRecognitionerTheme

// ergänzte Importe
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!hasCameraPermission()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 0
            )
        }
        setContent {
            LandmarkRecognitionerTheme {
                var classifications by remember {
                    mutableStateOf(emptyList<Classification>())
                }

                // Wikipedia Api
                var wikiSummary by remember { mutableStateOf("") }
                var wikiLoading by remember { mutableStateOf(false) }
                var wikiTerm by remember { mutableStateOf("") }
                val context = LocalContext.current

                val analyzer = remember {
                    LandmarkImageAnalyzer(
                        classifier = TfLiteLandmarkClassifier(
                            context = context.applicationContext  // ← context statt applicationContext
                        ),
                        onResults = { classificationsList ->
                            classifications = classificationsList
                            if (classificationsList.isNotEmpty()) {
                                wikiTerm = classificationsList.first().name
                            }
                        }
                    )
                }

                // Load Wikipedia summary when wikiTerm changes
                LaunchedEffect(wikiTerm) {
                    if (wikiTerm.isNotEmpty()) {
                        loadWikipediaSummary(wikiTerm) { summary ->
                            wikiSummary = summary
                        }
                    }
                }

                val controller = remember {
                    LifecycleCameraController(context.applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(
                            ContextCompat.getMainExecutor(applicationContext),
                            analyzer
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CameraPreview(controller, Modifier.fillMaxSize())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        classifications.forEach {
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Wikipedia summary card at the bottom
                    if (wikiSummary.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Wikipedia Summary",
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = wikiSummary,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun loadWikipediaSummary(term: String, onResult: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.wikipediaApi.getPageSummary(term)
                if (response.isSuccessful) {
                    val summary = response.body()?.extract ?: "Keine Zusammenfassung gefunden"
                    onResult(summary)
                } else {
                    onResult("Artikel '$term' nicht gefunden")
                }
            } catch (e: Exception) {
                onResult("Fehler: ${e.message}")
            }
        }
    }



    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}


// Info
// assets: landmarks.tflite is the pretrained model for TensorFlowLite