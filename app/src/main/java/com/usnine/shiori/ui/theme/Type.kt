package com.usnine.shiori.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.usnine.shiori.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val NotoSerifJpFamily = FontFamily(
    Font(googleFont = GoogleFont("Noto Serif JP"), fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = GoogleFont("Noto Serif JP"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Noto Serif JP"), fontProvider = provider, weight = FontWeight.Medium),
)

private val NotoSansKR = FontFamily(
    Font(googleFont = GoogleFont("Noto Sans KR"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Noto Sans KR"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Noto Sans KR"), fontProvider = provider, weight = FontWeight.Bold),
)

val Typography = Typography(
    // 앱 이름 / 대형 헤더
    displayLarge = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp,
    ),
    // 섹션 제목
    titleMedium = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    // 본문
    bodyMedium = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
    ),
    // 캡션 / 태그
    labelSmall = TextStyle(
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
