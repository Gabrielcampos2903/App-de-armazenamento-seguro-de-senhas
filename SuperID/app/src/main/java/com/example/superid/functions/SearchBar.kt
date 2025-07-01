package com.example.superid.functions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import kotlin.math.sin

@Composable
fun Searchbar(
    searchInfo: String,
    onTextChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val customFontRegular = FontFamily(Font(R.font.rubikregular))

    TextField(
        value = searchInfo,
        onValueChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        leadingIcon = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Pesquisar",
                    tint = Color(0xFF003366)
                )
            }
        },
        singleLine = true,
        placeholder = {
            Text("Search Passwords...", fontFamily = customFontRegular)
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


