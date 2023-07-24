apply {
    from("$rootDir/library-build.gradle")
}

plugins {
    kotlin(KotlinPlugins.serialization) version "1.5.10"
    id(SqlDelight.plugin)
}

dependencies {
    "implementation"(project(Modules.heroDomain))

    "implementation"(Ktor.core)
    "implementation"(Ktor.clientSerialization)
    "implementation"(Ktor.android)
    "implementation"(SqlDelight.runtime)
}

sqldelight{
    database("HeroDatabase"){
        packageName="com.app.hero_datasource.cache"
        sourceFolders= listOf("sqldelight")
    }
}