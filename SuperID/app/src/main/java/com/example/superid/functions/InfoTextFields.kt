package com.example.superid.functions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R


@Composable
fun TextFields(info: String, onValueChange: (String) -> Unit, title: String) {

    val customFontRegular = FontFamily(Font(R.font.rubikregular))

    TextField(
        value = info,
        onValueChange = { onValueChange(it) },
        label = {
            Text(title, fontSize = 18.sp, fontFamily = customFontRegular, fontWeight = FontWeight.Bold, color = Color(0xFF003366))
        },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF003366),
            unfocusedTextColor = Color(0xFF003366),
            focusedBorderColor = Color(0xFF003366),
            unfocusedBorderColor = Color(0xFF003366),
            focusedLabelColor = Color(0xFF003366),
            unfocusedLabelColor = Color(0xFF003366),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}
