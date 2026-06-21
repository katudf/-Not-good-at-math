package com.katudf.notgoodatmath.data.model

import kotlinx.serialization.Serializable

/** 分野ごとの解答実績。 */
@Serializable
data class TopicStat(
    val attempts: Int = 0,
    val correct: Int = 0,
) {
    val accuracy: Float get() = if (attempts == 0) 0f else correct.toFloat() / attempts
}

/**
 * 復習用の1問の状態（ライトナー方式＝忘却曲線にもとづく間隔反復）。
 * [box] が上がるほど次の復習までの間隔が長くなる。
 */
@Serializable
data class ReviewItem(
    val questionId: String,
    val box: Int = 0,
    val dueEpochDay: Long = 0,
    val lastCorrect: Boolean = false,
)

/** アプリ全体の保存対象データ。DataStore に JSON 1本で保存する。 */
@Serializable
data class ProfileState(
    val xp: Int = 0,
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val topicStats: Map<String, TopicStat> = emptyMap(),
    val reviews: Map<String, ReviewItem> = emptyMap(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastStudyEpochDay: Long = 0,
    val earnedBadges: Set<String> = emptySet(),
) {
    val accuracy: Float get() = if (totalAnswered == 0) 0f else totalCorrect.toFloat() / totalAnswered
}
