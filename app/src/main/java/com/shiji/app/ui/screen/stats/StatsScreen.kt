package com.shiji.app.ui.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shiji.app.ShijiApplication
import com.shiji.app.domain.model.BristolType
import com.shiji.app.ui.theme.BambooLight
import com.shiji.app.ui.theme.GoldAccent
import com.shiji.app.ui.theme.ParchmentLight
import com.shiji.app.ui.theme.SealRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    val viewModel: StatsViewModel = viewModel(
        factory = StatsViewModel.Factory(ShijiApplication.instance.repository)
    )
    val stats by viewModel.stats.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("本纪统计", fontWeight = FontWeight.Bold, letterSpacing = 4.sp) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                },
                actions = {
                    if (stats.totalRecords > 0) {
                        IconButton(onClick = onExport) {
                            Icon(Icons.Default.Share, contentDescription = "导出",
                                tint = MaterialTheme.colorScheme.onSurface)
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Summary banner
                SummaryBanner(
                    totalRecords = stats.totalRecords,
                    totalDays = stats.totalDaysRecorded,
                    currentStreak = stats.currentStreak
                )

                // Key stats grid (2x2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "最长间隔",
                        value = "${stats.longestIntervalDays}天",
                        subtitle = "不屈不挠",
                        modifier = Modifier.weight(1f),
                        emoji = "📅"
                    )
                    StatCard(
                        title = "黄金时段",
                        value = stats.goldenHour,
                        subtitle = "黄金时间",
                        modifier = Modifier.weight(1f),
                        emoji = "⏰"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "最长连击",
                        value = "${stats.longestStreak}天",
                        subtitle = "七连绝世",
                        modifier = Modifier.weight(1f),
                        emoji = "🔥"
                    )
                    StatCard(
                        title = "平均形态",
                        value = String.format("%.1f", stats.averageBristol),
                        subtitle = stats.mostCommonBristol,
                        modifier = Modifier.weight(1f),
                        emoji = "📊"
                    )
                }

                // Bristol distribution
                Card(
                    colors = CardDefaults.cardColors(containerColor = ParchmentLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "形态分布",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val maxCount = stats.bristolDistribution.values.maxOrNull()?.toFloat() ?: 1f
                        BristolType.entries.forEach { bristol ->
                            val count = stats.bristolDistribution[bristol.value] ?: 0
                            val ratio = count / maxCount
                            DistributionBar(
                                label = "${bristol.emoji} ${bristol.label}",
                                count = count,
                                ratio = ratio,
                                color = bristol.displayColor
                            )
                            if (bristol != BristolType.entries.last()) {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }

                // Color distribution
                Card(
                    colors = CardDefaults.cardColors(containerColor = ParchmentLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "颜色分布",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            stats.colorDistribution.forEach { (colorName, count) ->
                                val stoolColor = com.shiji.app.domain.model.StoolColor.entries
                                    .find { it.value == colorName }
                                ColorDot(
                                    color = stoolColor?.displayColor ?: MaterialTheme.colorScheme.onSurface,
                                    label = stoolColor?.label ?: colorName,
                                    count = count
                                )
                            }
                        }
                    }
                }

                // Date range
                if (stats.startDate > 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ParchmentLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "记录跨度",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "起：${formatDate(stats.startDate)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "讫：${formatDate(stats.endDate)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Achievements
                val unlockedCount = achievements.count { it.isUnlocked }
                if (achievements.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ParchmentLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "成就功勋",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$unlockedCount/${achievements.size}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            achievements.forEach { achievement ->
                                AchievementRow(achievement = achievement)
                                if (achievement != achievements.last()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AchievementRow(achievement: com.shiji.app.domain.model.Achievement) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (achievement.isUnlocked) achievement.emoji else "🔒",
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = achievement.rank,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (achievement.isUnlocked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
            Text(
                text = achievement.condition,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (achievement.isUnlocked) 0.6f else 0.3f)
            )
        }
    }
}

@Composable
private fun SummaryBanner(
    totalRecords: Int,
    totalDays: Int,
    currentStreak: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SealRed),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(value = "$totalRecords", label = "史录总数")
            SummaryItem(value = "$totalDays", label = "记录日数")
            SummaryItem(value = "$currentStreak", label = "当前连击")
        }
    }
}

@Composable
private fun SummaryItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = ParchmentLight,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = ParchmentLight.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    emoji: String = ""
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ParchmentLight),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = GoldAccent
            )
        }
    }
}

@Composable
private fun DistributionBar(
    label: String,
    count: Int,
    ratio: Float,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(80.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BambooLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio.coerceIn(0.05f, 1f))
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = 0.6f))
            )
        }
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun RowScope.ColorDot(
    color: androidx.compose.ui.graphics.Color,
    label: String,
    count: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年M月d日", Locale.CHINESE)
    return sdf.format(Date(timestamp))
}
