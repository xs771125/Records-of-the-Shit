package com.shiji.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shiji.app.ui.screen.MainScreen
import com.shiji.app.ui.screen.add.AddRecordScreen
import com.shiji.app.ui.screen.calendar.CalendarScreen
import com.shiji.app.ui.screen.export.ExportScreen
import com.shiji.app.ui.screen.home.HomeScreen
import com.shiji.app.ui.screen.stats.StatsScreen

object Routes {
    const val MAIN = "main"
    const val ADD_RECORD = "add_record?presetTimestamp={presetTimestamp}"
    const val EDIT_RECORD = "edit_record/{recordId}"
    const val CALENDAR = "calendar"
    const val STATS = "stats"
    const val EXPORT = "export"

    fun editRecord(id: Long) = "edit_record/$id"
    fun addRecord(presetTimestamp: Long? = null) =
        if (presetTimestamp != null) "add_record?presetTimestamp=$presetTimestamp"
        else "add_record"
}

private const val ANIM_DURATION = 300

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ANIM_DURATION)
            ) + fadeIn(animationSpec = tween(ANIM_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ANIM_DURATION)
            ) + fadeOut(animationSpec = tween(ANIM_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ANIM_DURATION)
            ) + fadeIn(animationSpec = tween(ANIM_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ANIM_DURATION)
            ) + fadeOut(animationSpec = tween(ANIM_DURATION))
        }
    ) {
        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }

        composable(
            route = Routes.ADD_RECORD,
            arguments = listOf(
                navArgument("presetTimestamp") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val presetTimestamp = backStackEntry.arguments?.getLong("presetTimestamp")?.takeIf { it > 0 }
            AddRecordScreen(
                recordId = null,
                presetTimestamp = presetTimestamp,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_RECORD,
            arguments = listOf(navArgument("recordId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId")
            AddRecordScreen(
                recordId = recordId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CALENDAR) {
            CalendarScreen(
                onRecordClick = { id -> navController.navigate(Routes.editRecord(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATS) {
            StatsScreen(
                onExport = { navController.navigate(Routes.EXPORT) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.EXPORT) {
            ExportScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
