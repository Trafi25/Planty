package com.traffipart.polanty.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.plant.ObservePlantsUseCase
import com.traffipart.polanty.domain.usecase.space.ObserveSpacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 *
 * It observes the collection of plants and spaces to provide high-level statistics
 * and dashboard information to the user.
 *
 * @property observePlantsUseCase Use case to observe all plants.
 * @property observeSpacesUseCase Use case to observe all plant spaces.
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val observePlantsUseCase: ObservePlantsUseCase,
        private val observeSpacesUseCase: ObserveSpacesUseCase,
    ) : ViewModel() {
        /**
         * The UI state for the Home screen, providing plant and space counts.
         */
        val uiState: StateFlow<HomeUiState> =
            combine(
                observePlantsUseCase(),
                observeSpacesUseCase(),
            ) { plants, spaces ->
                HomeUiState(
                    plantCount = plants.size,
                    spaceCount = spaces.size,
                    isLoading = false,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState(),
            )
    }
