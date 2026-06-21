package com.katudf.notgoodatmath.data.model

import androidx.compose.ui.graphics.Color
import com.katudf.notgoodatmath.ui.theme.ColorEquation
import com.katudf.notgoodatmath.ui.theme.ColorFunction
import com.katudf.notgoodatmath.ui.theme.ColorGeometry
import com.katudf.notgoodatmath.ui.theme.ColorProbability

/** 中学数学の主要4分野（高校受験の範囲） */
enum class Topic(
    val title: String,
    val emoji: String,
    val color: Color,
    val description: String,
) {
    FUNCTION(
        title = "関数・グラフ",
        emoji = "📈",
        color = ColorFunction,
        description = "一次関数・二次関数を、グラフを動かして体で覚える",
    ),
    EQUATION(
        title = "方程式・計算",
        emoji = "🧮",
        color = ColorEquation,
        description = "方程式・連立・因数分解・平方根。計算力の土台",
    ),
    GEOMETRY(
        title = "図形",
        emoji = "📐",
        color = ColorGeometry,
        description = "三平方の定理・相似・円。図を動かして直感をつかむ",
    ),
    PROBABILITY(
        title = "確率・統計",
        emoji = "🎲",
        color = ColorProbability,
        description = "確率・場合の数・データの活用",
    );
}
