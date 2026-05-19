package com.shiji.app.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shiji.app.ShijiApplication
import com.shiji.app.data.dao.DayCount
import com.shiji.app.data.entity.Record
import com.shiji.app.domain.model.BristolType
import com.shiji.app.ui.screen.home.RecordItem
import com.shiji.app.ui.theme.BambooDark
import com.shiji.app.ui.theme.DensityHigh
import com.shiji.app.ui.theme.DensityLow
import com.shiji.app.ui.theme.DensityMedium
import com.shiji.app.ui.theme.ParchmentLight
import com.shiji.app.ui.theme.ParchmentMedium
import com.shiji.app.ui.theme.SealRed
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onRecordClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onAddClick: (() -> Unit)? = null,
    onDaySelected: ((String) -> Unit)? = null
) {
    val viewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModel.Factory(ShijiApplication.instance.repository)
    )
    val state by viewModel.uiState.collectAsState()
    val selectedRecords by viewModel.selectedDayRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("历 书", fontWeight = FontWeight.Bold, letterSpacing = 4.sp) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Month selector
            MonthHeader(
                year = state.currentYear,
                month = state.currentMonth,
                onPrevious = { viewModel.previousMonth() },
                onNext = { viewModel.nextMonth() }
            )

            // Weekday headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        color = if (day == "日" || day == "六") SealRed.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            CalendarGrid(
                year = state.currentYear,
                month = state.currentMonth,
                dailyCounts = state.dailyCounts,
                selectedDate = state.selectedDate,
                onDayClick = { dateKey ->
                        viewModel.selectDay(dateKey)
                        onDaySelected?.invoke(dateKey)
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selected day records
            if (selectedRecords.isNotEmpty()) {
                Text(
                    text = "当日史录 (${selectedRecords.size}条)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(selectedRecords, key = { it.id }) { record ->
                        RecordItem(record = record, onClick = { onRecordClick(record.id) })
                    }
                }
            } else if (state.selectedDate.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "是日无记 💩",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MonthHeader(
    year: Int,
    month: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "上月")
        }
        Text(
            text = "${year}年 ${month}月",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "下月")
        }
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    dailyCounts: List<DayCount>,
    selectedDate: String,
    onDayClick: (String) -> Unit
) {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday

    val countMap = dailyCounts.associate { it.day to it.count }

    val today = Calendar.getInstance()
    val todayKey = "${today.get(Calendar.YEAR)}-${String.format("%02d", today.get(Calendar.MONTH) + 1)}-${String.format("%02d", today.get(Calendar.DAY_OF_MONTH))}"

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        var dayCounter = 1
        val totalCells = firstDayOfWeek - 1 + daysInMonth
        val weeks = (totalCells + 6) / 7

        for (week in 0 until weeks) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (weekday in 1..7) {
                    val cellIndex = week * 7 + (weekday - 1)
                    if (cellIndex < firstDayOfWeek - 1 || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val day = dayCounter
                        val dateKey = "${year}-${String.format("%02d", month)}-${String.format("%02d", day)}"
                        val count = countMap[dateKey] ?: 0
                        val isSelected = dateKey == selectedDate
                        val isToday = dateKey == todayKey

                        DayCell(
                            day = day,
                            count = count,
                            isSelected = isSelected,
                            isToday = isToday,
                            onClick = { onDayClick(dateKey) },
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    count: Int,
    isSelected: Boolean,
    isToday: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> SealRed.copy(alpha = 0.15f)
        isToday -> SealRed.copy(alpha = 0.1f)
        count >= 3 -> DensityHigh.copy(alpha = 0.3f)
        count == 2 -> DensityMedium.copy(alpha = 0.3f)
        count == 1 -> DensityLow.copy(alpha = 0.5f)
        else -> ParchmentLight
    }

    val textColor = when {
        isSelected || isToday -> SealRed
        else -> MaterialTheme.colorScheme.onSurface
    }

    val fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal

    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight,
            color = textColor
        )

        if (count > 0) {
            Text(
                text = "💩",
                fontSize = 9.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-1).dp, y = (-1).dp)
            )
        }
    }
}
