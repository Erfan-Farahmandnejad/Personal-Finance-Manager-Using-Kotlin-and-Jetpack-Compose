package com.example.test.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.CategoryDao
import com.example.test.model.Category
import com.example.test.model.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val categoryDao: CategoryDao
) : ViewModel() {

    var name by mutableStateOf("")
    var type by mutableStateOf(CategoryType.EXPENSE)
    var color by mutableStateOf("#FF0000") // default red
    var errorMessage: String? by mutableStateOf(null)
    private var categoryId: Int? = null

    fun loadCategory(id: Int) {
        viewModelScope.launch {
            val category = categoryDao.getCategoryById(id)
            category?.let {
                categoryId = it.id
                name = it.name
                type = it.type
                color = it.color
                Log.d("CategoryDebug", "Loaded category: name=${it.name}, type=${it.type}")
            }
        }
    }

    fun saveCategory(onDone: () -> Unit) {
        viewModelScope.launch {

            val existingCategory = categoryDao.getCategoryByName(name)
            if (existingCategory != null && existingCategory.id != categoryId) {
                errorMessage = "A category with this name already exists"
                return@launch
            }

            val category = Category(
                id = categoryId ?: 0,
                name = name,
                type = type,
                color = color
            )
            Log.d("CategoryDebug", "Saving category: name=${category.name}, type=${category.type}")
            categoryDao.insert(category)
            onDone()
        }
    }
}
