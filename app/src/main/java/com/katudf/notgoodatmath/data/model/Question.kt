package com.katudf.notgoodatmath.data.model

/** 問題の難易度。XP配分や色分けに使う。 */
enum class Difficulty(val label: String, val xp: Int) {
    BASIC("基礎", 10),
    STANDARD("標準", 20),
    ADVANCED("応用", 35),
}

/**
 * 1問を表すモデル。
 *
 * 選択式（[choices] が空でない）と、数値入力式（[choices] が空）の両方を扱える。
 * - 選択式: [answer] は正解の選択肢インデックス（"0" など）の文字列
 * - 入力式: [answer] は正規化後に一致すべき答え（例 "5", "-3/2", "2.5"）
 */
data class Question(
    val id: String,
    val topic: Topic,
    val difficulty: Difficulty,
    val prompt: String,
    val choices: List<String> = emptyList(),
    val answer: String,
    val explanation: String,
    val hint: String? = null,
) {
    val isChoice: Boolean get() = choices.isNotEmpty()

    val correctChoiceIndex: Int
        get() = answer.toIntOrNull() ?: -1

    /** 入力された解答が正解かどうかを判定する。 */
    fun isCorrect(userInput: String, selectedIndex: Int?): Boolean {
        return if (isChoice) {
            selectedIndex != null && selectedIndex == correctChoiceIndex
        } else {
            normalize(userInput) == normalize(answer)
        }
    }

    private fun normalize(value: String): String {
        // 全角→半角、空白除去、末尾の不要なゼロを丸める軽い正規化
        val half = value.trim()
            .replace('　', ' ')
            .map { ch ->
                when (ch) {
                    in '０'..'９' -> ('0' + (ch - '０'))
                    'ー', '－', '−' -> '-'
                    '．' -> '.'
                    '／' -> '/'
                    else -> ch
                }
            }
            .joinToString("")
            .replace(" ", "")
        // 小数の末尾ゼロ削除（"2.50" -> "2.5", "5.0" -> "5"）
        val asDouble = half.toDoubleOrNull()
        return if (asDouble != null) {
            if (asDouble == asDouble.toLong().toDouble()) asDouble.toLong().toString()
            else asDouble.toString()
        } else {
            half
        }
    }
}
