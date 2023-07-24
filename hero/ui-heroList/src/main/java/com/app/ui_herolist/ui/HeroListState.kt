package com.app.ui_herolist.ui

import com.app.core.ProgressBarState
import com.app.core.Queue
import com.app.core.UIComponent
import com.app.core.UIComponentState
import com.app.hero_domain.Hero
import com.app.hero_domain.HeroAttribute
import com.app.hero_domain.HeroFilter

data class HeroListState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val heros: List<Hero> = listOf(),
    val filteredHeros: List<Hero> = listOf(),
    val heroName: String = "",
    val heroFilter: HeroFilter=HeroFilter.Hero(),
    val primaryAttribute: HeroAttribute = HeroAttribute.Unknown,
    val filterDialogState:UIComponentState = UIComponentState.Hide,
    val errorQueue: Queue<UIComponent> = Queue(mutableListOf())
)
