package com.katudf.notgoodatmath.domain

import com.katudf.notgoodatmath.data.model.ProfileState
import com.katudf.notgoodatmath.data.model.Topic

/** レベル・称号・バッジなど「やる気を維持する」仕組みをまとめたロジック。 */
object Gamification {

    /** レベル n に到達するのに必要な累計XP。少しずつ重くなる。 */
    fun cumulativeXpForLevel(level: Int): Int {
        // level1=0, level2=100, level3=250, level4=450 ... 二次関数的に増える
        if (level <= 1) return 0
        val n = level - 1
        return 50 * n * (n + 1)
    }

    fun levelFor(xp: Int): Int {
        var level = 1
        while (cumulativeXpForLevel(level + 1) <= xp) level++
        return level
    }

    /** 現在レベル内での進捗（0f..1f）。 */
    fun levelProgress(xp: Int): Float {
        val level = levelFor(xp)
        val base = cumulativeXpForLevel(level)
        val next = cumulativeXpForLevel(level + 1)
        if (next == base) return 1f
        return ((xp - base).toFloat() / (next - base)).coerceIn(0f, 1f)
    }

    fun xpToNextLevel(xp: Int): Int {
        val level = levelFor(xp)
        return (cumulativeXpForLevel(level + 1) - xp).coerceAtLeast(0)
    }

    fun titleFor(level: Int): String = when {
        level >= 20 -> "数学マスター"
        level >= 15 -> "受験エース"
        level >= 10 -> "数学が得意かも"
        level >= 6 -> "コツをつかんだ人"
        level >= 3 -> "苦手脱出中"
        else -> "数学かけだし"
    }

    data class Badge(
        val id: String,
        val title: String,
        val emoji: String,
        val description: String,
    )

    val allBadges: List<Badge> = listOf(
        Badge("first_step", "はじめの一歩", "👟", "はじめて1問解いた"),
        Badge("ten_correct", "10問正解", "🔟", "正解を10問ためた"),
        Badge("fifty_answered", "コツコツ50問", "📚", "累計50問に挑戦した"),
        Badge("streak_3", "3日連続", "🔥", "3日連続で学習した"),
        Badge("streak_7", "1週間継続", "🗓️", "7日連続で学習した"),
        Badge("accuracy_80", "正答率80%", "🎯", "累計正答率が80%を超えた（20問以上）"),
        Badge("all_topics", "全分野制覇", "🌈", "4分野すべてで正解した"),
        Badge("level_10", "レベル10", "⭐", "レベル10に到達した"),
    )

    /** 現在の状態から、新たに獲得すべきバッジIDを判定する。 */
    fun newlyEarnedBadges(state: ProfileState): Set<String> {
        val earned = mutableSetOf<String>()
        if (state.totalAnswered >= 1) earned += "first_step"
        if (state.totalCorrect >= 10) earned += "ten_correct"
        if (state.totalAnswered >= 50) earned += "fifty_answered"
        if (state.currentStreak >= 3) earned += "streak_3"
        if (state.currentStreak >= 7) earned += "streak_7"
        if (state.totalAnswered >= 20 && state.accuracy >= 0.8f) earned += "accuracy_80"
        if (Topic.entries.all { (state.topicStats[it.name]?.correct ?: 0) >= 1 }) earned += "all_topics"
        if (levelFor(state.xp) >= 10) earned += "level_10"
        return earned - state.earnedBadges
    }

    fun badgeById(id: String): Badge? = allBadges.firstOrNull { it.id == id }
}
