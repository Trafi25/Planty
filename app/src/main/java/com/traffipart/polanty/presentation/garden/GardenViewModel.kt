package com.traffipart.polanty.presentation.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.ObservePlantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GardenViewModel
    @Inject
    constructor(
        private val observePlantsUseCase: ObservePlantsUseCase,
    ) : ViewModel() {
        val uiState =
            observePlantsUseCase()
                .map { plants ->
                    GardenUiState(
                        plants = plants,
                        isLoading = false,
                    )
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = GardenUiState(),
                )
    }
