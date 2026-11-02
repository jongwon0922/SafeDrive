package com.hyundai.pleos.safedrive.data.sensor

import com.hyundai.pleos.safedrive.data.model.VehicleSensorData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MockSensorDataSource {

    enum class SimulationScenario {
        NORMAL_CRUISING,
        MILD_FATIGUE_JITTER,
        DROWSY_MICRO_SLEEP
    }

    private var currentScenario = SimulationScenario.NORMAL_CRUISING
    private var simulatedMinutes = 45
    private var simulatedLkaCount = 0

    fun setScenario(scenario: SimulationScenario) {
        this.currentScenario = scenario
        if (scenario == SimulationScenario.DROWSY_MICRO_SLEEP) {
            simulatedMinutes = 140
            simulatedLkaCount = 7
        } else if (scenario == SimulationScenario.MILD_FATIGUE_JITTER) {
            simulatedMinutes = 105
            simulatedLkaCount = 3
        } else {
            simulatedMinutes = 30
            simulatedLkaCount = 0
        }
    }

    fun getVehicleTelemetryStream(): Flow<VehicleSensorData> = flow {
        var baseAngle = 0.0f
        while (true) {
            val (angleDelta, speed, lkaActive) = when (currentScenario) {
                SimulationScenario.NORMAL_CRUISING -> {
                    Triple(Random.nextFloat() * 1.5f - 0.75f, 98.0f + Random.nextFloat() * 2.0f, false)
                }
                SimulationScenario.MILD_FATIGUE_JITTER -> {
                    val jitter = if (Random.nextFloat() < 0.25f) Random.nextFloat() * 7.0f - 3.5f else Random.nextFloat() * 2.0f - 1.0f
                    Triple(jitter, 95.0f + Random.nextFloat() * 8.0f - 4.0f, Random.nextFloat() < 0.15f)
                }
                SimulationScenario.DROWSY_MICRO_SLEEP -> {
                    val isAbrupt = Random.nextFloat() < 0.35f
                    val jitter = if (isAbrupt) Random.nextFloat() * 16.0f - 8.0f else 0.1f
                    Triple(jitter, 90.0f + Random.nextFloat() * 14.0f - 7.0f, isAbrupt)
                }
            }

            baseAngle = (baseAngle + angleDelta).coerceIn(-45.0f, 45.0f)
            if (lkaActive) simulatedLkaCount++

            emit(
                VehicleSensorData(
                    speedKmh = speed,
                    steeringAngleDeg = baseAngle,
                    acceleratorPedalPct = (speed / 120.0f * 100.0f).coerceIn(20f, 80f),
                    brakePedalActive = speed < 92.0f,
                    laneKeepAssistActive = lkaActive,
                    laneDepartureWarningCount = simulatedLkaCount,
                    continuousDrivingMinutes = simulatedMinutes
                )
            )
            delay(500)
        }
    }
}
