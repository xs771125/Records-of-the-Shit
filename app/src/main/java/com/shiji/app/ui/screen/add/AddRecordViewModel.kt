package com.shiji.app.ui.screen.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shiji.app.data.entity.Record
import com.shiji.app.data.repository.RecordRepository
import com.shiji.app.domain.CommentaryGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddRecordViewModel(
    private val repository: RecordRepository,
    private val recordId: Long?,
    presetTimestamp: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddRecordUiState(
            timestamp = presetTimestamp ?: System.currentTimeMillis()
        )
    )
    val uiState: StateFlow<AddRecordUiState> = _uiState.asStateFlow()

    init {
        if (recordId != null) {
            viewModelScope.launch {
                val record = repository.getRecordById(recordId)
                if (record != null) {
                    _uiState.value = AddRecordUiState(
                        timestamp = record.timestamp,
                        bristolType = record.bristolType,
                        color = record.color,
                        odorLevel = record.odorLevel,
                        smoothness = record.smoothness,
                        note = record.note ?: "",
                        isEditing = true
                    )
                }
            }
        }
    }

    fun updateTimestamp(timestamp: Long) {
        _uiState.value = _uiState.value.copy(timestamp = timestamp)
    }

    fun updateBristolType(type: Int) {
        _uiState.value = _uiState.value.copy(bristolType = type)
    }

    fun updateColor(color: String) {
        _uiState.value = _uiState.value.copy(color = color)
    }

    fun updateOdorLevel(level: Int) {
        _uiState.value = _uiState.value.copy(odorLevel = level)
    }

    fun updateSmoothness(level: Int) {
        _uiState.value = _uiState.value.copy(smoothness = level)
    }

    fun updateNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun save(onComplete: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val record = Record(
                id = if (state.isEditing) recordId ?: 0 else 0,
                timestamp = state.timestamp,
                bristolType = state.bristolType,
                color = state.color,
                odorLevel = state.odorLevel,
                smoothness = state.smoothness,
                commentary = CommentaryGenerator.generate(
                    Record(
                        timestamp = state.timestamp,
                        bristolType = state.bristolType,
                        color = state.color,
                        odorLevel = state.odorLevel,
                        smoothness = state.smoothness,
                        commentary = "",
                        note = state.note.ifBlank { null }
                    )
                ),
                note = state.note.ifBlank { null }
            )
            if (state.isEditing) {
                repository.update(record)
            } else {
                repository.insert(record)
            }
            onComplete()
        }
    }

    class Factory(
        private val repository: RecordRepository,
        private val recordId: Long?,
        private val presetTimestamp: Long? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddRecordViewModel(repository, recordId, presetTimestamp) as T
        }
    }
}

data class AddRecordUiState(
    val timestamp: Long = System.currentTimeMillis(),
    val bristolType: Int = 4,
    val color: String = "棕色",
    val odorLevel: Int = 3,
    val smoothness: Int = 3,
    val note: String = "",
    val isEditing: Boolean = false
)
