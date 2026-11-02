package com.hyundai.pleos.safedrive.data.model

data class VehicleSensorData(
    val timestamp: Long = System.currentTimeMillis(),
    val speedKmh: Float,
    val steeringAngleDeg: Float,          // -540.0° ~ +540.0°
    val acceleratorPedalPct: Float,      // 0.0% ~ 100.0%
    val brakePedalActive: Boolean,
    val laneKeepAssistActive: Boolean,
    val laneDepartureWarningCount: Int,
    val continuousDrivingMinutes: Int
)
