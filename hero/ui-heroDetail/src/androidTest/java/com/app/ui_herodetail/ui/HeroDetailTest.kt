package com.app.ui_herodetail.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.app.hero_datasource_test.network.data.HeroDataValid
import com.app.hero_datasource_test.network.serializeHeroData
import com.app.ui_heroList.coil.FakeImageLoader
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoilApi::class)
@ExperimentalAnimationApi
class HeroDetailTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val imageLoader: ImageLoader = FakeImageLoader.build(context)
    private val heroData = serializeHeroData(HeroDataValid.data)

    @Test
    fun isHeroDataShown() {
        // choose a hero at random
        val hero = heroData.get(Random.nextInt(0, heroData.size - 1))
        composeTestRule.setContent {
            val state = remember {
                HeroDetailState(
                    hero = hero,
                )
            }
            HeroDetail(
                state = state,
                events = {},
                imageLoader = imageLoader,
            )
        }

        composeTestRule.onNodeWithText(hero.localizedName).assertIsDisplayed()
        composeTestRule.onNodeWithText(hero.primaryAttribute.uiValue).assertIsDisplayed()
        composeTestRule.onNodeWithText(hero.attackType.uiValue).assertIsDisplayed()

        val proWinPercentage = (hero.proWins.toDouble() / hero.proPick.toDouble() * 100).toInt()
        composeTestRule.onNodeWithText("$proWinPercentage %").assertIsDisplayed()

        val turboWinPercentage =
            (hero.turboWins.toDouble() / hero.turboPicks.toDouble() * 100).toInt()
        composeTestRule.onNodeWithText("$turboWinPercentage %").assertIsDisplayed()
    }

}