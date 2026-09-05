package com.traffipart.polanty.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.space.InitializeDefaultSpacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Root ViewModel for the entire application.
 *
 * Handles global initialization tasks such as setting up default plant spaces.
 *
 * @property initializeDefaultSpacesUseCase Use case to ensure default spaces exist in the database.
 */
@HiltViewModel
class PlantyRootViewModel
    @Inject
    constructor(
        private val initializeDefaultSpacesUseCase: InitializeDefaultSpacesUseCase,
    ) : ViewModel() {
        init {
            initializeApp()
        }

        private fun initializeApp() {
            viewModelScope.launch {
                initializeDefaultSpacesUseCase()
            }
        }
    }
