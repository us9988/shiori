package com.usnine.shiori.data.local.entity

enum class WordLevel {
    N5, N4, N3, N2, N1,
}

enum class PartOfSpeech {
    NOUN,         // 명사
    VERB,         // 동사
    I_ADJECTIVE,  // い형용사
    NA_ADJECTIVE, // な형용사
    ADVERB,       // 부사
    CONJUNCTION,  // 접속사
    PARTICLE,     // 조사
    OTHER,        // 기타
}
