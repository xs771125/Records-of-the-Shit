package com.shiji.app.ui.screen.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiji.app.domain.model.BristolType
import com.shiji.app.ui.theme.ParchmentMedium

@Composable
fun BristolPicker(
    selectedType: Int,
    onTypeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(BristolType.entries.toList()) { bristol ->
            val isSelected = selectedType == bristol.value
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) bristol.displayColor.copy(alpha = 0.15f)
                        else ParchmentMedium
                    )
                    .clickable { onTypeSelected(bristol.value) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .then(
                        if (isSelected) Modifier.background(
                            bristol.displayColor.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        ) else Modifier
                    )
            ) {
                Text(text = bristol.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bristol.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) bristol.displayColor else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = bristol.description,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(bristol.displayColor)
                    )
                }
            }
        }
    }
}
