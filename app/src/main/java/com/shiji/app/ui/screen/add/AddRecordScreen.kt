package com.shiji.app.ui.screen.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shiji.app.ShijiApplication
import com.shiji.app.domain.model.StoolColor
import com.shiji.app.ui.component.StarRating
import com.shiji.app.ui.theme.BambooDark
import com.shiji.app.ui.theme.BambooLight
import com.shiji.app.ui.theme.ParchmentMedium
import com.shiji.app.ui.theme.SealRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    recordId: Long? = null,
    presetTimestamp: Long? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddRecordViewModel = viewModel(
        factory = AddRecordViewModel.Factory(
            ShijiApplication.instance.repository,
            recordId,
            presetTimestamp
        )
    )
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditing) "修改史录" else "秉笔直书",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Date & Time picker
            DateTimeSection(
                timestamp = state.timestamp,
                onTimestampChange = { viewModel.updateTimestamp(it) }
            )

            // Bristol type picker
            Column {
                Text(
                    text = "形态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                BristolPicker(
                    selectedType = state.bristolType,
                    onTypeSelected = { viewModel.updateBristolType(it) }
                )
            }

            // Color picker
            Column {
                Text(
                    text = "颜色",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StoolColor.entries.forEach { stoolColor ->
                        val isSelected = state.color == stoolColor.value
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) stoolColor.displayColor.copy(alpha = 0.2f)
                                    else ParchmentMedium
                                )
                                .clickable { viewModel.updateColor(stoolColor.value) }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = stoolColor.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) stoolColor.displayColor
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Odor level
            Column {
                Text(
                    text = "气味等级",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                StarRating(
                    rating = state.odorLevel,
                    onRatingChange = { viewModel.updateOdorLevel(it) }
                )
            }

            // Smoothness
            Column {
                Text(
                    text = "顺畅度",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                StarRating(
                    rating = state.smoothness,
                    onRatingChange = { viewModel.updateSmoothness(it) }
                )
            }

            // Note
            OutlinedTextField(
                value = state.note,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text("备注（可选）") },
                placeholder = { Text("如有补充，在此记录...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Save button
            Button(
                onClick = { viewModel.save { onBack() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SealRed,
                    contentColor = ParchmentMedium
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(
                    text = if (state.isEditing) "修改史录" else "秉笔直书，载入史册",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DateTimeSection(
    timestamp: Long,
    onTimestampChange: (Long) -> Unit
) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance() }

    Column {
        Text(
            text = "时间",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date button
            Text(
                text = formatDate(timestamp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ParchmentMedium)
                    .clickable {
                        cal.timeInMillis = timestamp
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                cal.set(Calendar.YEAR, year)
                                cal.set(Calendar.MONTH, month)
                                cal.set(Calendar.DAY_OF_MONTH, day)
                                onTimestampChange(cal.timeInMillis)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            // Time button
            Text(
                text = formatTime(timestamp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ParchmentMedium)
                    .clickable {
                        cal.timeInMillis = timestamp
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                cal.set(Calendar.HOUR_OF_DAY, hour)
                                cal.set(Calendar.MINUTE, minute)
                                onTimestampChange(cal.timeInMillis)
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日", Locale.CHINESE)
    return sdf.format(Date(timestamp))
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.CHINESE)
    return sdf.format(Date(timestamp))
}
