package com.usnine.shiori.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.usnine.shiori.R

enum class DuckMood(@DrawableRes val resId: Int) {
    DEFAULT(R.drawable.duck_default),
    CORRECT(R.drawable.duck_correct),
    WRONG(R.drawable.duck_wrong),
    COMPLETE(R.drawable.duck_complete),
    EMPTY(R.drawable.duck_empty),
}

@Composable
fun DuckImage(
    mood: DuckMood,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    Image(
        painter            = painterResource(mood.resId),
        contentDescription = null,
        contentScale       = contentScale,
        modifier           = modifier,
    )
}
