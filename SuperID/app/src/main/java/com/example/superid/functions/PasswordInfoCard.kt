package com.example.superid.functions

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.tasks.await
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec


data class PasswordInfo(
    val category: String = "",
    val user: String = "",
    val domain: String = "",
    val password: String = ""
)

object SimpleCrypto {

    // Chave fixa de 16 bytes (128 bits) — para testes!
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



    fun decrypt(encrypted: String): String {
        val iv = IvParameterSpec(INIT_VECTOR.toByteArray(Charsets.UTF_8))
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val decoded = Base64.decode(encrypted, Base64.DEFAULT)
        val original = cipher.doFinal(decoded)
        return String(original, Charsets.UTF_8)
    }

}


@Composable
fun InfoCard(searchInfo: String, uid: String) {
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    val customFontRegular = FontFamily(Font(R.font.rubikregular))
    val customFontMedium = FontFamily(Font(R.font.rubikmedium))

    var info by remember { mutableStateOf<PasswordInfo?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(searchInfo) {
        try {
            val document = db.collection("user")
                .document(uid)
                .collection("SavedPasswords")
                .document(searchInfo)
                .get()
                .await()

            if (document.exists()) {
                val encryptedPassword = document.getString("password") ?: ""

                // Ajuste aqui: tenta descriptografar, se falhar, exibe mensagem amigável
                val decryptedPassword = try {
                    SimpleCrypto.decrypt(encryptedPassword)
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Senha inválida ou corrompida"
                }

                info = PasswordInfo(
                    category = document.getString("category") ?: "",
                    user = document.getString("user") ?: "",
                    password = decryptedPassword
                )
            }
        } finally {
            loading = false
        }
    }

    if (loading) {
        LinearProgressIndicator(
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF003366)
        )
        return
    }

    info?.let { data ->

        var showDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF003366).copy(alpha = .9f),
            )
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Key,
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PASSWORD",
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        lineHeight = 16.sp,
                        color = Color.White,
                        fontFamily = customFontMedium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Password",
                            tint = Color.White
                        )
                    }

                    if (showDialog && info != null) {
                        EditPasswordDialog(
                            initialUser = info!!.user,
                            initialPassword = info!!.password,
                            onDismiss = { showDialog = false },
                            onSave = { newUser, newPassword ->

                                val encryptedPassword = SimpleCrypto.encrypt(newPassword) 

                                val newData = hashMapOf(
                                    "user" to newUser,
                                    "password" to encryptedPassword,
                                )

                                val passwordRef = db.collection("user")
                                    .document(uid)
                                    .collection("SavedPasswords")
                                    .document(searchInfo)

                                passwordRef.update(newData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Password changed", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            },
                            onDelete = {
                                val passwordRef = db.collection("user")
                                    .document(uid)
                                    .collection("SavedPasswords")
                                    .document(searchInfo)

                                passwordRef.delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Password Deleted", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        )
                    }

                }
                HorizontalDivider(color = Color.White, thickness = 0.5.dp)

                InfoRow(label = "Title: ", value = searchInfo, font =customFontRegular)
                InfoRow(label = "Category:", value = data.category, font = customFontRegular)
                InfoRow(label = "User:", value = data.user, font = customFontRegular)
                InfoRow(label = "Password:", value = data.password, font = customFontRegular)
            }
        }
    } ?: Text("Password not found", color = Color.Red)
}

