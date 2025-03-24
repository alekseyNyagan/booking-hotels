rootProject.name = "booking.hotels"
include("booking-hotels-service")
include("config-server")
include("eureka-server")
include("gateway-server")
include("user-service")
include("statistic-service")
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
    }
}