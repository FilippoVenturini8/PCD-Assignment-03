plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation(platform("com.typesafe.akka:akka-bom_2.13:2.8.2"))
    implementation("com.typesafe.akka:akka-actor-typed_2.13")

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.rabbitmq:amqp-client:5.17.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}