# shiori · 일본어 첫걸음

## 패키지
com.us9988.mvi

## 아키텍처
- presentation/feature/{feature}/ 아래에
  {FEATURE}.md, Contract, ViewModel, Screen 4개 세트
- base 클래스: MviViewModel, MviEvent, MviState, MviEffect
- 새 feature 추가 시 STURCTURE.md 파일 참조, 그 외 참조 금지

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

## 네비게이션
Bottom Navigation 4탭: 홈 / 글자 / 문장 / 단어장

## 규칙
- 커밋 전 반드시 승인 요청
- 문자열은 strings.xml 관리
- 미완성 기능 TODO 주석 명시