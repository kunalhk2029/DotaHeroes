package com.app.core


sealed class ProgressBarState{

    object Loading: ProgressBarState()

    object Idle: ProgressBarState()
}