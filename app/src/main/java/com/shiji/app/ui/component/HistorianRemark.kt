package com.shiji.app.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.shiji.app.ui.theme.BambooLight
import com.shiji.app.ui.theme.InkGray
import com.shiji.app.ui.theme.SealRed

@Composable
fun HistorianRemark(
    commentary: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BambooLight,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Text(
                    text = "太史公曰",
                    style = MaterialTheme.typography.labelLarge,
                    color = SealRed,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "「",
                    color = InkGray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = commentary.removePrefix("太史公曰："),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text(
                text = "」",
                color = InkGray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(androidx.compose.ui.Alignment.End)
            )
        }
    }
}
