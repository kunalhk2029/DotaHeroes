package com.app.dotaheroesinfo.ui

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.app.dotaheroesinfo.ui.navigation.Screen
import com.app.dotaheroesinfo.ui.theme.DotaHeroesInfoTheme
import com.app.dotainfo.di.HeroInteractorsModule
import com.app.hero_datasource.cache.HeroCache
import com.app.hero_datasource.network.HeroService
import com.app.hero_datasource_test.cache.FakeHeroCache
import com.app.hero_datasource_test.cache.FakeHeroDatabase
import com.app.hero_datasource_test.network.FakeHeroService
import com.app.hero_datasource_test.network.HeroServiceResponseType
import com.app.hero_domain.HeroAttribute
import com.app.hero_interactors.FilterHeros
import com.app.hero_interactors.GetHeroFromCache
import com.app.hero_interactors.GetHeros
import com.app.hero_interactors.HeroInteractors
import com.app.ui_heroList.coil.FakeImageLoader
import com.app.ui_herodetail.ui.HeroDetail
import com.app.ui_herodetail.ui.HeroDetailViewModel
import com.app.ui_herolist.HeroList
import com.app.ui_herolist.ui.HeroListViewModel
import com.app.ui_herolist.ui.test.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@OptIn(ExperimentalCoilApi::class)
@UninstallModules(HeroInteractorsModule::class)
@HiltAndroidTest
class HeroListEndToEnd {

    @Module
    @InstallIn(SingletonComponent::class)
    object TestHeroInteractorsModule {

        @Provides
        @Singleton
        fun provideHeroCache(): HeroCache {
            return FakeHeroCache(FakeHeroDatabase())
        }

        @Provides
        @Singleton
        fun provideHeroService(): HeroService {
            return FakeHeroService.build(
                type = HeroServiceResponseType.ValidData
            )
        }

        @Provides
        @Singleton
        fun provideHeroInteractors(
            cache: HeroCache,
            service: HeroService
        ): HeroInteractors {
            return HeroInteractors(
                getHeros = GetHeros(
                    cache = cache,
                    service = service,
                ),
                filterHeros = FilterHeros(),
                getHeroFromCache = GetHeroFromCache(
                    cache = cache,
                ),
            )
        }
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val imageLoader: ImageLoader = FakeImageLoader.build(context)

    @Before
    fun before(){
        composeTestRule.activity.setContent {
            DotaHeroesInfoTheme{
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.HeroList.route,
                    builder = {
                        composable(
                            route = Screen.HeroList.route,
                        ){
                            val viewModel: HeroListViewModel = hiltViewModel()
                            HeroList(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                navigateToDetailScreen = { heroId ->
                                    navController.navigate("${Screen.HeroDetail.route}/$heroId")
                                },
                                imageLoader = imageLoader,
                            )
                        }
                        composable(
                            route = Screen.HeroDetail.route + "/{heroId}",
                            arguments = Screen.HeroDetail.arguments,
                        ){
                            val viewModel: HeroDetailViewModel = hiltViewModel()
                            HeroDetail(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                imageLoader = imageLoader
                            )
                        }
                    }
                )
            }
        }
    }

    @Test
    fun testSearchHeroByName(){
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAGING") // For learning the ui tree system

        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextInput("Anti-Mage")
        composeTestRule.onNodeWithTag(TAG_HERO_NAME, useUnmergedTree = true).assertTextEquals(
            "Anti-Mage",
        )
        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextClearance()

        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextInput("Storm Spirit")
        composeTestRule.onNodeWithTag(TAG_HERO_NAME, useUnmergedTree = true).assertTextContains(
            "Storm Spirit",
        )
        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextClearance()

        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextInput("Mirana")
        composeTestRule.onNodeWithTag(TAG_HERO_NAME, useUnmergedTree = true).assertTextEquals(
            "Mirana",
        )
    }

    @Test
    fun testFilterHeroAlphabetically(){
        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_BTN,useUnmergedTree = true).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        // Filter by "Hero" name
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_HERO_CHECKBOX).performClick()

        // Order Descending (z-a)
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DESC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_HERO_NAME, useUnmergedTree = true).assertAny(hasText("Zeus"))

        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_BTN, useUnmergedTree = true).performClick()

        // Order Ascending (a-z)
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_ASC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_HERO_NAME, useUnmergedTree = true).assertAny(hasText("Abaddon"))
    }

    @Test
    fun testFilterHeroByProWins(){
        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_BTN, useUnmergedTree = true).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        // Filter by ProWin %
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_PROWINS_CHECKBOX).performClick()

        // Order Descending (100% - 0%)
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DESC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_HERO_NAME, useUnmergedTree = true).assertAny(hasText("Chen"))

        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_BTN, useUnmergedTree = true).performClick()

        // Order Ascending (0% - 100%)
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_ASC, useUnmergedTree = true).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG_DONE, useUnmergedTree = true).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTag(TAG_HERO_NAME, useUnmergedTree = true).assertAny(hasText("Dawnbreaker"))
    }

    @Test
    fun testFilterHeroByStrength(){
        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_BTN, useUnmergedTree = true).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_STENGTH_CHECKBOX).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm that only STRENGTH heros are showing
        composeTestRule.onAllNodesWithTag(TAG_HERO_PRIMARY_ATTRIBUTE, useUnmergedTree = true).assertAll(hasText(HeroAttribute.Strength.uiValue))
    }

    @Test
    fun testFilterHeroByAgility(){
        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_BTN, useUnmergedTree = true).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_AGILITY_CHECKBOX).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm that only STRENGTH heros are showing
        composeTestRule.onAllNodesWithTag(TAG_HERO_PRIMARY_ATTRIBUTE, useUnmergedTree = true).assertAll(hasText(HeroAttribute.Agility.uiValue))
    }

    @Test
    fun testFilterHeroByIntelligence(){
        // Show the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_BTN, useUnmergedTree = true).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_INT_CHECKBOX).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTag(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm that only STRENGTH heros are showing
        composeTestRule.onAllNodesWithTag(TAG_HERO_PRIMARY_ATTRIBUTE, useUnmergedTree = true).assertAll(hasText(
            HeroAttribute.Intelligence.uiValue))
    }
}