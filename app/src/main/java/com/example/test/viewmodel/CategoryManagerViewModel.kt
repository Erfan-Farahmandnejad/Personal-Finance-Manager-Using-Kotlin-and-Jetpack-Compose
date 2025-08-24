package com.example.test.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.CategoryDao
import com.example.test.model.Category
import com.example.test.model.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryManagerViewModel @Inject constructor(
    private val categoryDao: CategoryDao
) : ViewModel() {
    private val _currentType = MutableLiveData(CategoryType.EXPENSE)
    val currentType: LiveData<CategoryType> = _currentType

    val categories = currentType.switchMap { type ->
        categoryDao.getCategoriesByType(type.toString()).asLiveData()
    }

    fun deleteCategory(id: Int) = viewModelScope.launch {
        categoryDao.deleteById(id)
    }

    fun switchType(type: CategoryType) {
        _currentType.value = type
    }
}
