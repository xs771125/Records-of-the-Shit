package com.shiji.app.ui.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shiji.app.data.dao.DayCount
import com.shiji.app.data.entity.Record
import com.shiji.app.data.repository.RecordRepository
import com.shiji.app.util.toDateKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel(
    private val repository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _selectedDayRecords = MutableStateFlow<List<Record>>(emptyList())
    val selectedDayRecords: StateFlow<List<Record>> = _selectedDayRecords.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            repository.getAllRecords().collect { records ->
                val dailyCounts = records.groupBy { toDateKey(it.timestamp) }
                    .map { (day, dayRecords) -> DayCount(day = day, count = dayRecords.size) }
                _uiState.value = _uiState.value.copy(dailyCounts = dailyCounts)

                val selected = _uiState.value.selectedDate
                if (selected.isNotEmpty()) {
                    refreshSelectedDayRecords(selected)
                }
            }
        }
    }

    private fun refreshSelectedDayRecords(dateKey: String) {
        viewModelScope.launch {
            val parts = dateKey.split("-").map { it.toInt() }
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, parts[0])
                set(Calendar.MONTH, parts[1] - 1)
                set(Calendar.DAY_OF_MONTH, parts[2])
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = cal.timeInMillis - 1
            _selectedDayRecords.value = repository.getRecordsBetween(startOfDay, endOfDay)
        }
    }

    fun updateMonth(year: Int, month: Int) {
        _uiState.value = _uiState.value.copy(currentYear = year, currentMonth = month)
    }

    fun selectDay(dateKey: String) {
        _uiState.value = _uiState.value.copy(selectedDate = dateKey)
        refreshSelectedDayRecords(dateKey)
    }

    fun previousMonth() {
        val state = _uiState.value
        if (state.currentMonth == 1) {
            updateMonth(state.currentYear - 1, 12)
        } else {
            updateMonth(state.currentYear, state.currentMonth - 1)
        }
    }

    fun nextMonth() {
        val state = _uiState.value
        if (state.currentMonth == 12) {
            updateMonth(state.currentYear + 1, 1)
        } else {
            updateMonth(state.currentYear, state.currentMonth + 1)
        }
    }

    class Factory(private val repository: RecordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(repository) as T
        }
    }
}

data class CalendarUiState(
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val selectedDate: String = "",
    val dailyCounts: List<DayCount> = emptyList()
)
