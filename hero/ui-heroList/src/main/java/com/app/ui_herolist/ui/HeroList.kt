package com.app.ui_herolist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import coil.ImageLoader
import com.app.components.DefaultScreenUI
import com.app.core.ProgressBarState
import com.app.core.UIComponentState
import com.app.ui_herolist.components.HeroListFilter
import com.app.ui_herolist.components.HeroListItem
import com.app.ui_herolist.components.HeroListToolbar
import com.app.ui_herolist.ui.HeroListEvents
import com.app.ui_herolist.ui.HeroListState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun HeroList(
    state: HeroListState,
    events: (HeroListEvents) -> Unit,
    imageLoader: ImageLoader,
    navigateToDetailScreen: (id: Int) -> Unit,
) {
    DefaultScreenUI(
        queue=state.errorQueue,
        onRemoveHeadFromQueue={
          events(HeroListEvents.OnRemoveHeadFromQueue)
        },
        progressBarState = state.progressBarState
    ){

        Column {
            HeroListToolbar(heroName = state.heroName, onHeroNameChanged = {
                events(HeroListEvents.UpdateHeroName(it))
            }, onExecuteSearch = {
                events(HeroListEvents.FilterHeros)
            },
                onShowFilterDialog = {
                    events(HeroListEvents.UpdateFilterDialogState(UIComponentState.Show))
                })
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(state.filteredHeros) { hero ->
                    HeroListItem(
                        hero = hero,
                        onSelectHero = { heroId ->
                            navigateToDetailScreen(heroId)
                        },
                        imageLoader = imageLoader
                    )
                }
            }
        }

        if (state.filterDialogState is UIComponentState.Show) {
            HeroListFilter(heroFilter = state.heroFilter, onUpdateHeroFilter = { heroFilter ->
                events(HeroListEvents.UpdateHeroFilter(heroFilter))
            },
                attributeFilter = state.primaryAttribute,
                onUpdateAttributeFilter = { heroAttribute ->
                    events(HeroListEvents.UpdateAttributeFilter(heroAttribute))
                }, onCloseDialog = {
                    events(HeroListEvents.UpdateFilterDialogState(UIComponentState.Hide))
                })
        }
    }
}












