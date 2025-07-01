package com.example.superid.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.superid.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.google.firebase.auth.FirebaseAuth
import com.example.superid.functions.generateAccessToken

class QRScanActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private val firestore = FirebaseFirestore.getInstance()
    private var isProcessingQR = false

    companion object {
        private const val TAG = "QRScanActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            QRScanScreen(
                onBackClick = {
                    val intent = Intent(this, MainDisplayActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                },
                onQRCodeDetected = { qrContent ->
                    if (!isProcessingQR) {
                        isProcessingQR = true
                        processQRCode(qrContent)
                    }
                }
            )
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QRScanScreen(onBackClick: () -> Unit, onQRCodeDetected: (String) -> Unit) {
        val customFontBlack = FontFamily(Font(R.font.rubikblack))
        val customFontRegular = FontFamily(Font(R.font.rubikregular))
        val corporateBlue = Color(0xFF003366)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Escanear Código QR",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontBlack,
                            color = corporateBlue
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = corporateBlue
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (allPermissionsGranted()) {
                    CameraPreview(onQRCodeDetected)

                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .align(Alignment.Center)
                            .background(Color.Transparent)
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                    )

                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = "Posicione o código QR dentro do quadro",
                            modifier = Modifier.padding(16.dp),
                            fontFamily = customFontRegular,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = corporateBlue,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Acesso à câmera necessário",
                            fontFamily = customFontBlack,
                            fontSize = 22.sp,
                            color = corporateBlue,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                ActivityCompat.requestPermissions(
                                    this@QRScanActivity,
                                    REQUIRED_PERMISSIONS,
                                    REQUEST_CODE_PERMISSIONS
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = corporateBlue),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text(
                                "Permitir",
                                fontSize = 18.sp,
                                fontFamily = customFontRegular,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CameraPreview(onQRCodeDetected: (String) -> Unit) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val previewView = remember { androidx.camera.view.PreviewView(context) }

        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

        LaunchedEffect(true) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val analyzer = QRCodeAnalyzer { qrCode ->
                onQRCodeDetected(qrCode)
            }

            imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

            cameraProvider.unbindAll()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        }

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
    }

    private inner class QRCodeAnalyzer(
        private val onQRCodeDetected: (String) -> Unit
    ) : ImageAnalysis.Analyzer {

        @androidx.camera.core.ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            barcodes[0].rawValue?.let { qrContent ->
                                if (qrContent.startsWith("superid-login:")) {
                                    onQRCodeDetected(qrContent)
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Falha ao processar imagem", it)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    private fun processQRCode(qrContent: String) {
        val loginToken = qrContent.substringAfter("superid-login:")
        verifyLoginToken(loginToken)
    }

    private fun verifyLoginToken(token: String) {
        Log.d(TAG, "Verificando token: $token")

        firestore.collection("login").document(token).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val used = document.getBoolean("used") ?: false

                    if (!used) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        val newAccessToken = generateAccessToken()

                        val updates = mapOf(
                            "used" to true,
                            "uid" to uid,
                            "confirmedAt" to System.currentTimeMillis(),
                            "accessToken" to newAccessToken
                        )

                        firestore.collection("login").document(token).update(updates)
                            .addOnSuccessListener {
                                navigateToSuccess(token)
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Erro ao atualizar status do token", e)
                                navigateToFailure("Erro ao validar token: ${e.message}")
                            }
                    } else {
                        navigateToFailure("Token já foi utilizado")
                    }
                } else {
                    navigateToFailure("Token não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao verificar token", e)
                navigateToFailure("Erro de conexão: ${e.message}")
            }
    }


    private fun navigateToSuccess(token: String) {
        val intent = Intent(this, LoginResultActivity::class.java).apply {
            putExtra("LOGIN_SUCCESS", true)
            putExtra("LOGIN_TOKEN", token)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToFailure(errorMessage: String) {
        val intent = Intent(this, LoginResultActivity::class.java).apply {
            putExtra("LOGIN_SUCCESS", false)
            putExtra("ERROR_MESSAGE", errorMessage)
        }
        startActivity(intent)
        finish()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                recreate()
            } else {
                Toast.makeText(
                    this,
                    "Permissões não concedidas pelo usuário.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
