package com.hyundai.pleos.safedrive.data.model

enum class FatigueLevel(val titleKo: String, val thresholdScore: Int) {
    NORMAL("정상 주행", 0),
    CAUTION("피로 주의 (환기/음악 리프레시 권장)", 50),
    DANGER("위험 휴식 필요 (휴게소 경유 권장)", 80)
}

data class FatigueAnalysisResult(
    val score: Int,
    val level: FatigueLevel,
    val steeringJitterStdDev: Float,
    val speedFluctuationRate: Float,
    val continuousDrivingMinutes: Int,
    val messageKo: String
)
