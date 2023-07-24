package com.app.hero_datasource_test.network

sealed class HeroServiceResponseType{

    object EmptyList: HeroServiceResponseType()

    object MalformedData: HeroServiceResponseType()

    object ValidData: HeroServiceResponseType()

    object Http404: HeroServiceResponseType()
}