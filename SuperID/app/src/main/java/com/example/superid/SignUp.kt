package com.example.superid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.screens.LicenseTermsPopUp
import com.example.superid.screens.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperIDApp(this)
        }
    }
}

object CryptoUtils {
    private const val SECRET_KEY = "1234567890123456"
    private const val INIT_VECTOR = "abcdefghijklmnop"

    fun encrypt(input: String): String {
        val iv = IvParameterSpec(INIT_VECTOR.toByteArray(Charsets.UTF_8))
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        val encrypted = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }
}

@Composable
fun SuperIDApp(activity: Activity) {
    var showInfoText by remember { mutableStateOf(true) }
    var showLicenseDialog by remember { mutableStateOf(!SessionState.hasAcceptedLicense) }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                showInfoText -> {
                    InfoText {
                        showInfoText = false
                    }
                }
                showLicenseDialog -> {
                    LicenseTermsPopUp(onDismiss = {
                        SessionState.hasAcceptedLicense = true
                        showLicenseDialog = false
                    })
                }
                else -> {
                    SuperIDSignUp(activity = activity)
                }
            }
        }
    }
}


@Composable
fun InfoText(onNextClicked: () -> Unit) {
    val customFontBlack = FontFamily(Font(R.font.rubikblack))
    val customFontRegular = FontFamily(Font(R.font.rubikregular))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SuperID",
                fontSize = 40.sp,
                fontFamily = customFontBlack,
                fontWeight = FontWeight.Black,
                color = Color(0xFF003366)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "SuperID is an innovative way to log in without passwords and securely store the passwords you use every day",
                fontSize = 18.sp,
                fontFamily = customFontRegular,
                color = Color(0xFF003366),
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onNextClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Next",
                    fontSize = 20.sp,
                    fontFamily = customFontBlack,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}


@SuppressLint("HardwareIds")
@Composable
fun SuperIDSignUp(activity: Activity) {
    val customFontBlack = FontFamily(Font(R.font.rubikblack))
    val customFontRegular = FontFamily(Font(R.font.rubikregular))

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var resultError by remember { mutableStateOf("") }
    var resultSuccess by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var waitingVerification by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { activity.finish() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 10.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFF003366),
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Create Account",
                fontSize = 50.sp,
                fontFamily = customFontBlack,
                fontWeight = FontWeight.Black,
                color = Color(0xFF003366),
            )
            Spacer(modifier = Modifier.height(20.dp))
            com.example.superid.functions.TextFields(
                info = username,
                onValueChange = { username = it },
                title = "Username"
            )

            Spacer(modifier = Modifier.height(15.dp))
            com.example.superid.functions.TextFields(
                info = email,
                onValueChange = { email = it },
                title = "Email")

            Spacer(modifier = Modifier.height(15.dp))
            com.example.superid.functions.PasswordField(
                info = password,
                onValueChange = { password = it},
                title = "Password"
            )

            Spacer(modifier = Modifier.height(15.dp))

            com.example.superid.functions.PasswordField(
                info = repeatPassword,
                onValueChange = { repeatPassword = it},
                title = "Repeat Password"
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank() && username.isNotBlank() && repeatPassword.isNotBlank()) {
                        if (password == repeatPassword) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        waitingVerification = true

                                    } else {
                                        resultError = task.exception?.message ?: "Erro desconhecido"
                                    }
                                }
                        } else {
                            resultError = "Passwords don't match"
                        }
                    } else {
                        resultError = "Missing info"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(text = "Sign Up ➜", fontSize = 30.sp, fontFamily = customFontBlack, color = Color.White)
            }

            LaunchedEffect(resultError) {
                if (resultError.isNotEmpty()) {
                    Toast.makeText(context, resultError, Toast.LENGTH_SHORT).show()
                    resultError = ""
                }
            }

            LaunchedEffect(resultSuccess) {
                if (resultSuccess.isNotEmpty()) {
                    Toast.makeText(context, resultSuccess, Toast.LENGTH_SHORT).show()
                    resultSuccess = ""
                }
            }

        }
    }


    LaunchedEffect(waitingVerification) {
        if (waitingVerification) {
                delay(3000)
                val user = auth.currentUser
                user?.reload()?.addOnSuccessListener {
                        val uid = auth.currentUser?.uid
                        val imei = Settings.Secure.getString(
                            context.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )

                        val categorys = hashMapOf(
                            "category" to arrayListOf("Site", "App", "Num pad")
                        )

                        val encryptedPassword = CryptoUtils.encrypt(password)

                        val userData = hashMapOf(
                            "email" to email,
                            "username" to username,
                            "password" to encryptedPassword,
                            "imei" to imei,
                            "date" to FieldValue.serverTimestamp()
                        )

                        db.collection("user").document(uid.toString())
                            .set(userData)
                            .addOnSuccessListener {
                                db.collection("user")
                                    .document(uid.toString())
                                    .collection("categorys")
                                    .document("allcategorys")
                                    .set(categorys)

                                val intent = Intent(activity, LoginActivity::class.java)
                                activity.startActivity(intent)
                                activity.finish()
                            }
                            .addOnFailureListener { e ->
                                resultError = "Not able to save at firebase: ${e.message}"
                            }
                }
        }
    }
}
