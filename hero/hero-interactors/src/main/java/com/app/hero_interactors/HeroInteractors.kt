package com.app.hero_interactors

import com.app.hero_datasource.cache.HeroCache
import com.app.hero_datasource.network.HeroService
import com.squareup.sqldelight.db.SqlDriver

data class HeroInteractors(
    val getHeros: GetHeros,
    val getHeroFromCache: GetHeroFromCache,
    val filterHeros: FilterHeros
) {
    companion object Factory {
        fun build(sqlDriver: SqlDriver): HeroInteractors{
            val service = HeroService.build()
            val cache = HeroCache.build(sqlDriver)
            return HeroInteractors(
                getHeros = GetHeros(
                    cache=cache,
                    service = service,
                )
            , getHeroFromCache = GetHeroFromCache(cache),
            filterHeros = FilterHeros())
        }
        val schema: SqlDriver.Schema = HeroCache.schema

        val dbName: String = HeroCache.dbName
    }
}