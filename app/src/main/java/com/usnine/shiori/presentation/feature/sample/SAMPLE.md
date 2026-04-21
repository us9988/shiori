# Sample Feature

Demonstrates the MVI pattern with base classes.

## Contract
- `UiEvent`: `OnButtonClick`, `OnTextChange`
- `UiState`: `text`, `count`, `isLoading`
- `UiEffect`: `ShowToast`

## Flow
1. User types → `OnTextChange` → `setState { copy(text = ...) }`
2. User clicks button → `OnButtonClick` → increment count + `ShowToast` effect
