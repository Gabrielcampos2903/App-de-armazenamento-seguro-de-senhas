package com.example.superid.screens



import androidx.compose.ui.platform.LocalContext

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.example.superid.SignupActivity
import androidx.activity.compose.setContent
import com.example.superid.ui.theme.SuperIDTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import android.view.View
import android.os.Build
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.view.WindowManager
import android.view.Window
import androidx.compose.foundation.layout.statusBars




class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.hide()


        setContent {
            SuperIDTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)  // Cor de fundo (ajuste conforme seu layout)
                ) {
                    WelcomeToApp()
                }
            }
        }
    }
@Composable
fun WelcomeToApp(){

    val context = LocalContext.current

    val customFont1 = FontFamily(
        Font(R.font.rubikblack)
    )
    val customFont2 = FontFamily(
        Font(R.font.rubikregular)
    )


   Box(
       modifier = Modifier
           .fillMaxSize()
           .background(Color.White)

    ){
       Column(
           modifier = Modifier
               .fillMaxWidth()
               .padding(
                   WindowInsets.statusBars.asPaddingValues()
               )
               .padding(top = 65.dp, start = 24.dp, end = 24.dp),
           horizontalAlignment = Alignment.CenterHorizontally,
       ){
            Text(
                text = "WELCOME",
                fontSize = 70.sp,
                fontFamily = customFont1,
                color = Color(0xFF003366),
            )
            Text(
                text = "TO SUPER ID",
                fontSize = 30.sp,
                fontFamily = customFont1,
                color = Color(0xFF003366)
            )
            Spacer(modifier = Modifier.height(120.dp))
           Image(
               painter = painterResource(id = R.drawable.data_security_5075022),
               contentDescription = "Icon",
               modifier = Modifier
                   .size(250.dp)
           )
           Spacer( modifier = Modifier.height(150.dp))

           Button(
               onClick = {
                   val intent = Intent(context, LoginActivity::class.java)
                   context.startActivity(intent)
            },
           colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
           modifier = Modifier.fillMaxWidth(),

           ) {
               Text ( text = "Login", color = Color.White, fontSize = 30.sp, fontFamily = customFont1)
           }

           Spacer(modifier = Modifier.height(20.dp))

           Button(
               onClick = {
                   val intent = Intent(context, SignupActivity::class.java)
                   context.startActivity(intent)
               },
           colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
           modifier = Modifier.fillMaxWidth(),

           ) {
               Text ( text = "Sign Up", color = Color.White , fontSize = 30.sp, fontFamily = customFont1)
           }
       }
   }
}
}



