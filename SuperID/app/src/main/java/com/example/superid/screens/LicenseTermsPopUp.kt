package com.example.superid.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.verticalScroll


@Composable
fun LicenseTermsPopUp(
    onDismiss: () -> Unit

) {
    var isChecked by remember { mutableStateOf(false) }
    val canConfirm by remember { derivedStateOf { isChecked } }

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = {},
        confirmButton = {
            // O botão de confirmação so podera ser clicado ao confirmar a checkbox
            TextButton(
                onClick = {
                    if (canConfirm) {
                        onDismiss() // Fecha o pop-up
                    }
                },
                enabled = canConfirm,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (canConfirm) Color(0xFF003366) else Color.DarkGray,
                    contentColor = Color.White
                )

            ) {
                Text("Confirm",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canConfirm) Color.White else Color.Gray
                )
            }
        },
        modifier = Modifier.height(400.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Privacy", tint = Color(0xFF003366))
                Text(
                    text = "License Terms",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Caixa de rolagem para os Termos de Licença
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Permite que a caixa de texto ocupe o espaço restante
                ) {
                    // Aqui você usa verticalScroll para rolar o conteúdo
                    val scrollState = rememberScrollState()
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        Text(
                            text = "SuperID User License Terms " +
                                    "Effective Date: [2025]\n" +
                                    "---------------------------------------------------------" +
                                    "\n" +
                                    "These License Terms govern the use of the SuperID application, intended for storing and managing passwords and login data. By installing or using the Application, you agree to the terms described herein. If you do not agree, do not use the Application.\n" +
                                    "\n" +
                                    "1. License Grant\n" +
                                    "SuperID grants the user a limited, non-exclusive, and non-transferable license to use the Application solely for personal purposes and in accordance with the terms of this License.\n" +
                                    "\n" +
                                    "2. Permitted Use\n" +
                                    "The user may use the Application to:\n" +
                                    "\n" +
                                    "Store and organize login credentials and authentication data.\n" +
                                    "\n" +
                                    "Use features such as password verification.\n" +
                                    "\n" +
                                    "3. Restrictions\n" +
                                    "The user may not:\n" +
                                    "\n" +
                                    "Modify, decompile, or attempt to extract the Application's source code.\n" +
                                    "\n" +
                                    "Redistribute or transfer rights to the Application.\n" +
                                    "\n" +
                                    "Use the Application in an illegal manner or in a way that harms its functionality.\n" +
                                    "\n" +
                                    "4. Data Protection\n" +
                                    "SuperID employs encryption and other measures to protect your data. However, the user acknowledges that no technology is 100% secure and is responsible for providing additional protection for their personal data.\n" +
                                    "\n" +
                                    "5. Privacy\n" +
                                    "The collection and use of personal data follow the SuperID Privacy Policy" +
                                    "\n" +
                                    "6. User Responsibility\n" +
                                    "The user is responsible for the security of the stored credentials. SuperID is not responsible for data loss or damage caused by system failures or improper exposure of information.\n" +
                                    "\n" +
                                    "7. Application Modifications\n" +
                                    "SuperID may modify or discontinue the Application at any time without prior notice. The user will not be entitled to compensation for interruptions or changes.\n" +
                                    "\n" +
                                    "8. Termination of the License\n" +
                                    "The License will be automatically terminated in case of violation of the terms. The user must cease using the Application and uninstall it after termination.\n" +
                                    "\n" +
                                    "9. Disclaimer of Warranties\n" +
                                    "The Application is provided \"as is,\" with no warranties of continuous or error-free operation.\n" +
                                    "\n" +
                                    "10. Limitation of Liability\n" +
                                    "SuperID is not liable for indirect, incidental, or consequential damages arising from the use of the Application, even if it has been advised of such damages.\n" +
                                    "\n" +
                                    "11. Changes to the Terms\n" +
                                    "SuperID may change these Terms at any time, with changes being published in the most recent version. Continued use of the Application after changes implies acceptance of the new terms.\n" +
                                    "\n" +
                                    "12. Governing Law\n" +
                                    "These Terms will be governed by the laws of [Brazil/São Paulo], and any disputes will be resolved in the competent courts of [São Paulo].\n" +
                                    "\n" +
                                    "13. Contact\n" +
                                    "For questions, contact us via email: [gabmcampos@gmail.com]..",

                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Caixa de seleção
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF003366),
                            uncheckedColor = Color(0xFF003366)
                        )
                    )
                    Text(
                        text = "Agree to the License Terms",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003366)
                    )
                }
            }
        }
    )
}
