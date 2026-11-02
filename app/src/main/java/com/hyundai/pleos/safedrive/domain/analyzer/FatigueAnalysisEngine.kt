package com.hyundai.pleos.safedrive.domain.analyzer

import com.hyundai.pleos.safedrive.data.model.FatigueAnalysisResult
import com.hyundai.pleos.safedrive.data.model.FatigueLevel
import com.hyundai.pleos.safedrive.data.model.VehicleSensorData
import java.util.LinkedList
import kotlin.math.pow
import kotlin.math.sqrt

class FatigueAnalysisEngine(
    private val windowSize: Int = 30
) {
    private val window = LinkedList<VehicleSensorData>()
    private var smoothedScore: Float = 15.0f

    @Synchronized
    fun processSample(data: VehicleSensorData): FatigueAnalysisResult {
        window.addLast(data)
        if (window.size > windowSize) {
            window.removeFirst()
        }

        val angles = window.map { it.steeringAngleDeg }
        val meanAngle = angles.average().toFloat()
        val variance = angles.map { (it - meanAngle).pow(2) }.average().toFloat()
        val stdDev = sqrt(variance)
        val steeringScore = (stdDev * 12.0f).coerceIn(0.0f, 100.0f)

        val speeds = window.map { it.speedKmh }
        val speedMean = speeds.average().toFloat()
        val speedStdDev = sqrt(speeds.map { (it - speedMean).pow(2) }.average().toFloat())
        val pedalScore = (speedStdDev * 15.0f).coerceIn(0.0f, 100.0f)

        val lkaScore = (data.laneDepartureWarningCount * 12.0f).coerceIn(0.0f, 100.0f)

        val timeScore = when {
            data.continuousDrivingMinutes < 60 -> 10.0f
            data.continuousDrivingMinutes < 120 -> 35.0f
            data.continuousDrivingMinutes < 150 -> 70.0f
            else -> 95.0f
        }

        val rawScore = (steeringScore * 0.40f) + (pedalScore * 0.20f) + (lkaScore * 0.25f) + (timeScore * 0.15f)
        smoothedScore = (smoothedScore * 0.85f) + (rawScore * 0.15f)
        val finalScore = smoothedScore.toInt().coerceIn(0, 100)

        val level = when {
            finalScore < FatigueLevel.CAUTION.thresholdScore -> FatigueLevel.NORMAL
            finalScore < FatigueLevel.DANGER.thresholdScore -> FatigueLevel.CAUTION
            else -> FatigueLevel.DANGER
        }

        val msg = when (level) {
            FatigueLevel.NORMAL -> "안정적인 주행 패턴을 유지하고 있습니다."
            FatigueLevel.CAUTION -> "미세 조향 떨림이 감지되었습니다. 실내 환기와 리프레시 음악을 추천합니다."
            FatigueLevel.DANGER -> "심한 피로 패턴이 감지되었습니다. 전방 휴게소에서 휴식을 권장합니다."
        }

        return FatigueAnalysisResult(
            score = finalScore,
            level = level,
            steeringJitterStdDev = stdDev,
            speedFluctuationRate = speedStdDev,
            continuousDrivingMinutes = data.continuousDrivingMinutes,
            messageKo = msg
        )
    }

    fun reset() {
        window.clear()
        smoothedScore = 15.0f
    }
}
