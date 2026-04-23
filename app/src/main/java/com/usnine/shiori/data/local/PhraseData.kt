package com.usnine.shiori.data.local

import com.usnine.shiori.data.local.entity.PhraseCategory
import com.usnine.shiori.data.local.entity.PhraseEntity

object PhraseData {

    val phrases = listOf(
        // GREETING
        PhraseEntity(
            japanese      = "おはようございます",
            hiragana      = "おはようございます",
            korean        = "안녕하세요 (아침 인사)",
            category      = PhraseCategory.GREETING,
            level         = "N5",
            audioFileName = "greeting_ohayou.mp3",
        ),
        PhraseEntity(
            japanese      = "ありがとうございます",
            hiragana      = "ありがとうございます",
            korean        = "감사합니다",
            category      = PhraseCategory.GREETING,
            level         = "N5",
            audioFileName = "greeting_arigatou.mp3",
        ),
        // CAFE
        PhraseEntity(
            japanese      = "コーヒーを一つください",
            hiragana      = "コーヒーをひとつください",
            korean        = "커피 한 잔 주세요",
            category      = PhraseCategory.CAFE,
            level         = "N5",
            audioFileName = "cafe_coffee.mp3",
        ),
        PhraseEntity(
            japanese      = "持ち帰りでお願いします",
            hiragana      = "もちかえりでおねがいします",
            korean        = "포장해 주세요",
            category      = PhraseCategory.CAFE,
            level         = "N4",
            audioFileName = "cafe_takeout.mp3",
        ),
        // SHOPPING
        PhraseEntity(
            japanese      = "これはいくらですか",
            hiragana      = "これはいくらですか",
            korean        = "이건 얼마예요?",
            category      = PhraseCategory.SHOPPING,
            level         = "N5",
            audioFileName = "shopping_price.mp3",
        ),
        PhraseEntity(
            japanese      = "試着してもいいですか",
            hiragana      = "しちゃくしてもいいですか",
            korean        = "입어봐도 될까요?",
            category      = PhraseCategory.SHOPPING,
            level         = "N4",
            audioFileName = "shopping_tryon.mp3",
        ),
        // TRANSPORT
        PhraseEntity(
            japanese      = "渋谷駅まで一枚ください",
            hiragana      = "しぶやえきまでいちまいください",
            korean        = "시부야역까지 한 장 주세요",
            category      = PhraseCategory.TRANSPORT,
            level         = "N4",
            audioFileName = "transport_ticket.mp3",
        ),
        PhraseEntity(
            japanese      = "このバスは新宿に止まりますか",
            hiragana      = "このバスはしんじゅくにとまりますか",
            korean        = "이 버스 신주쿠에 서나요?",
            category      = PhraseCategory.TRANSPORT,
            level         = "N4",
            audioFileName = "transport_bus.mp3",
        ),
        // ACCOMMODATION
        PhraseEntity(
            japanese      = "チェックインをお願いします",
            hiragana      = "チェックインをおねがいします",
            korean        = "체크인 부탁드려요",
            category      = PhraseCategory.ACCOMMODATION,
            level         = "N4",
            audioFileName = "accommodation_checkin.mp3",
        ),
        PhraseEntity(
            japanese      = "Wi-Fiのパスワードを教えてください",
            hiragana      = "ワイファイのパスワードをおしえてください",
            korean        = "와이파이 비밀번호 알려주세요",
            category      = PhraseCategory.ACCOMMODATION,
            level         = "N4",
            audioFileName = "accommodation_wifi.mp3",
        ),
    )
}
