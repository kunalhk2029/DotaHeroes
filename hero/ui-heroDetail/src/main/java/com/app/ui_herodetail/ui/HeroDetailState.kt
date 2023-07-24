package com.app.ui_herodetail.ui

import com.app.core.ProgressBarState
import com.app.core.Queue
import com.app.core.UIComponent
import com.app.hero_domain.Hero

data class HeroDetailState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val hero: Hero? = null,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf())
)