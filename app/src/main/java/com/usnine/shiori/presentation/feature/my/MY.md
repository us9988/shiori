# My Feature

## 화면 목적
사용자의 저장된 단어(북마크)를 플래시카드로 복습하고 목록으로 관리한다.

## 오리 캐릭터 (마스코트)
| 상태 | 파일 |
|---|---|
| 북마크 없음 | `assets/images/duck/duck_empty.png` |
| 기본 (북마크 있음) | `assets/images/duck/duck_default.png` |

## 화면 구성
1. 오리 캐릭터 이미지 (상단)
2. 내 단어장 섹션 (북마크 있을 때)
   - 플래시카드: 탭 → 뜻 표시 (3D 플립)
   - 알아요 / 다시볼게요 버튼 (카드 뒷면 표시 후 활성화)
3. 북마크 목록 LazyColumn (삭제 가능)
4. 학습통계 섹션 (하단)

## UiState
| 필드 | 타입 | 설명 |
|---|---|---|
| `bookmarks` | `List<WordUi>` | 저장된 단어 목록 |
| `totalCount` | `Int` | 전체 단어 수 |
| `streakDays` | `Int` | 연속 학습일 |
| `completedCount` | `Int` | 이번 세션 완료 수 |
| `currentCard` | `WordUi?` | 현재 플래시카드 (null = 완료) |
| `isCardFlipped` | `Boolean` | 카드 앞/뒷면 |

## UiEvent
| 이벤트 | 설명 |
|---|---|
| `BookmarkRemoved(wordId)` | 북마크 삭제 → Room DELETE |
| `CardFlipped` | 카드 탭 → 뒤집기 |
| `KnowTapped` | 알아요 → 다음 카드 |
| `AgainTapped` | 다시볼게요 → 다음 카드 |

## UiEffect
없음 (현재 Phase 1)
