package com.example.superid.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginApp(this)
        }
    }
}

@Composable
fun LoginApp(activity: Activity){
    SuperIdLogin(activity = activity)
}


@Composable
fun SuperIdLogin(activity: Activity) {



    val customFontBlack = FontFamily(
        Font(R.font.rubikblack)
    )

    val customFontRegular = FontFamily(
        Font(R.font.rubikregular)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}
    var resultError by remember { mutableStateOf("")}
    var resultSuccess by remember {mutableStateOf("")}

    val context = LocalContext.current


    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                modifier = Modifier
                    .size(32.dp)
            )
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                text = "Login",
                fontSize = 50.sp,
                fontFamily = customFontBlack,
                fontWeight = FontWeight.Black,
                color = Color(0xFF003366),

            )
            Spacer(modifier = Modifier.height(100.dp))

            com.example.superid.functions.TextFields(
                info = email,
                onValueChange = { email = it},
                title = "Email"
            )

            Spacer(modifier = Modifier.height(15.dp))

           com.example.superid.functions.PasswordField(
               info = password,
               onValueChange = { password = it},
               title = "Password"
           )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Forgot password?",
                color = Color(0xFF003366),
                fontSize = 16.sp,
                fontFamily = customFontRegular,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        val intent = Intent(activity, PasswordResetActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        val intent = Intent(activity, MainDisplayActivity::class.java)
                                        activity.startActivity(intent)
                                        activity.finish()
                                    }
                                    else{
                                        val errorMessage = when (val exception = task.exception) {
                                            is FirebaseAuthInvalidUserException,
                                            is FirebaseAuthInvalidCredentialsException -> "Email or Password mismatch"
                                            else -> exception?.localizedMessage ?: "Error"
                                        }
                                        resultError = errorMessage

                                    }
                                }
                    }
                    else{
                        resultError = "Missing Info"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            ){
                Text(text = "Login ➜", fontSize = 30.sp, fontFamily = customFontBlack, color = Color.White)
            }
            Spacer(modifier = Modifier.height(20.dp))

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
}
