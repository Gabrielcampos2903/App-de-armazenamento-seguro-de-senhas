package com.example.superid.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth

class LoginResultActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        
        val loginSuccess = intent.getBooleanExtra("LOGIN_SUCCESS", false)
        val loginToken = intent.getStringExtra("LOGIN_TOKEN") ?: ""
        val errorMessage = intent.getStringExtra("ERROR_MESSAGE") ?: "Erro desconhecido"
        
        setContent {
            LoginResultScreen(
                success = loginSuccess,
                errorMessage = errorMessage,
                onContinue = {
                    if (loginSuccess) {
                        // Navegue para a tela principal do app
                        val intent = Intent(this, MainDisplayActivity::class.java)
                        startActivity(intent)
                        finishAffinity() // Encerra todas as activities anteriores
                    } else {
                        // Volte para a tela anterior
                        finish()
                    }
                }
            )
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginResultScreen(success: Boolean, errorMessage: String, onContinue: () -> Unit) {
        val customFontBlack = FontFamily(Font(R.font.rubikblack))
        val customFontRegular = FontFamily(Font(R.font.rubikregular))
        val corporateBlue = Color(0xFF003366)
        val successGreen = Color(0xFF4CAF50)
        val errorRed = Color(0xFFE53935)
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (success) "Login Bem-Sucedido" else "Falha no Login",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontBlack,
                            color = corporateBlue
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo SuperID
                    Image(
                        painter = painterResource(id = R.drawable.superid_logo),
                        contentDescription = "Super ID Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 24.dp)
                    )
                    
                    // Ícone de resultado
                    if (success) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Sucesso",
                            tint = successGreen,
                            modifier = Modifier.size(80.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Erro",
                            tint = errorRed,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Mensagem principal
                    Text(
                        text = if (success) 
                            "Login sem senha realizado com sucesso!" 
                        else 
                            "Não foi possível realizar o login sem senha",
                        fontFamily = customFontBlack,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        color = if (success) successGreen else errorRed,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Mensagem secundária
                    Text(
                        text = if (success) 
                            "Você foi autenticado com segurança usando o código QR." 
                        else 
                            "Motivo: $errorMessage",
                        fontFamily = customFontRegular,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    // Botão de continuar
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (success) successGreen else corporateBlue
                        )
                    ) {
                        Text(
                            text = if (success) "Continuar" else "Tentar novamente",
                            fontSize = 18.sp,
                            fontFamily = customFontRegular,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
} 