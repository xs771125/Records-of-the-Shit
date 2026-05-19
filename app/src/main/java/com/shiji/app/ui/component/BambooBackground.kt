package com.shiji.app.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.shiji.app.ui.theme.BambooDark
import com.shiji.app.ui.theme.ParchmentLight

@Composable
fun BambooCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(ParchmentLight)
            .padding(16.dp)
    ) {
        // Vertical bamboo line on the left
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineX = 8.dp.toPx()
            drawLine(
                color = BambooDark.copy(alpha = 0.3f),
                start = Offset(lineX, 0f),
                end = Offset(lineX, size.height),
                strokeWidth = 2.dp.toPx()
            )
            // Bamboo node
            val nodeY = size.height * 0.3f
            drawCircle(
                color = BambooDark.copy(alpha = 0.2f),
                radius = 4.dp.toPx(),
                center = Offset(lineX, nodeY)
            )
            drawCircle(
                color = BambooDark.copy(alpha = 0.2f),
                radius = 4.dp.toPx(),
                center = Offset(lineX, size.height * 0.7f)
            )
        }
        content()
    }
}

@Composable
fun BambooDivider(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.padding(vertical = 4.dp)) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
        }
        drawPath(
            path = path,
            color = BambooDark.copy(alpha = 0.3f),
            style = Stroke(width = 1.dp.toPx())
        )
        // Draw nodes along the divider
        val nodeSpacing = 80.dp.toPx()
        var x = 16.dp.toPx()
        while (x < size.width) {
            drawCircle(
                color = BambooDark.copy(alpha = 0.2f),
                radius = 2.dp.toPx(),
                center = Offset(x, 0f)
            )
            x += nodeSpacing
        }
    }
}
