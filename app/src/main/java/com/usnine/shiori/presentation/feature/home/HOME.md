# Home Feature

## 화면 목적
앱 진입점. 연속 학습 스트릭, 오늘의 미션, 내 책갈피 목록을 한눈에 보여준다.

## UiState
| 필드 | 타입 | 설명 |
|---|---|---|
| `streak` | `Int` | 연속 학습일 수 |
| `todayMission` | `String` | 오늘 미션 제목 (예: "히라가나 さ행 퀴즈") |
| `bookmarks` | `List<BookmarkUi>` | 저장된 책갈피 문장 목록 |

## UiEvent
| 이벤트 | 설명 |
|---|---|
| `LoadData` | 화면 진입 시 더미 데이터 로드 |

## UiEffect
현재 없음. 추후 북마크 삭제 Snackbar 등 추가 예정.
