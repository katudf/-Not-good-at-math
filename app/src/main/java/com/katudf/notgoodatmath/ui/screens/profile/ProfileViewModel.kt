package com.katudf.notgoodatmath.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katudf.notgoodatmath.data.ProgressRepository
import com.katudf.notgoodatmath.data.model.ProfileState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ホーム・進捗画面が共有する、プロフィール状態の読み取り用ViewModel。 */
class ProfileViewModel(private val repository: ProgressRepository) : ViewModel() {

    val state = repository.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileState(),
    )

    fun resetProgress() {
        viewModelScope.launch { repository.reset() }
    }
}
