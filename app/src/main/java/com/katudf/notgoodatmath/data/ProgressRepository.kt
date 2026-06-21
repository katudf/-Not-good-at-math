package com.katudf.notgoodatmath.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.katudf.notgoodatmath.data.model.ProfileState
import com.katudf.notgoodatmath.data.model.Question
import com.katudf.notgoodatmath.data.model.TopicStat
import com.katudf.notgoodatmath.domain.Gamification
import com.katudf.notgoodatmath.domain.ReviewScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "math_profile")

/** 学習データの保存・更新を一手に引き受けるリポジトリ。 */
class ProgressRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val profileKey = stringPreferencesKey("profile_json")

    val state: Flow<ProfileState> = context.dataStore.data.map { prefs ->
        prefs[profileKey]?.let { raw ->
            runCatching { json.decodeFromString<ProfileState>(raw) }.getOrDefault(ProfileState())
        } ?: ProfileState()
    }

    /** 解答結果を1件記録し、XP・連続日数・復習予定・バッジをまとめて更新する。 */
    suspend fun recordAnswer(question: Question, correct: Boolean): AnswerOutcome {
        var outcome = AnswerOutcome()
        context.dataStore.edit { prefs ->
            val current = prefs[profileKey]?.let {
                runCatching { json.decodeFromString<ProfileState>(it) }.getOrNull()
            } ?: ProfileState()

            val today = LocalDate.now().toEpochDay()

            // 分野別の集計
            val topicKey = question.topic.name
            val stat = current.topicStats[topicKey] ?: TopicStat()
            val newStat = stat.copy(
                attempts = stat.attempts + 1,
                correct = stat.correct + if (correct) 1 else 0,
            )

            // 連続学習日数（ストリーク）
            val newStreak = when (current.lastStudyEpochDay) {
                today -> current.currentStreak.coerceAtLeast(1)
                today - 1 -> current.currentStreak + 1
                else -> 1
            }

            // 復習スケジュール
            val updatedReview = ReviewScheduler.next(
                current = current.reviews[question.id],
                questionId = question.id,
                correct = correct,
                todayEpochDay = today,
            )

            val gainedXp = if (correct) question.difficulty.xp else 0

            val draft = current.copy(
                xp = current.xp + gainedXp,
                totalAnswered = current.totalAnswered + 1,
                totalCorrect = current.totalCorrect + if (correct) 1 else 0,
                topicStats = current.topicStats + (topicKey to newStat),
                reviews = current.reviews + (question.id to updatedReview),
                currentStreak = newStreak,
                bestStreak = maxOf(current.bestStreak, newStreak),
                lastStudyEpochDay = today,
            )

            val newBadges = Gamification.newlyEarnedBadges(draft)
            val finalState = draft.copy(earnedBadges = draft.earnedBadges + newBadges)

            val leveledUp = Gamification.levelFor(finalState.xp) > Gamification.levelFor(current.xp)

            outcome = AnswerOutcome(
                gainedXp = gainedXp,
                leveledUp = leveledUp,
                newLevel = Gamification.levelFor(finalState.xp),
                newBadgeIds = newBadges,
            )

            prefs[profileKey] = json.encodeToString(finalState)
        }
        return outcome
    }

    suspend fun reset() {
        context.dataStore.edit { it[profileKey] = json.encodeToString(ProfileState()) }
    }
}

/** 1問解いた直後に画面へ返すフィードバック情報。 */
data class AnswerOutcome(
    val gainedXp: Int = 0,
    val leveledUp: Boolean = false,
    val newLevel: Int = 1,
    val newBadgeIds: Set<String> = emptySet(),
)
