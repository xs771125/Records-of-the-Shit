package com.shiji.app.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shiji.app.ui.theme.StarEmpty
import com.shiji.app.ui.theme.StarFilled

@Composable
fun StarRating(
    rating: Int,
    maxRating: Int = 5,
    onRatingChange: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        (1..maxRating).forEach { star ->
            val isFilled = star <= rating
            Text(
                text = if (isFilled) "★" else "☆",
                color = if (isFilled) StarFilled else StarEmpty,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = if (onRatingChange != null) {
                    Modifier.clickable { onRatingChange(star) }
                } else Modifier
            )
        }
    }
}
