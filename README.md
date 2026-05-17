
# 시오리 · shiori 🦆

**일본어 첫걸음 | Japanese Learning App**

히라가나·가타카나부터 N5 수준 단어, 일상회화 문장까지 — 체계적인 커리큘럼과 퀴즈, 북마크 단어장을 통해 꾸준한 일본어 학습을 돕습니다.

---

## Screenshots

| Home | Kana | Word Study |                     WordList                     | Conversation |
|:---:|:---:|:---:|:------------------------------------------------:|:------------:|
| ![Home](docs/images/screenshot_home.png) | ![Kana](docs/images/screenshot_kana.png) | ![Word](docs/images/screenshot_word.png) | ![List](docs/images/screenshot_conversation.png) | ![Conversation](docs/images/screenshot_conversation.png) |
 
---

## Features

- **히라가나 · 가타카나 · 탁음** — 한글 발음과 함께 전체 117자 학습
- **글자 퀴즈** — 원하는 글자만 선택하거나 전체 랜덤 퀴즈 도전
- **JLPT 단어 학습** — N5~N1 단어 2,500+개 단계별 카드 학습 + 회독 시스템
- **단어 퀴즈** — 뜻 맞추기 · 일본어 맞추기 4지선다 퀴즈
- **일상회화** — 100+개 회화 문장과 원어민 발음 듣기
- **내 단어장** — 북마크한 단어 플래시카드로 복습
- **오늘의 단어 · 회화** — 매일 새로운 단어와 회화 문장 제공
---

## Architecture

```
app/src/main/java/com/us9988/mvi/
├── data/
│   ├── local/
│   │   ├── entity/              # Room Entity
│   │   │   ├── WordEntity.kt
│   │   │   ├── PhraseEntity.kt
│   │   │   ├── LearnedKanaEntity.kt
│   │   │   ├── WordProgressEntity.kt
│   │   │   └── BookmarkEntity.kt
│   │   ├── dao/                 # Room DAO
│   │   ├── datasource/          # 로컬 데이터 (단어, 회화)
│   │   │   ├── WordN5DataSource.kt
│   │   │   ├── WordN4DataSource.kt
│   │   │   ├── WordLocalDataSource.kt
│   │   │   └── PhraseLocalDataSource.kt
│   │   └── ShioriDatabase.kt
│   └── repository/              # Repository 구현체
├── domain/
│   ├── model/                   # 도메인 모델
│   ├── repository/              # Repository 인터페이스
│   └── usecase/                 # 비즈니스 로직
└── presentation/
    ├── base/                    # MviViewModel, MviEvent, MviState, MviEffect
    ├── analytics/               # Firebase Analytics, Crashlytics
    ├── ad/                      # AdMob 관리
    ├── billing/                 # 인앱 결제 (프리미엄)
    └── feature/
        ├── home/                # 홈 화면
        ├── kana/                # 글자 학습 · 퀴즈
        ├── word/                # JLPT 단어 목록
        ├── wordstudy/           # 단어 카드 학습
        ├── wordquiz/            # 단어 퀴즈
        ├── conversation/        # 일상회화
        └── my/                  # 내 단어장 · 설정
```

## Key Technical Decisions

**MVI 아키텍처**
- MviViewModel · MviEvent · MviState · MviEffect 베이스 클래스 직접 설계
- StateFlow 기반 단방향 데이터 흐름으로 UI 상태 일관성 확보
- Channel 기반 UiEffect로 네비게이션 · 스낵바 등 일회성 이벤트 처리 분리
  **로컬 데이터 버전 관리**
- 앱 업데이트 시 기존 DB 유지하면서 새로 추가된 단어 · 회화만 자동 삽입
- DataStore에 버전 저장 후 앱 실행 시 비교하여 증분 업데이트 처리
  **단어 학습 시스템**
- 누적 방식 단계 설계 (1-50 → 1-100 → 1-150) 로 이전 단어 자연스럽게 복습
- 매 세션 랜덤 셔플로 순서 암기 방지
- 다시볼게요 단어는 세션 맨 뒤로 이동하여 알아요 될 때까지 반복 출제
- 중간 종료 시 알아요 완료 단어만 저장하여 다음 세션에서 이어서 진행
- 회독 카운트로 반복 학습 횟수 추적
  **오디오 재생**
- ExoPlayer로 assets 폴더 원어민 발음 재생
- 성별(여성 · 남성) 음성 확장을 고려한 폴더 구조 설계
- 재생 중 다른 문장 선택 시 기존 재생 자동 중지
  **광고 최적화**
- 앱 실행 시 배너 · 전면 광고 미리 로드하여 노출 손실 최소화
- 바텀탭 7회 클릭 카운트 기반 전면 광고 노출로 자연스러운 광고 경험
---

## Monetization

- **무료**: AdMob 배너 광고 (앱 종료 시) + 전면 광고 (퀴즈 완료 후, 탭 7회 클릭 시)
- **프리미엄 (₩2,900)**: 전면 광고 전체 제거 · 일회성 영구 구매 (Google Play Billing)
---

