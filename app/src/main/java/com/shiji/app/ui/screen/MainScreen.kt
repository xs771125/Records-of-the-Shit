package com.shiji.app.ui.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiji.app.ui.navigation.Routes
import com.shiji.app.ui.screen.calendar.CalendarScreen
import com.shiji.app.ui.screen.home.HomeScreen
import com.shiji.app.ui.screen.stats.StatsScreen
import com.shiji.app.ui.theme.ParchmentLight
import com.shiji.app.ui.theme.ParchmentMedium
import com.shiji.app.ui.theme.SealRed
import java.util.Calendar

@Composable
fun MainScreen(navController: NavController) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var calendarSelectedDate by rememberSaveable { mutableStateOf("") }
    val pagerState = rememberPagerState(pageCount = { 3 })

    // Sync bottom nav tap → pager
    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }

    // Sync pager → bottom nav (only when settled)
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTab = pagerState.currentPage
        }
    }

    val tabs = listOf(
        TabItem("历书", Icons.Default.CalendarMonth),
        TabItem("本纪", Icons.AutoMirrored.Filled.ListAlt),
        TabItem("统计", Icons.Default.QueryStats)
    )

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0 || selectedTab == 1) {
                FloatingActionButton(
                    onClick = {
                        when (selectedTab) {
                            0 -> {
                                val timestamp = if (calendarSelectedDate.isNotEmpty()) {
                                    val parts = calendarSelectedDate.split("-").map { it.toInt() }
                                    val cal = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, parts[0])
                                        set(Calendar.MONTH, parts[1] - 1)
                                        set(Calendar.DAY_OF_MONTH, parts[2])
                                        set(Calendar.HOUR_OF_DAY, 12)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    cal.timeInMillis
                                } else {
                                    Calendar.getInstance().timeInMillis
                                }
                                navController.navigate("${Routes.ADD_RECORD}?presetTimestamp=$timestamp")
                            }
                            1 -> navController.navigate(Routes.ADD_RECORD)
                        }
                    },
                    containerColor = SealRed,
                    contentColor = ParchmentMedium
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加记录",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            CustomSlidingBottomBar(
                tabs = tabs,
                selectedTab = selectedTab,
                pagerCurrentPage = pagerState.currentPage,
                pagerOffset = pagerState.currentPageOffsetFraction,
                onTabClick = { selectedTab = it }
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> CalendarScreen(
                    onRecordClick = { id -> navController.navigate(Routes.editRecord(id)) },
                    onDaySelected = { dateKey -> calendarSelectedDate = dateKey }
                )
                1 -> HomeScreen(
                    onAddClick = { navController.navigate(Routes.ADD_RECORD) },
                    onRecordClick = { id -> navController.navigate(Routes.editRecord(id)) }
                )
                2 -> StatsScreen(
                    onExport = { navController.navigate(Routes.EXPORT) }
                )
            }
        }
    }
}

@Composable
private fun CustomSlidingBottomBar(
    tabs: List<TabItem>,
    selectedTab: Int,
    pagerCurrentPage: Int,
    pagerOffset: Float,
    onTabClick: (Int) -> Unit
) {
    val density = LocalDensity.current
    var barWidthPx by remember { mutableFloatStateOf(1f) }
    val tabCount = tabs.size
    val indicatorHeight = 28.dp
    val indicatorHorizontalPadding = 6.dp
    val barPaddingVertical = 8.dp

    // Use pagerCurrentPage (updates real-time) for continuous scroll position
    val tabWidthPx = barWidthPx / tabCount
    val indicatorWidthPx = tabWidthPx - with(density) { indicatorHorizontalPadding.toPx() * 2 }
    val scrollFraction = pagerCurrentPage + pagerOffset
    val indicatorLeftPx = scrollFraction * tabWidthPx + (tabWidthPx - indicatorWidthPx) / 2

    val indicatorOffset by animateDpAsState(
        targetValue = with(density) { indicatorLeftPx.toDp() },
        animationSpec = if (pagerOffset == 0f) tween(250) else tween(0),
        label = "indicator"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = with(density) { indicatorWidthPx.toDp() },
        animationSpec = if (pagerOffset == 0f) tween(250) else tween(0),
        label = "indicator_w"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParchmentLight)
            .padding(horizontal = indicatorHorizontalPadding, vertical = barPaddingVertical)
            .onSizeChanged { barWidthPx = it.width.toFloat() }
    ) {
        // Animated pill indicator — sits behind icons, above text
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .height(indicatorHeight)
                .clip(RoundedCornerShape(14.dp))
                .background(SealRed.copy(alpha = 0.15f))
        )

        // Tab items
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onTabClick(index) }
                        .padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (isSelected) SealRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) SealRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

private data class TabItem(val label: String, val icon: ImageVector)
