package com.usnine.shiori.data.local

data class KanaItem(val kana: String, val koreanReading: String)
data class KanaRow(val label: String, val items: List<KanaItem?>)

object KanaData {

    val hiraganaRows: List<KanaRow> = listOf(
        KanaRow("あ행", listOf(KanaItem("あ","아"),  KanaItem("い","이"),  KanaItem("う","우"),  KanaItem("え","에"),  KanaItem("お","오"))),
        KanaRow("か행", listOf(KanaItem("か","카"),  KanaItem("き","키"),  KanaItem("く","쿠"),  KanaItem("け","케"),  KanaItem("こ","코"))),
        KanaRow("さ행", listOf(KanaItem("さ","사"),  KanaItem("し","시"),  KanaItem("す","스"),  KanaItem("せ","세"),  KanaItem("そ","소"))),
        KanaRow("た행", listOf(KanaItem("た","타"),  KanaItem("ち","치"),  KanaItem("つ","츠"),  KanaItem("て","테"),  KanaItem("と","토"))),
        KanaRow("な행", listOf(KanaItem("な","나"),  KanaItem("に","니"),  KanaItem("ぬ","누"),  KanaItem("ね","네"),  KanaItem("の","노"))),
        KanaRow("は행", listOf(KanaItem("は","하"),  KanaItem("ひ","히"),  KanaItem("ふ","후"),  KanaItem("へ","헤"),  KanaItem("ほ","호"))),
        KanaRow("ま행", listOf(KanaItem("ま","마"),  KanaItem("み","미"),  KanaItem("む","무"),  KanaItem("め","메"),  KanaItem("も","모"))),
        KanaRow("や행", listOf(KanaItem("や","야"),  null,                KanaItem("ゆ","유"),  null,                KanaItem("よ","요"))),
        KanaRow("ら행", listOf(KanaItem("ら","라"),  KanaItem("り","리"),  KanaItem("る","루"),  KanaItem("れ","레"),  KanaItem("ろ","로"))),
        KanaRow("わ행", listOf(KanaItem("わ","와"),  null,                null,                null,                KanaItem("を","오"))),
        KanaRow("ん",   listOf(KanaItem("ん","응"),  null,                null,                null,                null)),
    )

    val katakanaRows: List<KanaRow> = listOf(
        KanaRow("ア행", listOf(KanaItem("ア","아"),  KanaItem("イ","이"),  KanaItem("ウ","우"),  KanaItem("エ","에"),  KanaItem("オ","오"))),
        KanaRow("カ행", listOf(KanaItem("カ","카"),  KanaItem("キ","키"),  KanaItem("ク","쿠"),  KanaItem("ケ","케"),  KanaItem("コ","코"))),
        KanaRow("サ행", listOf(KanaItem("サ","사"),  KanaItem("シ","시"),  KanaItem("ス","스"),  KanaItem("セ","세"),  KanaItem("ソ","소"))),
        KanaRow("タ행", listOf(KanaItem("タ","타"),  KanaItem("チ","치"),  KanaItem("ツ","츠"),  KanaItem("テ","테"),  KanaItem("ト","토"))),
        KanaRow("ナ행", listOf(KanaItem("ナ","나"),  KanaItem("ニ","니"),  KanaItem("ヌ","누"),  KanaItem("ネ","네"),  KanaItem("ノ","노"))),
        KanaRow("ハ행", listOf(KanaItem("ハ","하"),  KanaItem("ヒ","히"),  KanaItem("フ","후"),  KanaItem("ヘ","헤"),  KanaItem("ホ","호"))),
        KanaRow("マ행", listOf(KanaItem("マ","마"),  KanaItem("ミ","미"),  KanaItem("ム","무"),  KanaItem("メ","메"),  KanaItem("モ","모"))),
        KanaRow("ヤ행", listOf(KanaItem("ヤ","야"),  null,                KanaItem("ユ","유"),  null,                KanaItem("ヨ","요"))),
        KanaRow("ラ행", listOf(KanaItem("ラ","라"),  KanaItem("リ","리"),  KanaItem("ル","루"),  KanaItem("レ","레"),  KanaItem("ロ","로"))),
        KanaRow("ワ행", listOf(KanaItem("ワ","와"),  null,                null,                null,                KanaItem("ヲ","오"))),
        KanaRow("ン",   listOf(KanaItem("ン","응"),  null,                null,                null,                null)),
    )

    val dakutenRows: List<KanaRow> = listOf(
        KanaRow("が행", listOf(KanaItem("が","가"),  KanaItem("ぎ","기"),  KanaItem("ぐ","구"),  KanaItem("げ","게"),  KanaItem("ご","고"))),
        KanaRow("ざ행", listOf(KanaItem("ざ","자"),  KanaItem("じ","지"),  KanaItem("ず","즈"),  KanaItem("ぜ","제"),  KanaItem("ぞ","조"))),
        KanaRow("だ행", listOf(KanaItem("だ","다"),  KanaItem("ぢ","지"),  KanaItem("づ","즈"),  KanaItem("で","데"),  KanaItem("ど","도"))),
        KanaRow("ば행", listOf(KanaItem("ば","바"),  KanaItem("び","비"),  KanaItem("ぶ","부"),  KanaItem("べ","베"),  KanaItem("ぼ","보"))),
        KanaRow("ぱ행", listOf(KanaItem("ぱ","파"),  KanaItem("ぴ","피"),  KanaItem("ぷ","푸"),  KanaItem("ぺ","페"),  KanaItem("ぽ","포"))),
    )
}
