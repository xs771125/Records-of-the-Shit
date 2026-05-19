package com.shiji.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shiji.app.data.entity.Record
import com.shiji.app.data.repository.RecordRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RecordRepository
) : ViewModel() {

    val records: StateFlow<List<Record>> = repository.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var lastDeletedRecord: Record? = null

    private val _deleteEvent = Channel<Unit>(Channel.BUFFERED)
    val deleteEvent = _deleteEvent.receiveAsFlow()

    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            lastDeletedRecord = record
            repository.deleteById(record.id)
            _deleteEvent.send(Unit)
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            lastDeletedRecord?.let { repository.insert(it) }
            lastDeletedRecord = null
        }
    }

    class Factory(private val repository: RecordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
