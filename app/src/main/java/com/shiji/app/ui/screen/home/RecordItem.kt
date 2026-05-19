package com.shiji.app.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiji.app.data.entity.Record
import com.shiji.app.domain.model.BristolType
import com.shiji.app.domain.model.StoolColor
import com.shiji.app.ui.theme.BambooLight
import com.shiji.app.ui.theme.ParchmentLight
import com.shiji.app.ui.theme.SealRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordItem(
    record: Record,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = ParchmentLight),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row: date + bristol + emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimestamp(record.timestamp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val bristol = BristolType.fromValue(record.bristolType)
                    Text(text = bristol.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bristol.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = bristol.displayColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Color, odor, smoothness
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val color = StoolColor.entries.find { it.value == record.color }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "●", color = color?.displayColor ?: MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = color?.label ?: record.color,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "臭 ${"★".repeat(record.odorLevel)}${"☆".repeat(5 - record.odorLevel)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp
                )
                Text(
                    text = "顺 ${"★".repeat(record.smoothness)}${"☆".repeat(5 - record.smoothness)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Commentary
            Text(
                text = record.commentary,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (!record.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "「${record.note}」",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINESE)
    return sdf.format(Date(timestamp))
}
