package com.app.dotaheroesinfo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.ImageLoader
import com.app.dotaheroesinfo.ui.navigation.Screen
import com.app.dotaheroesinfo.ui.theme.DotaHeroesInfoTheme
import com.app.ui_herodetail.ui.HeroDetail
import com.app.ui_herodetail.ui.HeroDetailViewModel
import com.app.ui_herolist.HeroList
import com.app.ui_herolist.ui.HeroListViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DotaHeroesInfoTheme {

                val navController = rememberAnimatedNavController()

                BoxWithConstraints {
                    AnimatedNavHost(navController = navController,
                        startDestination = Screen.HeroList.route,
                        builder = {
                            addHeroList(imageLoader = imageLoader,
                                navController = navController,
                                width = constraints.maxWidth / 2)

                            addHeroDetail(imageLoader = imageLoader,
                                width = constraints.maxWidth / 2)
                        })
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addHeroList(
    imageLoader: ImageLoader, navController: NavController,
    width: Int,
) {
    composable(
        Screen.HeroList.route,
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -width },
                animationSpec = tween(durationMillis = 300,
                    easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -width },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)) + fadeIn(
                animationSpec = tween(300))
        },
    ) {
        val viewModel: HeroListViewModel = hiltViewModel()
        HeroList(state = viewModel.state.value,
            events = viewModel::onTriggerEvent,
            imageLoader = imageLoader,
            navigateToDetailScreen = { heroId ->
                navController.navigate("${Screen.HeroDetail.route}/$heroId")
            })
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addHeroDetail(
    imageLoader: ImageLoader,
    width: Int,
) {
    composable(
        Screen.HeroDetail.route + "/{heroId}", arguments = Screen.HeroDetail.arguments,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { width },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)) + fadeIn(
                animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { width },
                animationSpec = tween(durationMillis = 300,
                    easing = FastOutSlowInEasing)) + fadeOut(animationSpec = tween(300))
        },
    ) {
        val viewModel: HeroDetailViewModel = hiltViewModel()
        HeroDetail(viewModel.state.value, imageLoader = imageLoader,
        events = viewModel::onTriggerEvent)
    }
}
