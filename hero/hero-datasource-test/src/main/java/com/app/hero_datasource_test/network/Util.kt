package com.app.hero_datasource_test.network

import com.app.hero_datasource.network.model.HeroDto
import com.app.hero_datasource.network.model.toHero
import com.app.hero_domain.Hero
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

@OptIn(ExperimentalSerializationApi::class)
fun serializeHeroData(jsonData: String): List<Hero>{
   return json.decodeFromString<List<HeroDto>>(jsonData).map { it.toHero() }
}