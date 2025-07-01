package com.example.superid.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.superid.functions.InfoCard
import kotlinx.coroutines.tasks.await


class AllPasswordsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShowAllPasswords(this)
        }
    }
}

@Composable
fun ShowAllPasswords(activity: Activity) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid

    var passwords by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect (uid) {
        if (uid != null) {
            val passwordsInfo = db.collection("user")
                .document(uid)
                .collection("SavedPasswords")
                .get()
                .await()

            val titles = passwordsInfo.documents.mapNotNull { it.id }
            passwords = titles
        }
        loading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        IconButton(
            onClick = {
                val intent = Intent(activity, MainDisplayActivity::class.java)
                activity.startActivity(intent)
                activity.finish() },
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

        if (loading) {
            LinearProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF003366)
            )
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                passwords.forEach { title ->
                    uid?.let {
                        InfoCard(searchInfo = title, uid = it)
                    }
                }
            }
        }
    }
}



