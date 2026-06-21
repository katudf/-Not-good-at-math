package com.katudf.notgoodatmath.ui.navigation

/** 画面ルートの定義。 */
object Routes {
    const val HOME = "home"
    const val QUIZ = "quiz/{topic}"
    const val REVIEW = "review"
    const val GRAPH = "graph"
    const val GEOMETRY = "geometry"
    const val PROGRESS = "progress"

    /** topic には Topic.name か "MIXED" を渡す。 */
    fun quiz(topic: String) = "quiz/$topic"

    const val ARG_TOPIC = "topic"
    const val MIXED = "MIXED"
}
