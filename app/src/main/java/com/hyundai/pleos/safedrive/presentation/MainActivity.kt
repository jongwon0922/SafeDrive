package com.hyundai.pleos.safedrive.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hyundai.pleos.safedrive.R
import com.hyundai.pleos.safedrive.data.model.FatigueLevel
import com.hyundai.pleos.safedrive.data.sensor.MockSensorDataSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: SafeDriveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSimulationButtons()
        observeViewModel()
    }

    private fun setupSimulationButtons() {
        findViewById<Button>(R.id.btn_sim_normal).setOnClickListener {
            viewModel.setSimulationScenario(MockSensorDataSource.SimulationScenario.NORMAL_CRUISING)
        }
        findViewById<Button>(R.id.btn_sim_caution).setOnClickListener {
            viewModel.setSimulationScenario(MockSensorDataSource.SimulationScenario.MILD_FATIGUE_JITTER)
        }
        findViewById<Button>(R.id.btn_sim_danger).setOnClickListener {
            viewModel.setSimulationScenario(MockSensorDataSource.SimulationScenario.DROWSY_MICRO_SLEEP)
        }
        findViewById<Button>(R.id.btn_add_waypoint).setOnClickListener {
            viewModel.confirmAddWaypoint()
        }
    }

    private fun observeViewModel() {
        val tvScore = findViewById<TextView>(R.id.tv_fatigue_score)
        val tvLevel = findViewById<TextView>(R.id.tv_fatigue_level)
        val tvSpeed = findViewById<TextView>(R.id.tv_speed)
        val tvSteering = findViewById<TextView>(R.id.tv_steering_angle)
        val tvDrivingTime = findViewById<TextView>(R.id.tv_driving_time)
        val progressScore = findViewById<ProgressBar>(R.id.progress_fatigue)
        val tvTtsPrompt = findViewById<TextView>(R.id.tv_tts_prompt)
        val layoutRestArea = findViewById<View>(R.id.layout_rest_area_card)
        val tvRestAreaName = findViewById<TextView>(R.id.tv_rest_area_name)
        val tvRestAreaDesc = findViewById<TextView>(R.id.tv_rest_area_desc)
        val btnAddWaypoint = findViewById<Button>(R.id.btn_add_waypoint)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                state.currentTelemetry?.let { t ->
                    tvSpeed.text = "${t.speedKmh.toInt()} km/h"
                    tvSteering.text = "조향각: %.1f°".format(t.steeringAngleDeg)
                    tvDrivingTime.text = "연속 주행: ${t.continuousDrivingMinutes}분"
                }

                state.analysisResult?.let { a ->
                    tvScore.text = "${a.score}"
                    tvLevel.text = a.level.titleKo
                    progressScore.progress = a.score

                    when (a.level) {
                        FatigueLevel.NORMAL -> {
                            tvScore.setTextColor(Color.parseColor("#4CAF50"))
                            layoutRestArea.visibility = View.GONE
                        }
                        FatigueLevel.CAUTION -> {
                            tvScore.setTextColor(Color.parseColor("#FF9800"))
                            layoutRestArea.visibility = View.GONE
                        }
                        FatigueLevel.DANGER -> {
                            tvScore.setTextColor(Color.parseColor("#F44336"))
                            layoutRestArea.visibility = View.VISIBLE
                        }
                    }
                }

                state.recommendedRestArea?.let { ra ->
                    tvRestAreaName.text = "⭐ ${ra.name} (전방 ${ra.distanceKm}km)"
                    tvRestAreaDesc.text = "주차: ${ra.parkingStatus.labelKo} | 인기메뉴: ${ra.signatureFood}\n카페: ${ra.coffeeBrand}"
                }

                state.ttsPromptMessage?.let { msg ->
                    tvTtsPrompt.text = "🎙️ TTS 음성 안내: " + msg
                }

                if (state.isWaypointAdded) {
                    btnAddWaypoint.text = "✅ 내비 경유지 등록 완료"
                    btnAddWaypoint.isEnabled = false
                } else {
                    btnAddWaypoint.text = "경유지로 등록 (원클릭)"
                    btnAddWaypoint.isEnabled = true
                }
            }
        }
    }
}
