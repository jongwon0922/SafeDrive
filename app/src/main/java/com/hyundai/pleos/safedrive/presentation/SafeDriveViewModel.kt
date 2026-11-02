package com.hyundai.pleos.safedrive.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyundai.pleos.safedrive.data.model.FatigueAnalysisResult
import com.hyundai.pleos.safedrive.data.model.FatigueLevel
import com.hyundai.pleos.safedrive.data.model.RestArea
import com.hyundai.pleos.safedrive.data.model.VehicleSensorData
import com.hyundai.pleos.safedrive.data.sensor.MockSensorDataSource
import com.hyundai.pleos.safedrive.domain.analyzer.FatigueAnalysisEngine
import com.hyundai.pleos.safedrive.domain.concierge.RestAreaConcierge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SafeDriveUiState(
    val currentTelemetry: VehicleSensorData? = null,
    val analysisResult: FatigueAnalysisResult? = null,
    val recommendedRestArea: RestArea? = null,
    val ttsPromptMessage: String? = null,
    val isWaypointAdded: Boolean = false,
    val airVentActive: Boolean = false,
    val refreshMusicPlaying: Boolean = false
)

class SafeDriveViewModel : ViewModel() {

    private val sensorDataSource = MockSensorDataSource()
    private val analysisEngine = FatigueAnalysisEngine()
    private val restAreaConcierge = RestAreaConcierge()

    private val _uiState = MutableStateFlow(SafeDriveUiState())
    val uiState: StateFlow<SafeDriveUiState> = _uiState.asStateFlow()

    init {
        startTelemetryStream()
    }

    private fun startTelemetryStream() {
        viewModelScope.launch {
            sensorDataSource.getVehicleTelemetryStream().collect { telemetry ->
                val analysis = analysisEngine.processSample(telemetry)
                val restArea = if (analysis.level == FatigueLevel.DANGER) {
                    restAreaConcierge.getRecommendedRestArea()
                } else null

                val ttsMsg = when (analysis.level) {
                    FatigueLevel.CAUTION -> "실내 환기를 가동하고 경쾌한 음악을 재생합니다."
                    FatigueLevel.DANGER -> restArea?.let { restAreaConcierge.generateTtsBriefing(it) }
                    else -> null
                }

                _uiState.value = _uiState.value.copy(
                    currentTelemetry = telemetry,
                    analysisResult = analysis,
                    recommendedRestArea = restArea,
                    ttsPromptMessage = ttsMsg,
                    airVentActive = analysis.level != FatigueLevel.NORMAL,
                    refreshMusicPlaying = analysis.level == FatigueLevel.CAUTION
                )
            }
        }
    }

    fun setSimulationScenario(scenario: MockSensorDataSource.SimulationScenario) {
        sensorDataSource.setScenario(scenario)
        if (scenario == MockSensorDataSource.SimulationScenario.NORMAL_CRUISING) {
            _uiState.value = _uiState.value.copy(isWaypointAdded = false)
        }
    }

    fun confirmAddWaypoint() {
        val restArea = _uiState.value.recommendedRestArea ?: return
        val success = restAreaConcierge.addWaypointToNavi(restArea)
        if (success) {
            _uiState.value = _uiState.value.copy(
                isWaypointAdded = true,
                ttsPromptMessage = "${restArea.name}를 내비게이션 경유지로 등록했습니다. 안전하게 쉬어가세요."
            )
        }
    }
}
