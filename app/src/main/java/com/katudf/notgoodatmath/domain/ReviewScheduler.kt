package com.katudf.notgoodatmath.domain

import com.katudf.notgoodatmath.data.model.ReviewItem

/**
 * 間隔反復（忘却曲線）スケジューラ。ライトナーの箱方式。
 * 正解で箱が1つ上がり次の復習が先になる。間違えると箱0に戻ってすぐ復習対象になる。
 */
object ReviewScheduler {

    /** 各箱の「次の復習までの日数」。箱が上がるほど間隔が伸びる。 */
    private val intervalsDays = intArrayOf(0, 1, 3, 7, 16, 35)

    val maxBox: Int get() = intervalsDays.lastIndex

    fun next(current: ReviewItem?, questionId: String, correct: Boolean, todayEpochDay: Long): ReviewItem {
        val prevBox = current?.box ?: 0
        val newBox = if (correct) (prevBox + 1).coerceAtMost(maxBox) else 0
        val due = todayEpochDay + intervalsDays[newBox]
        return ReviewItem(
            questionId = questionId,
            box = newBox,
            dueEpochDay = due,
            lastCorrect = correct,
        )
    }

    /** 今日復習すべき問題ID（期限が来ていて、まだ完全マスターでないもの）。 */
    fun dueQuestionIds(reviews: Map<String, ReviewItem>, todayEpochDay: Long): List<String> =
        reviews.values
            .filter { it.dueEpochDay <= todayEpochDay && it.box < maxBox }
            .sortedWith(compareBy({ it.box }, { it.dueEpochDay }))
            .map { it.questionId }
}
