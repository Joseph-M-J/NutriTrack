package com.example.nutritrack.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadedEntries(
    modifier: Modifier = Modifier,
    count: Int = 0
) {
    val color = if (count > 0) {
        Color(0.373f, 0.875f, 0.392f, 1.0f)
    } else {
        Color.Gray.copy(alpha = 0.5f)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = "Loaded Entries",
            tint = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadedEntries() {
    LoadedEntries(count=2)
}