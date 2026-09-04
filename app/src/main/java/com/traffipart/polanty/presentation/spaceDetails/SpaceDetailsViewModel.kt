package com.traffipart.polanty.presentation.spaceDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.plant.ObservePlantsBySpaceUseCase
import com.traffipart.polanty.domain.usecase.space.ObserveSpaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SpaceDetailsViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val observeSpaceUseCase: ObserveSpaceUseCase,
        private val observePlantsBySpaceUseCase: ObservePlantsBySpaceUseCase,
    ) : ViewModel() {
        private val spaceId = savedStateHandle.get<Long>("spaceId")

        val uiState: StateFlow<SpaceDetailsUiState> =
            if (spaceId != null) {
                combine(
                    observeSpaceUseCase(spaceId),
                    observePlantsBySpaceUseCase(spaceId),
                ) { space, plants ->
                    SpaceDetailsUiState(
                        space = space,
                        plants = plants,
                        isLoading = false,
                    )
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = SpaceDetailsUiState(),
                )
            } else {
                MutableStateFlow(SpaceDetailsUiState(isLoading = false))
            }
    }
