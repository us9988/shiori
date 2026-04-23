# shiori · 일본어 첫걸음

## 패키지
com.usnine.shiori

## 아키텍처
- presentation/feature/{feature}/ 아래에
  {FEATURE}.md, Contract, ViewModel, Screen 4개 세트
- base 클래스: MviViewModel, MviEvent, MviState, MviEffect
- 새 feature 추가 시 STRUCTURE.md 파일 참조, 그 외 참조 금지

## 디자인 토큰 (Color.kt)
- primary: #7B5C3E (accent, 메인 버튼)
- secondary: #A67C52
- tertiary: #4E7258 (green)
- background: #F5F1EA
- surface: #EFEBE3
- outline: rgba(90,65,40,0.13)
- error: #8C4040

색상은 반드시 MaterialTheme.colorScheme.* 으로만 접근.
Color 값 직접 사용 금지.

## 마스코트
- 오리 캐릭터 (이름: 시오리)
- 이미지 위치: assets/images/duck/
- 파일 목록:
  - duck_default.png   기본 포즈
  - duck_correct.png   정답 리액션
  - duck_wrong.png     오답 리액션
  - duck_complete.png  학습 완료
  - duck_empty.png     빈 상태 (단어 없음 등)

## 네비게이션
Bottom Navigation 4탭: 홈 / 글자 / 문장 / 마이

### 문장 탭 내부 세그먼트 (3개)
| 세그먼트 | 설명 |
|---|---|
| 단어 | 단어 플래시카드 복습 (기존 vocab feature) |
| 일상회화 | 일상 회화 학습 |
| 문장분석 | 일본어 문장 입력 → Claude API 분석 |

### 마이 탭 구성
| 섹션 | 설명 |
|---|---|
| 내 단어장 | 북마크한 단어 목록 |
| 학습통계 | 연속학습일, 완료 수 등 |
| 설정 | 앱 설정 |

## 북마크 흐름
문장분석 탭 > 단어 섹션 > 🔖 버튼 → 마이 탭 > 내 단어장에 저장

## 규칙
- 커밋 전 반드시 승인 요청
- 문자열은 strings.xml 관리
- 미완성 기능 TODO 주석 명시
- `Modifier.padding()`에서 `horizontal`/`vertical`과 `top`/`bottom`/`start`/`end`를 혼용 금지.
  반드시 `start`, `end`, `top`, `bottom` 네 파라미터만 사용.

## 디자인 레퍼런스
- UI 레퍼런스: ./reference/shiori.html
- 각 화면 구현 시 반드시 참조할 것
