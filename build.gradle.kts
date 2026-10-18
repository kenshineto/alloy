import cat.freya.lodestone.excludeKotlin
import cat.freya.lodestone.publishing.License

plugins {
    alias(libs.plugins.lodestone)
}

group = "cat.freya.alloy"

version = "0.1-SNAPSHOT"

description = "Common utilities for KenshinEto's projects"

lodestone {
    java(8)
    kotlin()
    detekt()
    spotless()
    publish {
        group = "cat.freya"
        artifact = "alloy"
        author = "KenshinEto"
        license = License.GPL3
        gitRepo = "https://g.freya.cat/alloy"
        reposilite("https://maven.freya.cat")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // config
    api(libs.jackson.kotlin, excludeKotlin)
    api(libs.jackson.dataformat.yaml, excludeKotlin)

    // database
    implementation(libs.sqlite.jdbc)
    implementation(libs.mysql.connector.j)
    implementation(libs.postgresql)
    implementation(libs.hikari) {
        exclude(group = "org.slf4j")
    }
}
