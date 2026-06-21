package com.katudf.notgoodatmath.ui.screens.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katudf.notgoodatmath.data.AnswerOutcome
import com.katudf.notgoodatmath.data.ProgressRepository
import com.katudf.notgoodatmath.data.model.Question
import kotlinx.coroutines.launch

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val index: Int = 0,
    val selectedIndex: Int? = null,
    val input: String = "",
    val checked: Boolean = false,
    val lastCorrect: Boolean = false,
    val showHint: Boolean = false,
    val correctCount: Int = 0,
    val gainedXpTotal: Int = 0,
    val lastOutcome: AnswerOutcome? = null,
    val finished: Boolean = false,
    val title: String = "演習",
) {
    val current: Question? get() = questions.getOrNull(index)
    val total: Int get() = questions.size
    val isLast: Boolean get() = index == questions.lastIndex
    val progress: Float get() = if (total == 0) 0f else (index + if (checked) 1 else 0).toFloat() / total
    val canSubmit: Boolean
        get() = current?.let {
            if (it.isChoice) selectedIndex != null else input.isNotBlank()
        } ?: false
}

/** 演習・復習セッションを進行させるViewModel。 */
class QuizViewModel(private val repository: ProgressRepository) : ViewModel() {

    var uiState by mutableStateOf(QuizUiState())
        private set

    private var started = false

    /** セッション開始。すでに開始済みなら再初期化しない（回転対策）。 */
    fun startOnce(questions: List<Question>, title: String) {
        if (started) return
        started = true
        uiState = QuizUiState(questions = questions, title = title)
    }

    /** 同じ問題セットでもう一度。 */
    fun restart() {
        uiState = QuizUiState(questions = uiState.questions, title = uiState.title)
    }

    fun selectChoice(i: Int) {
        if (uiState.checked) return
        uiState = uiState.copy(selectedIndex = i)
    }

    fun updateInput(text: String) {
        if (uiState.checked) return
        uiState = uiState.copy(input = text)
    }

    fun toggleHint() {
        uiState = uiState.copy(showHint = !uiState.showHint)
    }

    fun submit() {
        val q = uiState.current ?: return
        if (uiState.checked || !uiState.canSubmit) return
        val correct = q.isCorrect(uiState.input, uiState.selectedIndex)
        viewModelScope.launch {
            val outcome = repository.recordAnswer(q, correct)
            uiState = uiState.copy(
                checked = true,
                lastCorrect = correct,
                correctCount = uiState.correctCount + if (correct) 1 else 0,
                gainedXpTotal = uiState.gainedXpTotal + outcome.gainedXp,
                lastOutcome = outcome,
            )
        }
    }

    fun next() {
        if (!uiState.checked) return
        if (uiState.isLast) {
            uiState = uiState.copy(finished = true)
        } else {
            uiState = uiState.copy(
                index = uiState.index + 1,
                selectedIndex = null,
                input = "",
                checked = false,
                lastCorrect = false,
                showHint = false,
                lastOutcome = null,
            )
        }
    }
}
