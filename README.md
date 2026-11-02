# SafeDrive (세이프 드라이브) 🛡️
> 현대자동차그룹 차량용 앱 개발 공모전 (Pleos App Market) 출품작
> **차량 센서 기반 스마트 피로도 분석 및 휴식 컨시어지**

![SafeDrive in-car Pleos display mockup](assets/safedrive-in-car-mockup.png)
*▲ 현대자동차 더 뉴 아이오닉 5 파노라믹 커브드 디스플레이 (ccNC / Pleos OS) 실제 탑재 구동 목업*

---

## 📌 1. 프로젝트 개요 (Overview)
- **앱명**: SafeDrive (세이프 드라이브)
- **참가자**: 임종원 (대학생 부문 · 개인)
- **개발 환경**: Android API Level 28+ (Pie / Automotive OS 호환), Kotlin 1.9+, Gradle 8.0+
- **핵심 목표**: 
  1. 기존 내비게이션의 획일적인 '단순 시간 경고'를 탈피하여, 차량 CAN 센서 데이터(Vehicle SDK) 기반의 **실시간 운전자 피로도 정밀 감지**
  2. 단순 잔소리성 경고가 아닌 **가고 싶은 휴게소(맛집·주차 여유)를 능동적으로 제안(Nudge)**하여 운전자의 자발적 휴식 유도
  3. 100% 음성(TTS/STT) 인터페이스와 직관적인 Automotive Dark Mode 대화면 UI로 **운전자 시선 분산 방지(Driver Distraction Guidelines) 완벽 준수**

---

## 🏗️ 2. 시스템 아키텍처 및 핵심 모듈
SafeDrive는 **Clean Architecture**와 **MVVM 패턴**을 기반으로 설계되었습니다.

```
com.hyundai.pleos.safedrive/
├── data/
│   ├── model/           # 차량 텔레메트리, 피로도 분석 결과, 휴게소 데이터 모델
│   └── sensor/          # Pleos Vehicle SDK 연동 및 실시간 시뮬레이션 데이터 소스
├── domain/
│   ├── analyzer/        # 4대 센서 융합 피로도 판별 알고리즘 엔진 (Sliding Window & EMA)
│   └── concierge/       # Pleos NaviHelper 연동 경로상 휴게소 추천 및 TTS 브리핑 생성
└── presentation/
    ├── MainActivity     # Automotive 대화면 규격 고대비 Dark Mode 대시보드
    └── SafeDriveViewModel # 센서 스트림 수집 및 실시간 상태 관리
```

---

## 💡 3. Pleos Connect SDK 활용 상세

### 1) Vehicle SDK (주행 동역학 센서)
- **Steering Wheel Angle**: 조향 고착 및 미세 급보정(Jitter) 표준편차 실시간 추적
- **Speed & Pedals**: 액셀/브레이크 급조작 및 속도 불규칙 변동률 산출
- **Lane Keep Assist (LKA)**: 차선 이탈 방지 보조 개입 빈도 누적 집계

### 2) NaviHelper SDK (경로 및 경유지)
- 현재 고속도로 주행 경로 상의 전방 3~10km 내 최적 휴게소 POI 자동 탐색
- 운전자 원클릭/음성 승인 시 `addWaypoint()`로 경유지 즉시 자동 등록

### 3) TTS / SpeechToText SDK
- 화면 주시를 배제한 Eyes-Free 음성 브리핑:
  - *"종원님, 전방 4km 앞 안성휴게소에 들러보세요. 주차가 여유로우며 스타벅스와 원조 소떡소떡이 준비되어 있습니다. 경유지로 등록할까요?"*

---

## 🔌 4. 차량 순정 시스템 호환성 및 상호운용성 (Interoperability & Compatibility)
SafeDrive는 서드파티 앱의 한계를 넘어 현대차 순정 OS 및 차량 제어 시스템과 완벽히 동기화됩니다:

1. **순정 내비게이션 엔진 무결절 호환 (NaviHelper)**
   - **목적지 보존**: 기존 운전자가 설정한 최종 목적지를 임의 취소하거나 화면을 가리지 않고, `setWaypoint()` API를 통해 백그라운드에서 최적 휴게소를 1초 내 경유지로 자동 삽입 및 우회 경로 실시간 계산.
   - **비간섭형 UI**: 주행 지도 시야를 방해하지 않는 분할 화면(Split Screen) 및 헤드업 디스플레이(HUD) 요약 브리핑 호환.
2. **차내 멀티미디어 & 오디오 호환 (Android MediaSession & CarAudioManager)**
   - **오디오 더킹 (Audio Ducking)**: 긴급 경고 및 컨시어지 음성 브리핑 발생 시 재생 중인 음악 볼륨을 일시 감쇄 후 부드럽게 원복.
   - **미디어 세션 자동 제어**: 피로 주의(2단계) 시 표준 Android `MediaSession`을 통해 차내 활성 음악 앱(멜론, 지니, 스포티파이, 유튜브 뮤직 등)의 템포 빠른 리프레시 플레이리스트 자동 재생 연동.
3. **차량 바디 액추에이터 및 하드웨어 연동 호환 (Vehicle Hardware Integration)**
   - **공조(HVAC) 시스템**: 졸음 유발 원인인 실내 CO2 농도 저하를 위해 공조기를 내기 순환에서 '외기 순환 모드'로 자동 전환.
   - **앰비언트 무드램프 & 시트 햅틱**: 순정 무드램프 색상을 피로도 레벨(정상: 퍼플/블루 → 주의: 앰버 → 위험: 레드 점멸)과 실시간 연동하고, 운전석 시트 햅틱 진동 피드백 제공.
4. **전 차종 OS 호환성 및 안전 규격 (Automotive Safety Compliance)**
   - **ccNC 및 Pleos 전 라인업 대응**: Android API 28+ 기반으로 더 뉴 아이오닉 5, 디 올 뉴 그랜저(GN7), 싼타페(MX5), 기아 EV9 등 현대차그룹 전 차종 호환.
   - **운전자 시선 분산 방지 규격 (Driver Distraction Guidelines)**: 76dp 이상의 대형 터치 타깃, 고대비 Automotive Dark Theme, 100% 음성 핸즈프리 인터페이스 완벽 준수.

---

## 🎮 5. 시연 및 에뮬레이터 테스트 가이드
앱 화면 하단의 **시연 시뮬레이터 버튼**을 통해 심사위원이 즉시 3가지 주행 시나리오를 검증할 수 있습니다:
1. **[1. 정상 주행]**: 속도 98km/h, 부드러운 조향 → 피로 지수 15점 (초록색 정상 상태)
2. **[2. 피로 주의]**: 장거리 주행 + 미세 조향 떨림 발생 → 피로 지수 65점 돌파 → 실내 공조 환기 가동 및 경쾌한 음악 재생 안내
3. **[3. 위험 졸음]**: 차선 이탈 보조 개입 급증 → 피로 지수 85점 (빨간색 위험 상태) → 우측에 **안성휴게소 추천 카드** 즉시 표출 및 TTS 음성 안내 → `[경유지로 등록]` 터치 시 내비게이션 경유지 자동 설정 완료

---

## 📄 5. 공모전 제출 서류
- **기획서(접수양식 DOCX)**: `SafeDrive_현대차공모전_기획서_임종원.docx`
- **소스 코드 저장소**: 본 GitHub 저장소 (가산점 제출용)
