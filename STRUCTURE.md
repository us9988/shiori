# STRUCTURE.md — android-mvi Project Structure Guide

Refer to this file only when:
- Scaffolding the project for the first time
- Adding or modifying the project layer structure
- Creating a new feature folder

---

## Project Layer Structure

```
app/src/main/java/com/us9988/mvi/
├── data/
│   ├── local/
│   ├── remote/
│   ├── model/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
└── presentation/
    ├── component/
    └── feature/
        └── {feature}/
            ├── {FEATURE}.md
            ├── {Feature}Contract.kt
            ├── {Feature}ViewModel.kt
            └── {Feature}Screen.kt
```

---

## Feature Convention

- **Contract** — always `interface`, contains `UiEvent`, `UiState`, `UiEffect`
- **ViewModel** — extends `MviViewModel<Event, State, Effect>`, use `setState {}` and `sendEffect()`
- **Screen** — collect `state` via `collectAsStateWithLifecycle()`, collect `effect` via `LaunchedEffect`

---

## Base Classes

Located at: `presentation/base/`

- `MviEvent` — marker interface for UiEvent
- `MviState` — marker interface for UiState
- `MviEffect` — marker interface for UiEffect
- `MviViewModel<Event, State, Effect>` — base ViewModel
  - `state: StateFlow<State>` — via `MutableStateFlow`
  - `effect: Flow<Effect>` — via `Channel(Channel.BUFFERED)`
  - `setState {}` — update state immutably
  - `sendEffect()` — emit one-time effect
  - `onEvent()` — abstract, handle UiEvent

---

## Color Naming Convention

| Token | 용도 |
|---|---|
| `primary` | 핵심 액션, 주요 버튼 |
| `secondary` | 보조 액션 |
| `tertiary` | 강조, 포인트 |
| `error` | 에러 상태 |
| `background` | 앱 전체 배경 |
| `surface` | 카드, 시트, 다이얼로그 |
| `outline` | 테두리, 구분선 |
| `scrim` | 딤처리 오버레이 |

- Dark 모드는 같은 이름에 `Dark` 접미사
- 직접 Color 값 사용 금지 — 반드시 `MaterialTheme.colorScheme.*` 으로 접근
