package com.katudf.notgoodatmath.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.katudf.notgoodatmath.data.model.Question
import com.katudf.notgoodatmath.ui.AppViewModelProvider
import com.katudf.notgoodatmath.ui.components.ProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    title: String,
    questions: List<Question>,
    emptyMessage: String,
    onExit: () -> Unit,
    viewModel: QuizViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    LaunchedEffect(Unit) { viewModel.startOnce(questions, title) }
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title.ifEmpty { title }) },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Filled.Close, contentDescription = "閉じる")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.questions.isEmpty() -> EmptyState(emptyMessage, onExit)
                state.finished -> ResultView(
                    correct = state.correctCount,
                    total = state.total,
                    gainedXp = state.gainedXpTotal,
                    onRetry = { viewModel.restart() },
                    onExit = onExit,
                )

                else -> QuestionView(state, viewModel)
            }
        }
    }
}

@Composable
private fun QuestionView(state: QuizUiState, viewModel: QuizViewModel) {
    val q = state.current ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 進捗
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${state.index + 1} / ${state.total}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${q.topic.emoji} ${q.topic.title} ・ ${q.difficulty.label}",
                    style = MaterialTheme.typography.labelLarge,
                    color = q.topic.color,
                    fontWeight = FontWeight.Bold,
                )
            }
            ProgressBar(progress = state.progress, height = 8, color = q.topic.color)
        }

        // 問題文
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = q.prompt,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(20.dp),
            )
        }

        // 解答エリア
        if (q.isChoice) {
            q.choices.forEachIndexed { i, choice ->
                ChoiceRow(
                    text = choice,
                    selected = state.selectedIndex == i,
                    checked = state.checked,
                    isCorrect = i == q.correctChoiceIndex,
                    enabled = !state.checked,
                    onClick = { viewModel.selectChoice(i) },
                )
            }
        } else {
            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::updateInput,
                label = { Text("答えを入力") },
                singleLine = true,
                enabled = !state.checked,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // ヒント
        if (!state.checked && q.hint != null) {
            TextButton(onClick = viewModel::toggleHint) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (state.showHint) "ヒントを隠す" else "ヒントを見る")
            }
            AnimatedVisibility(visible = state.showHint) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                    ),
                ) {
                    Text(
                        text = "💡 ${q.hint}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        // フィードバック
        AnimatedVisibility(visible = state.checked) {
            FeedbackCard(correct = state.lastCorrect, question = q, outcomeXp = state.lastOutcome?.gainedXp ?: 0)
        }

        Spacer(Modifier.height(4.dp))

        // 操作ボタン
        if (state.checked) {
            Button(
                onClick = viewModel::next,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(if (state.isLast) "結果を見る" else "次の問題へ", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Button(
                onClick = viewModel::submit,
                enabled = state.canSubmit,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("確認する", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun ChoiceRow(
    text: String,
    selected: Boolean,
    checked: Boolean,
    isCorrect: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val correctColor = MaterialTheme.colorScheme.secondary
    val wrongColor = MaterialTheme.colorScheme.error
    val container = when {
        checked && isCorrect -> correctColor.copy(alpha = 0.18f)
        checked && selected && !isCorrect -> wrongColor.copy(alpha = 0.18f)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        checked && isCorrect -> correctColor
        checked && selected && !isCorrect -> wrongColor
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (checked && isCorrect) Text("⭕", style = MaterialTheme.typography.titleMedium)
        else if (checked && selected && !isCorrect) Text("❌", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun FeedbackCard(correct: Boolean, question: Question, outcomeXp: Int) {
    val color = if (correct) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (correct) "正解！ 🎉" else "おしい！",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.weight(1f),
                )
                if (correct && outcomeXp > 0) {
                    Text("+${outcomeXp} XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
                }
            }
            if (!correct) {
                Text(
                    text = "正解： ${correctAnswerText(question)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(text = question.explanation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun correctAnswerText(q: Question): String =
    if (q.isChoice) q.choices.getOrElse(q.correctChoiceIndex) { q.answer } else q.answer

@Composable
private fun ResultView(
    correct: Int,
    total: Int,
    gainedXp: Int,
    onRetry: () -> Unit,
    onExit: () -> Unit,
) {
    val rate = if (total == 0) 0 else (correct * 100 / total)
    val message = when {
        rate == 100 -> "全問正解！完璧だね 🏆"
        rate >= 70 -> "よくできました！この調子 👏"
        rate >= 40 -> "あと一歩。復習で伸ばそう 💪"
        else -> "大丈夫、間違えた問題が伸びしろ 🌱"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🎯", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Text("$correct / $total 問正解", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        ) {
            Text(
                "獲得XP： +$gainedXp",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
        }
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
        ) { Text("もう一度", style = MaterialTheme.typography.titleMedium) }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
        ) { Text("ホームへ戻る", style = MaterialTheme.typography.titleMedium) }
    }
}

@Composable
private fun EmptyState(message: String, onExit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("✅", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Text(message, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onExit, shape = RoundedCornerShape(16.dp)) { Text("ホームへ戻る") }
    }
}
