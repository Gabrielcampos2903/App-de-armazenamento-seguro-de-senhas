package com.example.superid.functions


import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.superid.screens.QRScanActivity

/**
 * Este é um exemplo de como você pode integrar o botão de escanear QR Code na sua interface
 * Você pode adicionar esse FloatingActionButton na sua tela principal ou onde desejar
 */
@Composable
fun QRScannerButton() {
    val context = LocalContext.current
    val corporateBlue = Color(0xFF003366)

    FloatingActionButton(
        onClick = {
            // Inicia a activity de scanner QR
            val intent = Intent(context, QRScanActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier.padding(16.dp),
        containerColor = corporateBlue,
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "Escanear QR Code",
            tint = Color.White
        )
    }
}

/**
 * Exemplo de como você poderia modificar a HomeMenu.kt para incluir um botão ou item de menu para o scanner QR
 *
 * Você pode adicionar um NavigationDrawerItem no DrawerContent:
 */
/*
// Em HomeMenu.kt adicione:
NavigationDrawerItem(
    icon = {
        Icon(
            imageVector = Icons.Default.QrCodeScanner, contentDescription = "QR Code"
        )
    },
    label = {
        Text(
            text = "Scan QR Code",
            fontSize = 17.sp,
            color = Color(0xFF003366),
            fontFamily = FontFamily(Font(R.font.rubikregular))
        )
    },
    selected = false,
    onClick = {
        // Iniciar a QRScanActivity
        val intent = Intent(context, QRScanActivity::class.java)
        context.startActivity(intent)
    }
)
*/