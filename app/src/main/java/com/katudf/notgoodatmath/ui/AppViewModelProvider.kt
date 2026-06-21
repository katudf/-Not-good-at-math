package com.katudf.notgoodatmath.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.katudf.notgoodatmath.MathApp
import com.katudf.notgoodatmath.ui.screens.profile.ProfileViewModel
import com.katudf.notgoodatmath.ui.screens.quiz.QuizViewModel

/** 各ViewModelの生成方法をまとめたファクトリ。 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            QuizViewModel(mathApp().container.progressRepository)
        }
        initializer {
            ProfileViewModel(mathApp().container.progressRepository)
        }
    }
}

private fun CreationExtras.mathApp(): MathApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MathApp
