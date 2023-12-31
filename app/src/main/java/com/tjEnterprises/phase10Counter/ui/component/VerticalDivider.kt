package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalDivider(modifier: Modifier = Modifier, color: Color = DividerDefaults.color, width: Dp = 1.dp) {
    Divider(
        color = color,
        modifier = Modifier
            .fillMaxHeight()  //fill the max height
            .width(width).then(modifier)
    )
}