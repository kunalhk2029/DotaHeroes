apply {
    from("$rootDir/library-build.gradle")
}


plugins {
    kotlin(KotlinPlugins.serialization) version "1.5.10"
}

dependencies {
    "implementation"(project(Modules.heroDataSource))
    "implementation"(project(Modules.heroDomain))

    "implementation"(Ktor.ktorClientMock)
    "implementation"(Ktor.clientSerialization)

}