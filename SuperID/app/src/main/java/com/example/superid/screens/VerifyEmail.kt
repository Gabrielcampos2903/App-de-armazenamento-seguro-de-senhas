package com.example.superid.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class VerifyEmailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           VerifyUserEmail(this)
        }
    }
}


@Composable
fun VerifyUserEmail(activity: Activity){
    val customFontBlack = FontFamily(
        Font(R.font.rubikblack)
    )

    val customFontRegular = FontFamily(
        Font(R.font.rubikregular)
    )

    var resultMessage by remember { mutableStateOf("")}
    var isChecking by remember { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current




    LaunchedEffect(isChecking) {
        while (isChecking) {
            delay(3000)
            user?.reload()?.addOnSuccessListener {
                if (user.isEmailVerified) {
                    Toast.makeText(context, "Email verified! Returning...", Toast.LENGTH_SHORT).show()
                    isChecking = false
                    val intent = Intent(activity, MainDisplayActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }
            }
        }
    }




   Box(
       modifier = Modifier
       .fillMaxSize()
   ){

       IconButton(
           onClick = {
               val intent = Intent(activity, MainDisplayActivity::class.java)
               activity.startActivity(intent)
               activity.finish()
           },
           modifier = Modifier
               .align(Alignment.TopStart)
               .padding(top = 20.dp),

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
               .fillMaxSize()
               .padding(15.dp),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ){

           Text(
               text = "Verify account email",
               fontSize = 50.sp,
               fontFamily = customFontBlack,
               fontWeight = FontWeight.Black,
               color = Color(0xFF003366),
               textAlign = TextAlign.Center
           )
           Spacer(modifier = Modifier.height(40.dp))

           Button(
               onClick = {
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                resultMessage = "Verification sent to your email"
                            }else{
                                resultMessage = "Failed to send verification"
                            }
                            Toast.makeText(context, resultMessage, Toast.LENGTH_LONG).show()
                        }
               },
               colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
               shape = RoundedCornerShape(30.dp)
           ){
               Text("Send Email verification", fontSize = 20.sp, fontFamily = customFontBlack, color = Color.White)
           }
       }
   }
}







































