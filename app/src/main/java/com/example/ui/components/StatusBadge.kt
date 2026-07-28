package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "delivered" -> Color(0xFF064E3B) to Color(0xFF34D399)
        "shipped" -> Color(0xFF1E1B4B) to Color(0xFF818CF8)
        "confirmed" -> Color(0xFF1E3A8A) to Color(0xFF60A5FA)
        "pending" -> Color(0xFF451A03) to Color(0xFFFBBF24)
        "cancelled", "refunded" -> Color(0xFF4C1D95) to Color(0xFFF87171)
        else -> Color(0xFF1E293B) to Color(0xFF94A3B8)
    }

    Text(
        text = status,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
