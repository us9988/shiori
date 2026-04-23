# Sentence Feature

## 화면 목적
문장 관련 학습을 3개 세그먼트 탭으로 제공한다.
상단 탭 선택 상태만 관리하며, 각 탭의 로직은 하위 Screen/Contract에서 독립적으로 담당한다.

## 구조
```
sentence/
├── SENTENCE.md
├── SentenceContract.kt     — 탭 상태만
├── SentenceViewModel.kt    — 탭 전환만
├── SentenceScreen.kt       — 탭 셀렉터 + 하위 Screen 라우팅
├── word/
│   └── WordScreen.kt       — 로컬 단어 콘텐츠
├── conversation/
│   └── ConversationScreen.kt — 로컬 110문장 + 오디오
└── analysis/
    └── AnalysisScreen.kt   — Claude API 문장분석
```

## SentenceTab
| 탭 | 설명 |
|---|---|
| `WORD` | 로컬 단어 학습 (플래시카드, 복습) |
| `CONVERSATION` | 일상회화 110문장 + 오디오 (Phase 2) |
| `ANALYSIS` | 일본어 문장 입력 → Claude API 분석 (Phase 2) |

## UiState
| 필드 | 타입 | 설명 |
|---|---|---|
| `selectedTab` | `SentenceTab` | 현재 선택된 탭 |

## UiEvent
| 이벤트 | 설명 |
|---|---|
| `TabChanged` | 탭 전환 |

## UiEffect
없음
