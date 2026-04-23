# Quiz Feature

## 화면 목적
선택된 탭(히라가나/가타카나/탁음)의 글자를 랜덤 순서로 4지선다 퀴즈로 출제한다.
정답 선택 후 0.9초 뒤 자동으로 다음 문제로 진행, 전체 완료 시 결과 카드 표시.

## UiState
| 필드 | 타입 | 설명 |
|---|---|---|
| `currentIndex` | `Int` | 현재 문제 번호 (0-based) |
| `total` | `Int` | 전체 문제 수 |
| `currentKana` | `KanaItem` | 현재 출제 글자 |
| `choices` | `List<String>` | 4개 선택지 (romaji) |
| `selectedAnswer` | `String?` | 선택된 답 (null = 미선택) |
| `isCorrect` | `Boolean?` | 정답 여부 (null = 미선택) |
| `isFinished` | `Boolean` | 퀴즈 완료 여부 |
| `correctCount` | `Int` | 맞은 문제 수 |

## UiEvent
| 이벤트 | 설명 |
|---|---|
| `LoadQuiz(tab)` | KanaTab 기준으로 문제 초기화 |
| `AnswerSelected(answer)` | 선택지 탭 → 정답 확인 후 0.9s 딜레이 |
| `NextQuestion` | 다음 문제 진행 (딜레이 후 내부 호출) |

## UiEffect
| 이펙트 | 설명 |
|---|---|
| `NavigateBack` | 뒤로 가기 |
| `ShowResult` | 퀴즈 완료 (현재는 인라인 결과 카드로 처리) |
