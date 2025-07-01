package com.example.superid.screens

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class WebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("URL_EXTRA") ?: "https://example.com"

        setContent {
            var webView: WebView? = null

            WebViewScreen(
                url = url,
                onBackClick = {
                    if (webView?.canGoBack() == true) {
                        webView?.goBack()
                    } else {
                        finish()
                    }
                },
                onWebViewCreated = {
                    webView = it
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WebViewScreen(url: String, onBackClick: () -> Unit, onWebViewCreated: (WebView) -> Unit) {
        val corporateBlue = Color(0xFF003366)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Super ID Secure Login",
                            fontSize = 24.sp,
                            color = corporateBlue
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
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
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    return false
                                }
                            }
                            loadUrl(url)
                            onWebViewCreated(this)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed() // Botão voltar tratado diretamente no WebViewScreen agora
    }
}
