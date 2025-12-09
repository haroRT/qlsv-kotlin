package com.kma.qlsv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Number.pxToDp(): Dp {
    val scale = LocalScaleFactor.current
    return (this.toFloat() * scale).dp
}