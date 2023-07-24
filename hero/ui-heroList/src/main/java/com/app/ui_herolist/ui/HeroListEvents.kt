package com.app.ui_herolist.ui

import com.app.core.UIComponentState
import com.app.hero_domain.HeroAttribute
import com.app.hero_domain.HeroFilter

sealed class HeroListEvents {

    object GetHeros : HeroListEvents()

    object FilterHeros: HeroListEvents()

    data class UpdateHeroName(
        val heroName: String,
    ): HeroListEvents()

    data class UpdateHeroFilter(
        val heroFilter: HeroFilter,
    ): HeroListEvents()

    data class UpdateAttributeFilter(
        val attribute: HeroAttribute
    ): HeroListEvents()

    data class UpdateFilterDialogState(
        val uiComponentState: UIComponentState
    ): HeroListEvents()

    object OnRemoveHeadFromQueue: HeroListEvents()

}
