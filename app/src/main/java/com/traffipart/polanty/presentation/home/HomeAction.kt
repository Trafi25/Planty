package com.traffipart.polanty.presentation.home

sealed interface HomeAction {
    data class CompleteCareTask(
        val task: HomeCareTaskUiModel,
    ) : HomeAction
}
