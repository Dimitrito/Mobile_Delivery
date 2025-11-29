package com.mobiledelivery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiledelivery.data.api.models.CategoryResponse
import com.mobiledelivery.domain.models.MenuItem
import com.mobiledelivery.domain.usecases.GetCategoriesUseCase
import com.mobiledelivery.domain.usecases.GetDishesByCategoryUseCase
import com.mobiledelivery.presentation.states.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для роботи з категоріями та стравами
 */
class CategoriesViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getDishesByCategoryUseCase: GetDishesByCategoryUseCase
) : ViewModel() {
    
    // Стан категорій
    private val _categoriesState = MutableStateFlow<UiState<List<CategoryResponse>>>(UiState.Idle)
    val categoriesState: StateFlow<UiState<List<CategoryResponse>>> = _categoriesState.asStateFlow()
    
    // Стан страв для вибраної категорії
    private val _dishesState = MutableStateFlow<UiState<List<MenuItem>>>(UiState.Idle)
    val dishesState: StateFlow<UiState<List<MenuItem>>> = _dishesState.asStateFlow()
    
    // Вибрана категорія
    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()
    
    // Чи було вже завантажено категорії
    private var isInitialized = false
    
    /**
     * Ініціалізує завантаження категорій (викликається з HomeScreen)
     */
    fun initialize() {
        if (isInitialized) return
        isInitialized = true
        loadCategories()
    }
    
    /**
     * Завантажує список категорій
     */
    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            getCategoriesUseCase()
                .onSuccess { categories ->
                    _categoriesState.value = UiState.Success(categories)
                    // Автоматично завантажуємо страви для першої категорії
                    if (categories.isNotEmpty() && _selectedCategoryId.value == null) {
                        selectCategory(categories.first().id)
                    }
                }
                .onFailure { exception ->
                    _categoriesState.value = UiState.Error(exception.message ?: "Помилка завантаження категорій")
                }
        }
    }
    
    /**
     * Вибирає категорію та завантажує страви
     */
    fun selectCategory(categoryId: Int) {
        if (_selectedCategoryId.value == categoryId) return
        
        _selectedCategoryId.value = categoryId
        loadDishesByCategory(categoryId)
    }
    
    /**
     * Завантажує страви для вибраної категорії
     */
    private fun loadDishesByCategory(categoryId: Int) {
        viewModelScope.launch {
            _dishesState.value = UiState.Loading
            getDishesByCategoryUseCase(categoryId)
                .onSuccess { dishes ->
                    _dishesState.value = UiState.Success(dishes)
                }
                .onFailure { exception ->
                    _dishesState.value = UiState.Error(exception.message ?: "Помилка завантаження страв")
                }
        }
    }
    
    /**
     * Оновлює страви для поточної категорії
     */
    fun refreshDishes() {
        _selectedCategoryId.value?.let { categoryId ->
            loadDishesByCategory(categoryId)
        }
    }
}

