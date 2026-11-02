package com.hyundai.pleos.safedrive.data.model

enum class ParkingStatus(val labelKo: String) {
    SMOOTH("여유"),
    NORMAL("보통"),
    CONGESTED("혼잡")
}

data class RestArea(
    val id: String,
    val name: String,
    val distanceKm: Float,
    val etaMinutes: Int,
    val signatureFood: String,
    val hasCoffeeShop: Boolean,
    val coffeeBrand: String,
    val parkingStatus: ParkingStatus,
    val isRecommended: Boolean = false
)
