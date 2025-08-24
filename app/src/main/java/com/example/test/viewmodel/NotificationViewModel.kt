package com.example.test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.NotificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val dao: NotificationDao
) : ViewModel() {
    val notifications = dao.getAll().asLiveData()

    fun markAsRead(id: Int) = viewModelScope.launch {
        dao.markAsRead(id)
    }

    fun clearAll() = viewModelScope.launch {
        dao.clearAll()
    }
}
