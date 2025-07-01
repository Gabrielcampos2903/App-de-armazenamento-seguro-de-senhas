package com.example.superid.functions

import java.util.Base64
import java.security.SecureRandom
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

data class DropdownItem(
    val title: String
)

fun generateAccessToken(): String {
    val randomBytes = ByteArray(192)
    SecureRandom().nextBytes(randomBytes)
    return Base64.getEncoder().encodeToString(randomBytes)
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
        return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT)
    }
}


@Composable
fun AddPassword(onDismiss: () -> Unit, userId: String) {

    val context = LocalContext.current

    fun savePassword(
        context: Context,
        userId: String,
        user: String,
        category: String,
        password: String,
        title: String,
        onDismiss: () -> Unit
    ) {
        val db = Firebase.firestore
        val token = generateAccessToken()

        val passwordRef = db.collection("user")
            .document(userId)
            .collection("SavedPasswords")
            .document(title)

        passwordRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Toast.makeText(context, "This password title already exist", Toast.LENGTH_SHORT).show()
                } else {

                    val encryptedPassword = CryptoUtils.encrypt(password)

                    val data = hashMapOf(
                        "category" to category,
                        "user" to user,
                        "password" to encryptedPassword,
                        "accessToken" to token
                    )

                    passwordRef.set(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Password added", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to verify: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    val customFontBlack = FontFamily(Font(R.font.rubikblack))
    val customFontRegular = FontFamily(Font(R.font.rubikregular))

    var password by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("")}
    var selectedCategory by remember { mutableStateOf<DropdownItem?>(null) }

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Enter all data",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = customFontBlack,
                color = Color(0xFF003366)
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(color = Color.White),
            ) {
                DropDownMenu(
                    selectedItem = selectedCategory,
                    onItemSelected = { selectedCategory = it }
                )

                Spacer(modifier = Modifier.height(30.dp))

                com.example.superid.functions.TextFields(
                    info = title,
                    onValueChange = { title = it},
                    title = "Title"
                )

                Spacer(modifier = Modifier.height(30.dp))

                com.example.superid.functions.TextFields(
                    info = user,
                    onValueChange = { user = it },
                    title = "User"
                )


                Spacer(modifier = Modifier.height(30.dp))

                com.example.superid.functions.TextFields(
                    info = password,
                    onValueChange = { password = it },
                    title = "Password"
                )

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password.isNotEmpty() && selectedCategory != null) {
                        savePassword(
                            context = context,
                            userId = userId,
                            user = user,
                            category = selectedCategory!!.title,
                            password = password,
                            title = title,
                            onDismiss = onDismiss
                        )
                    } else {
                        Toast.makeText(context, "Fill all data", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Save",
                    fontSize = 20.sp,
                    fontFamily = customFontRegular,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    fontSize = 20.sp,
                    fontFamily = customFontRegular,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366)
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    selectedItem: DropdownItem?,
    onItemSelected: (DropdownItem) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var itemList by remember { mutableStateOf<List<DropdownItem>>(emptyList()) }

    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid

    LaunchedEffect(Unit) {
        val document = db.collection("user")
            .document(uid.toString())
            .collection("categorys")
            .document("allcategorys")
            .get()
            .await()

        val categories = document.get("category") as? List<String>
        categories?.let { list ->
            itemList = list.map { DropdownItem(it) }
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(10.dp)),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    value = selectedItem?.title ?: "",
                    onValueChange = { },
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = "Ícone de edição")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    placeholder = {
                        Text(
                            text = "Choose a category",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.rubikregular)),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF003366),
                        unfocusedTextColor = Color(0xFF003366),
                        focusedBorderColor = Color(0xFF003366),
                        unfocusedBorderColor = Color(0xFF003366),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                itemList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.rubikregular))
                                )
                            }
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}



