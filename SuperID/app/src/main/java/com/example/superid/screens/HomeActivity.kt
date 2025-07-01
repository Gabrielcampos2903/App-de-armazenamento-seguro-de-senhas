package com.example.superid.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
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

class HomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        
        setContent {
            HomeScreen(
                onQRCodeClick = {
                    val intent = Intent(this, QRScanActivity::class.java)
                    startActivity(intent)
                },
                onLogoutClick = {
                    auth.signOut()
                    // Voltar para tela inicial após logout
                    finish()
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen(onQRCodeClick: () -> Unit, onLogoutClick: () -> Unit) {
        val customFontBlack = FontFamily(Font(R.font.rubikblack))
        val customFontRegular = FontFamily(Font(R.font.rubikregular))
        val corporateBlue = Color(0xFF003366)
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Super ID",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontBlack,
                            color = corporateBlue
                        )
                    },
                    actions = {
                        IconButton(onClick = onLogoutClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logout),
                                contentDescription = "Logout",
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo ou ícone
                    Image(
                        painter = painterResource(id = R.drawable.superid_logo),
                        contentDescription = "Super ID Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(bottom = 24.dp)
                    )
                    
                    Text(
                        text = "Bem-vindo ao Super ID",
                        fontFamily = customFontBlack,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = corporateBlue,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Seu gerenciador de senhas seguro",
                        fontFamily = customFontRegular,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    Button(
                        onClick = onQRCodeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = corporateBlue)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Login sem Senha (QR Code)",
                                fontSize = 18.sp,
                                fontFamily = customFontRegular
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Texto explicativo
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Como funciona:",
                                fontFamily = customFontBlack,
                                fontSize = 18.sp,
                                color = corporateBlue,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "1. Clique no botão acima\n" +
                                        "2. Escaneie o código QR no site\n" +
                                        "3. Acesse sem digitar senha",
                                fontFamily = customFontRegular,
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
} 