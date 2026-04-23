# Kana Feature

## 화면 목적
히라가나 · 가타카나 · 탁음 46자를 행별 5열 그리드로 표시하고, 셀 탭으로 학습 완료를 토글한다.

## UiState
| 필드 | 타입 | 설명 |
|---|---|---|
| `selectedTab` | `KanaTab` | 현재 선택된 탭 (HIRAGANA / KATAKANA / DAKUTEN) |
| `rows` | `List<KanaRow>` | 현재 탭의 행별 데이터 |
| `learnedSet` | `Set<String>` | 학습 완료된 글자 집합 (kana 문자열 기준) |

## UiEvent
| 이벤트 | 설명 |
|---|---|
| `TabChanged(tab)` | 상단 탭 변경 |
| `KanaTapped(kana)` | 셀 탭 → learnedSet 토글 |

## UiEffect
현재 없음. 추후 퀴즈 화면 이동 Effect 추가 예정.
