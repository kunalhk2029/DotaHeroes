package com.app.dotaheroesinfo.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route:String,val arguments:List<NamedNavArgument>){

    object HeroList:Screen(
        route="heroList", listOf()
    )

    object HeroDetail:Screen(
        route = "heroDetail",
        listOf(navArgument(
            name = "heroId"
        ){
            type= NavType.IntType
        })
    )
}
