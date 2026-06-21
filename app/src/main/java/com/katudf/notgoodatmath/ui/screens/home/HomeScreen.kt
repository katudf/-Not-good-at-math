package com.katudf.notgoodatmath.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.katudf.notgoodatmath.data.QuestionBank
import com.katudf.notgoodatmath.data.model.Topic
import com.katudf.notgoodatmath.domain.Gamification
import com.katudf.notgoodatmath.domain.ReviewScheduler
import com.katudf.notgoodatmath.ui.AppViewModelProvider
import com.katudf.notgoodatmath.ui.components.EmojiBadge
import com.katudf.notgoodatmath.ui.components.ProgressBar
import com.katudf.notgoodatmath.ui.components.SectionHeader
import com.katudf.notgoodatmath.ui.components.StatBlock
import com.katudf.notgoodatmath.ui.screens.profile.ProfileViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(
    onOpenTopicQuiz: (Topic) -> Unit,
    onOpenMixedQuiz: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenGraph: () -> Unit,
    onOpenGeometry: () -> Unit,
    onOpenProgress: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val today = LocalDate.now().toEpochDay()
    val dueCount = ReviewScheduler.dueQuestionIds(state.reviews, today).size
    val level = Gamification.levelFor(state.xp)

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "今日もコツコツいこう 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        item {
            LevelCard(
                level = level,
                title = Gamification.titleFor(level),
                xp = state.xp,
                progress = Gamification.levelProgress(state.xp),
                xpToNext = Gamification.xpToNextLevel(state.xp),
                streak = state.currentStreak,
                accuracy = state.accuracy,
                onClick = onOpenProgress,
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction(
                    emoji = "📝",
                    label = "ミックス演習",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenMixedQuiz,
                )
                QuickAction(
                    emoji = "🔁",
                    label = if (dueCount > 0) "復習 ($dueCount)" else "復習",
                    color = Color(0xFFFF8A3D),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenReview,
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction(
                    emoji = "📈",
                    label = "グラフで遊ぶ",
                    color = Color(0xFF36A2EB),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenGraph,
                )
                QuickAction(
                    emoji = "📐",
                    label = "図形で遊ぶ",
                    color = Color(0xFF9C6BFF),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenGeometry,
                )
            }
        }

        item { SectionHeader("分野から選ぶ") }

        items(Topic.entries) { topic ->
            val stat = state.topicStats[topic.name]
            TopicCard(
                topic = topic,
                attempts = stat?.attempts ?: 0,
                accuracy = stat?.accuracy ?: 0f,
                questionCount = QuestionBank.byTopic(topic).size,
                onClick = { onOpenTopicQuiz(topic) },
            )
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun LevelCard(
    level: Int,
    title: String,
    xp: Int,
    progress: Float,
    xpToNext: Int,
    streak: Int,
    accuracy: Float,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF4C5BD4), Color(0xFF7A5BFF)),
                    ),
                )
                .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Lv. $level",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                    Text(
                        text = "🔥 $streak 日",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                ProgressBar(
                    progress = progress,
                    color = Color(0xFFFFD54F),
                    track = Color.White.copy(alpha = 0.25f),
                )
                Text(
                    text = "あと ${xpToNext}XP で次のレベル ・ 正答率 ${(accuracy * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
        }
    }
}

@Composable
private fun QuickAction(
    emoji: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EmojiBadge(emoji = emoji, background = color)
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun TopicCard(
    topic: Topic,
    attempts: Int,
    accuracy: Float,
    questionCount: Int,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            EmojiBadge(emoji = topic.emoji, background = topic.color, size = 52)
            Column(Modifier.weight(1f)) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                ProgressBar(progress = accuracy, height = 8, color = topic.color)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (attempts == 0) "未挑戦 ・ $questionCount 問" else "正答率 ${(accuracy * 100).toInt()}% ・ ${attempts}回挑戦",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
