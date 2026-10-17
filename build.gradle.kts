import cat.freya.lodestone.License
import cat.freya.lodestone.excludeKotlin

plugins {
    alias(libs.plugins.lodestone)
}

group = "cat.freya.alloy"

version = "0.1-SNAPSHOT"

description = "Common utilities for KenshinEto's projects"

lodestone {
    jvm(8)
    detekt()
    spotless()
    publish {
        group = "cat.freya"
        artifact = "alloy"
        author = "KenshinEto"
        url = "https://g.freya.cat/alloy"
        license = License.GPL3
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
