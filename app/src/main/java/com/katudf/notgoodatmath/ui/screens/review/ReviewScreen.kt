package com.katudf.notgoodatmath.ui.screens.review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.katudf.notgoodatmath.MathApp
import com.katudf.notgoodatmath.data.QuestionBank
import com.katudf.notgoodatmath.data.model.Question
import com.katudf.notgoodatmath.domain.ReviewScheduler
import com.katudf.notgoodatmath.ui.screens.quiz.QuizScreen
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@Composable
fun ReviewScreen(onExit: () -> Unit) {
    val context = LocalContext.current
    val repository = remember {
        (context.applicationContext as MathApp).container.progressRepository
    }

    // 期限の来た復習問題を一度だけ読み込む。
    val questions by produceState<List<Question>?>(initialValue = null, repository) {
        val state = repository.state.first()
        val today = LocalDate.now().toEpochDay()
        value = ReviewScheduler.dueQuestionIds(state.reviews, today)
            .mapNotNull { QuestionBank.byId(it) }
    }

    when (val q = questions) {
        null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        else -> QuizScreen(
            title = "今日の復習",
            questions = q,
            emptyMessage = "今日の復習はありません。\n新しい問題に挑戦して、復習リストを育てよう！",
            onExit = onExit,
        )
    }
}
