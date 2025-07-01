package com.example.superid.functions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.superid.R


@Composable
fun EditPasswordDialog(
    initialUser: String,
    initialPassword: String,
    onDismiss: () -> Unit,
    onSave: (newTitle: String, newPassword: String) -> Unit,
    onDelete: () -> Unit
) {
    val customFontBlack = FontFamily(
        Font(R.font.rubikblack)
    )

    val customFontRegular = FontFamily(
        Font(R.font.rubikregular)
    )


    var user by remember { mutableStateOf(initialUser) }
    var password by remember { mutableStateOf(initialPassword) }

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Edit Password", fontFamily = customFontBlack, color = Color(0xFF003366))
        },
        text = {
            Column {
                com.example.superid.functions.TextFields(
                    info = user,
                    onValueChange = {user = it},
                    title = "User"
                )

                Spacer(modifier = Modifier.height(8.dp))

                com.example.superid.functions.TextFields(
                    info = password,
                    onValueChange = {password = it},
                    title = "Password"
                )

                Spacer(modifier = Modifier.height(8.dp))


            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(user, password)
                onDismiss()
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Save", color = Color.White)
            }
        },

        dismissButton = {
            Row {

                TextButton(onClick = {
                    onDelete()
                    onDismiss()
                }) {

                    Text("Delete", color = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFF003366))
                }
            }
        }
    )
}

