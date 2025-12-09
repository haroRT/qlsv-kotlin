package com.kma.qlsv.ui.screen.intro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kma.qlsv.R
import com.kma.qlsv.ui.theme.AppTheme
import com.kma.qlsv.ui.theme.Primary
import com.kma.qlsv.ui.theme.OldStandardTTFontFamily
import com.kma.qlsv.ui.theme.pxToDp

@Composable
fun RoundedBottomHeader(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Primary, // Màu xanh như hình
    text: String = "Quản Lý Hồ Sơ\nSinh Viên"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp) // Chiều cao header
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, height * 0.8f)

                // Vẽ đoạn cong đáy
                quadraticBezierTo(
                    width / 2, height * 1.2f, // Điểm điều khiển (vòm cong xuống)
                    width, height * 0.8f // Điểm kết thúc
                )

                lineTo(width, 0f)
                close()
            }

            drawPath(
                path = path,
                color = backgroundColor
            )
        }

        // Văn bản ở giữa
        Text(
            text = "Quản Lý Hồ Sơ Sinh Viên",
            fontFamily = OldStandardTTFontFamily,
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 54.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .zIndex(1f)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun IntroScreen(modifier: Modifier = Modifier) {
    // Main container
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Blue circle background
        RoundedBottomHeader()
        
        // Background image
        Image(
            painter = painterResource(id = R.drawable.image_intro),
            contentDescription = "Background image",
            modifier = Modifier
                .size(width = 344.pxToDp(), height = 200.pxToDp())
                .offset(x = (-52).pxToDp(), y = 440.pxToDp())
                .zIndex(1f)
        )

        // Decorative shapes
        // Star
        Image(
            painter = painterResource(id = R.drawable.ic_star),
            contentDescription = "Star decoration",
            modifier = Modifier
                .offset(x = 227.pxToDp(), y = 519.pxToDp())
                .zIndex(2f)
        )

        // Polygon
        Image(
            painter = painterResource(id = R.drawable.ic_polygon),
            contentDescription = "Polygon decoration",
            modifier = Modifier
                .offset(x = 280.pxToDp(), y = 468.pxToDp())
                .zIndex(2f)
        )

        // Small circle
        Image(
            painter = painterResource(id = R.drawable.ic_circle),
            contentDescription = "Circle decoration",
            modifier = Modifier
                .offset(x = 287.pxToDp(), y = 574.pxToDp())
                .zIndex(2f)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun IntroScreenPreview() {
    AppTheme {
        IntroScreen(modifier = Modifier)
    }
}