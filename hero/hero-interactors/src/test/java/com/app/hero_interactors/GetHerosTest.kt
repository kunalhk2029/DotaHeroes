package com.app.hero_interactors

import com.app.core.DataState
import com.app.core.ProgressBarState
import com.app.core.UIComponent
import com.app.hero_datasource_test.cache.FakeHeroCache
import com.app.hero_datasource_test.cache.FakeHeroDatabase
import com.app.hero_datasource_test.network.FakeHeroService
import com.app.hero_datasource_test.network.HeroServiceResponseType
import com.app.hero_datasource_test.network.data.HeroDataValid
import com.app.hero_datasource_test.network.data.HeroDataValid.NUM_HEROS
import com.app.hero_datasource_test.network.serializeHeroData
import com.app.hero_domain.Hero
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetHerosTest {

    // system in test
    private lateinit var getHeros: GetHeros

    @Test
    fun getHeros_success() =  runBlocking {
        // setup
        val heroDatabase = FakeHeroDatabase()
        val heroCache = FakeHeroCache(heroDatabase)
        val heroService = FakeHeroService.build(
            type = HeroServiceResponseType.ValidData // good data
        )

        getHeros = GetHeros(
            cache = heroCache,
            service = heroService
        )

        // Confirm the cache is empty before use-case has been executed
        var cachedHeros = heroCache.selectAll()
        assert(cachedHeros.isEmpty())

        // Execute the use-case
        val emissions = getHeros.execute().toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Hero>>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)
        assert(((emissions[1] as DataState.Data).data?.size?: 0) == NUM_HEROS)

        // Confirm the cache is no longer empty
        cachedHeros = heroCache.selectAll()
        assert(cachedHeros.size == NUM_HEROS)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<List<Hero>>(ProgressBarState.Idle))
    }

    /**
     * 1. Insert some data into the cache by executing a successful use-case.
     * 2. Configure the network operation to return malformed data.
     * 3. Execute use-case for a second time and confirm it still emits the cached data.
     */
    @Test
    fun getHeros_malformedData_successFromCache() =  runBlocking {
        // setup
        val heroDatabase = FakeHeroDatabase()
        val heroCache = FakeHeroCache(heroDatabase)
        val heroService = FakeHeroService.build(
            type = HeroServiceResponseType.MalformedData // Malformed data
        )

        getHeros = GetHeros(
            cache = heroCache,
            service = heroService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedHeros = heroCache.selectAll()
        assert(cachedHeros.isEmpty())

        // Add some data to the cache by executing a successful request
        val heroData = serializeHeroData(HeroDataValid.data)
        heroCache.insert(heroData)

        // Confirm the cache is not empty anymore
        cachedHeros = heroCache.selectAll()
        assert(cachedHeros.size == 121)

        // Execute the use-case
        val emissions = getHeros.execute().toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Hero>>(ProgressBarState.Loading))

        // Confirm second emission is error response
        assert(emissions[1] is DataState.Response)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == "Network Data Error")
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description.contains("Unexpected JSON token at offset"))

        // Confirm third emission is data from the cache
        assert(emissions[2] is DataState.Data)
        assert((emissions[2] as DataState.Data).data?.size == 121)

        // Confirm the cache is still not empty
        cachedHeros = heroCache.selectAll()
        assert(cachedHeros.size == 121)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<List<Hero>>(ProgressBarState.Idle))
    }

    @Test
    fun getHeros_emptyList() =  runBlocking {
        // setup
        val heroDatabase = FakeHeroDatabase()
        val heroCache = FakeHeroCache(heroDatabase)
        val heroService = FakeHeroService.build(
            type = HeroServiceResponseType.EmptyList // Empty data
        )

        getHeros = GetHeros(
            cache = heroCache,
            service = heroService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedHeros = heroCache.selectAll()
        assert(cachedHeros.isEmpty())

        // Execute the use-case
        val emissions = getHeros.execute().toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Hero>>(ProgressBarState.Loading))

        // Confirm second emission is data (empty list)
        assert(emissions[1] is DataState.Data)
        assert(((emissions[1] as DataState.Data).data?.size ?: 0) == 0)

        // Confirm the cache is STILL EMPTY
        cachedHeros = heroCache.selectAll()
        assert(cachedHeros.size == 0)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<List<Hero>>(ProgressBarState.Idle))
    }

}








