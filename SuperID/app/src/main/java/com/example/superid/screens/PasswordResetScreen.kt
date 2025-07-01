package com.example.superid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.enableEdgeToEdge
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import com.example.superid.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.Font
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class PasswordResetActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var shouldListenToAuthChanges = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            PasswordResetScreen(
                onEmailSent = {
                    shouldListenToAuthChanges = true
                },
                onBackClick = {
                    finish()
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (shouldListenToAuthChanges && firebaseAuth.currentUser != null) {
                finish()
            }
        }
        authStateListener?.let { auth.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        authStateListener?.let { auth.removeAuthStateListener(it) }
    }
}

fun sendVerificationEmail(
    onResult: (String) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult("✅ E-mail de verificação enviado.")
                } else {
                    onResult("❌ Falha ao enviar e-mail: ${task.exception?.message}")
                }
            }
    } else {
        onResult("⚠️ Nenhum usuário logado para verificar.")
    }
}


@Composable
fun PasswordResetScreen(
    onEmailSent: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val customFontBlack = FontFamily(Font(R.font.rubikblack))
    val customFontRegular = FontFamily(Font(R.font.rubikregular))

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFF003366),
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Reset Password", fontFamily = customFontBlack, fontSize = 50.sp, color = Color(0xFF003366))
            Spacer(modifier = Modifier.height(32.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.startsWith("Email sent")) Color.Green else Color.Red,
                    fontFamily = customFontRegular,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            com.example.superid.functions.TextFields(
                info = email,
                onValueChange = { email = it },
                title = "Email"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                    onClick = {
                        if (email.isEmpty()) {
                            message = "Please enter your email address"
                            return@Button
                        }

                        isLoading = true
                        message = ""

                        val client = OkHttpClient()
                        val json = JSONObject().apply {
                            put("email", email)
                        }

                        val requestBody = json.toString()
                            .toRequestBody("application/json; charset=utf-8".toMediaType())

                        val request = Request.Builder()
                            .url("https://us-central1-superid-turma2-4.cloudfunctions.net/checkEmailVerified")
                            .post(requestBody)
                            .build()

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = client.newCall(request).execute()
                                val responseBody = response.body?.string()
                                val responseJson = JSONObject(responseBody ?: "")

                                val isVerified = responseJson.optBoolean("verified", false)

                                withContext(Dispatchers.Main) {
                                    if (isVerified) {
                                        auth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener { task ->
                                                isLoading = false
                                                if (task.isSuccessful) {
                                                    message = "Email sent! Check your inbox to reset your password."
                                                    onEmailSent()
                                                } else {
                                                    message = "Failed to send reset email."
                                                }
                                            }
                                    } else {
                                        message = "Email is not verified. Please verify your email first."
                                        isLoading = false
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    message = "Error: ${e.message}"
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text(
                            text = "Send Link",
                            fontFamily = customFontBlack,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = {
                        if (email.isNotEmpty()) {
                            sendVerificationEmail { result ->
                                message = result
                            }
                        } else {
                            message = "⚠️ Insira seu e-mail antes de verificar."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),

                    ) {
                    Text(
                        text = "Verify Email",
                        fontFamily = customFontBlack,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }   }}}