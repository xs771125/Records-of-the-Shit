package com.shiji.app.ui.screen.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shiji.app.data.repository.RecordRepository
import com.shiji.app.domain.AchievementCalculator
import com.shiji.app.domain.StatisticsCalculator
import com.shiji.app.domain.model.Achievement
import com.shiji.app.domain.model.StatisticsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val repository: RecordRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(StatisticsData())
    val stats: StateFlow<StatisticsData> = _stats.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            repository.getAllRecords().collect { records ->
                _stats.value = StatisticsCalculator.calculate(records)
                _achievements.value = AchievementCalculator.calculate(records)
            }
        }
    }

    class Factory(private val repository: RecordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StatsViewModel(repository) as T
        }
    }
}
