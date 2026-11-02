package com.hyundai.pleos.safedrive.domain.concierge

import com.hyundai.pleos.safedrive.data.model.ParkingStatus
import com.hyundai.pleos.safedrive.data.model.RestArea

class RestAreaConcierge {

    private val candidateRestAreas = listOf(
        RestArea(
            id = "RA_ANSEONG",
            name = "안성휴게소 (부산방향)",
            distanceKm = 4.2f,
            etaMinutes = 4,
            signatureFood = "원조 소떡소떡, 안성국밥",
            hasCoffeeShop = true,
            coffeeBrand = "스타벅스 / 투썸플레이스",
            parkingStatus = ParkingStatus.SMOOTH,
            isRecommended = true
        ),
        RestArea(
            id = "RA_MANGHYANG",
            name = "망향휴게소 (부산방향)",
            distanceKm = 18.5f,
            etaMinutes = 15,
            signatureFood = "명품 닭개장, 호두과자",
            hasCoffeeShop = true,
            coffeeBrand = "할리스커피",
            parkingStatus = ParkingStatus.NORMAL
        ),
        RestArea(
            id = "RA_CHEONAN",
            name = "천안호두휴게소",
            distanceKm = 32.0f,
            etaMinutes = 24,
            signatureFood = "원조 천안 앙금 호두과자",
            hasCoffeeShop = true,
            coffeeBrand = "탐앤탐스",
            parkingStatus = ParkingStatus.CONGESTED
        )
    )

    fun getUpcomingRestAreas(): List<RestArea> = candidateRestAreas

    fun getRecommendedRestArea(): RestArea = candidateRestAreas.first { it.isRecommended }

    fun generateTtsBriefing(restArea: RestArea, driverName: String = "종원"): String {
        return "${driverName}님, 전방 ${restArea.distanceKm}km 앞 ${restArea.name}에 들러보세요. " +
                "주차가 ${restArea.parkingStatus.labelKo}하며, ${restArea.coffeeBrand}와 맛있는 ${restArea.signatureFood}가 준비되어 있습니다. " +
                "경유지로 등록하시겠습니까?"
    }

    fun addWaypointToNavi(restArea: RestArea): Boolean {
        return true
    }
}
