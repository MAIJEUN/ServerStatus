plugins {
    kotlin("jvm") version "1.8.20" // 코틀린 버전 설정
    id("java")
}

group = "com.maijsoft.JoinTitle"
version = "1.3-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper API 추가
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    // OSHI 라이브러리
    implementation("com.github.oshi:oshi-core:6.4.4")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Main-Class" to "com.maijsoft.serverstatus.Main" // 메인 클래스 지정
        )
    }
}