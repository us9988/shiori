package com.usnine.shiori.data.local

data class KanaItem(val kana: String, val romaji: String)
data class KanaRow(val label: String, val items: List<KanaItem?>)

object KanaData {

    val hiraganaRows: List<KanaRow> = listOf(
        KanaRow("あ행", listOf(KanaItem("あ","a"),   KanaItem("い","i"),   KanaItem("う","u"),   KanaItem("え","e"),   KanaItem("お","o"))),
        KanaRow("か행", listOf(KanaItem("か","ka"),  KanaItem("き","ki"),  KanaItem("く","ku"),  KanaItem("け","ke"),  KanaItem("こ","ko"))),
        KanaRow("さ행", listOf(KanaItem("さ","sa"),  KanaItem("し","shi"), KanaItem("す","su"),  KanaItem("せ","se"),  KanaItem("そ","so"))),
        KanaRow("た행", listOf(KanaItem("た","ta"),  KanaItem("ち","chi"), KanaItem("つ","tsu"), KanaItem("て","te"),  KanaItem("と","to"))),
        KanaRow("な행", listOf(KanaItem("な","na"),  KanaItem("に","ni"),  KanaItem("ぬ","nu"),  KanaItem("ね","ne"),  KanaItem("の","no"))),
        KanaRow("は행", listOf(KanaItem("は","ha"),  KanaItem("ひ","hi"),  KanaItem("ふ","fu"),  KanaItem("へ","he"),  KanaItem("ほ","ho"))),
        KanaRow("ま행", listOf(KanaItem("ま","ma"),  KanaItem("み","mi"),  KanaItem("む","mu"),  KanaItem("め","me"),  KanaItem("も","mo"))),
        KanaRow("や행", listOf(KanaItem("や","ya"),  null,                 KanaItem("ゆ","yu"),  null,                 KanaItem("よ","yo"))),
        KanaRow("ら행", listOf(KanaItem("ら","ra"),  KanaItem("り","ri"),  KanaItem("る","ru"),  KanaItem("れ","re"),  KanaItem("ろ","ro"))),
        KanaRow("わ행", listOf(KanaItem("わ","wa"),  null,                 null,                 null,                 KanaItem("を","wo"))),
        KanaRow("ん",   listOf(KanaItem("ん","n"),   null,                 null,                 null,                 null)),
    )

    val katakanaRows: List<KanaRow> = listOf(
        KanaRow("ア행", listOf(KanaItem("ア","a"),   KanaItem("イ","i"),   KanaItem("ウ","u"),   KanaItem("エ","e"),   KanaItem("オ","o"))),
        KanaRow("カ행", listOf(KanaItem("カ","ka"),  KanaItem("キ","ki"),  KanaItem("ク","ku"),  KanaItem("ケ","ke"),  KanaItem("コ","ko"))),
        KanaRow("サ행", listOf(KanaItem("サ","sa"),  KanaItem("シ","shi"), KanaItem("ス","su"),  KanaItem("セ","se"),  KanaItem("ソ","so"))),
        KanaRow("タ행", listOf(KanaItem("タ","ta"),  KanaItem("チ","chi"), KanaItem("ツ","tsu"), KanaItem("テ","te"),  KanaItem("ト","to"))),
        KanaRow("ナ행", listOf(KanaItem("ナ","na"),  KanaItem("ニ","ni"),  KanaItem("ヌ","nu"),  KanaItem("ネ","ne"),  KanaItem("ノ","no"))),
        KanaRow("ハ행", listOf(KanaItem("ハ","ha"),  KanaItem("ヒ","hi"),  KanaItem("フ","fu"),  KanaItem("ヘ","he"),  KanaItem("ホ","ho"))),
        KanaRow("マ행", listOf(KanaItem("マ","ma"),  KanaItem("ミ","mi"),  KanaItem("ム","mu"),  KanaItem("メ","me"),  KanaItem("モ","mo"))),
        KanaRow("ヤ행", listOf(KanaItem("ヤ","ya"),  null,                 KanaItem("ユ","yu"),  null,                 KanaItem("ヨ","yo"))),
        KanaRow("ラ행", listOf(KanaItem("ラ","ra"),  KanaItem("リ","ri"),  KanaItem("ル","ru"),  KanaItem("レ","re"),  KanaItem("ロ","ro"))),
        KanaRow("ワ행", listOf(KanaItem("ワ","wa"),  null,                 null,                 null,                 KanaItem("ヲ","wo"))),
        KanaRow("ン",   listOf(KanaItem("ン","n"),   null,                 null,                 null,                 null)),
    )

    val dakutenRows: List<KanaRow> = listOf(
        KanaRow("が행", listOf(KanaItem("が","ga"),  KanaItem("ぎ","gi"),  KanaItem("ぐ","gu"),  KanaItem("げ","ge"),  KanaItem("ご","go"))),
        KanaRow("ざ행", listOf(KanaItem("ざ","za"),  KanaItem("じ","ji"),  KanaItem("ず","zu"),  KanaItem("ぜ","ze"),  KanaItem("ぞ","zo"))),
        KanaRow("だ행", listOf(KanaItem("だ","da"),  KanaItem("ぢ","di"),  KanaItem("づ","du"),  KanaItem("で","de"),  KanaItem("ど","do"))),
        KanaRow("ば행", listOf(KanaItem("ば","ba"),  KanaItem("び","bi"),  KanaItem("ぶ","bu"),  KanaItem("べ","be"),  KanaItem("ぼ","bo"))),
        KanaRow("ぱ행", listOf(KanaItem("ぱ","pa"),  KanaItem("ぴ","pi"),  KanaItem("ぷ","pu"),  KanaItem("ぺ","pe"),  KanaItem("ぽ","po"))),
    )
}
