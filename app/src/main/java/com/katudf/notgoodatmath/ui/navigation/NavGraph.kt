package com.katudf.notgoodatmath.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.katudf.notgoodatmath.data.QuestionBank
import com.katudf.notgoodatmath.data.model.Topic
import com.katudf.notgoodatmath.ui.screens.geometry.GeometryScreen
import com.katudf.notgoodatmath.ui.screens.graph.GraphScreen
import com.katudf.notgoodatmath.ui.screens.home.HomeScreen
import com.katudf.notgoodatmath.ui.screens.progress.ProgressScreen
import com.katudf.notgoodatmath.ui.screens.quiz.QuizScreen
import com.katudf.notgoodatmath.ui.screens.review.ReviewScreen

private const val QUIZ_LENGTH = 8

@Composable
fun MathNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenTopicQuiz = { navController.navigate(Routes.quiz(it.name)) },
                onOpenMixedQuiz = { navController.navigate(Routes.quiz(Routes.MIXED)) },
                onOpenReview = { navController.navigate(Routes.REVIEW) },
                onOpenGraph = { navController.navigate(Routes.GRAPH) },
                onOpenGeometry = { navController.navigate(Routes.GEOMETRY) },
                onOpenProgress = { navController.navigate(Routes.PROGRESS) },
            )
        }

        composable(
            route = Routes.QUIZ,
            arguments = listOf(navArgument(Routes.ARG_TOPIC) { type = NavType.StringType }),
        ) { backStackEntry ->
            val topicArg = backStackEntry.arguments?.getString(Routes.ARG_TOPIC) ?: Routes.MIXED
            val topic = Topic.entries.firstOrNull { it.name == topicArg }

            // セッションの問題は遷移ごとに一度だけ確定する（再構成でシャッフルし直さない）。
            val questions = remember(topicArg) {
                val pool = if (topic == null) QuestionBank.all else QuestionBank.byTopic(topic)
                pool.shuffled().take(QUIZ_LENGTH)
            }
            val title = topic?.let { "${it.emoji} ${it.title}" } ?: "📝 ミックス演習"

            QuizScreen(
                title = title,
                questions = questions,
                emptyMessage = "この分野の問題がまだありません。",
                onExit = { navController.popBackStack() },
            )
        }

        composable(Routes.REVIEW) {
            ReviewScreen(onExit = { navController.popBackStack() })
        }

        composable(Routes.GRAPH) {
            GraphScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.GEOMETRY) {
            GeometryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROGRESS) {
            ProgressScreen(onBack = { navController.popBackStack() })
        }
    }
}
