package com.katudf.notgoodatmath.data

import com.katudf.notgoodatmath.data.model.Difficulty
import com.katudf.notgoodatmath.data.model.Question
import com.katudf.notgoodatmath.data.model.Topic

/**
 * 問題データの一覧（高校受験＝中学数学の範囲）。
 * 解説とヒントを必ず添えて、「なぜそうなるか」を残すようにしている。
 */
object QuestionBank {

    val all: List<Question> = buildList {
        addAll(functionQuestions)
        addAll(equationQuestions)
        addAll(geometryQuestions)
        addAll(probabilityQuestions)
    }

    fun byTopic(topic: Topic): List<Question> = all.filter { it.topic == topic }

    fun byId(id: String): Question? = all.firstOrNull { it.id == id }

    private val functionQuestions
        get() = listOf(
            Question(
                id = "fn_01",
                topic = Topic.FUNCTION,
                difficulty = Difficulty.BASIC,
                prompt = "一次関数 y = 2x + 3 で、x = 4 のときの y の値は？",
                answer = "11",
                explanation = "x に 4 を代入：y = 2×4 + 3 = 8 + 3 = 11。\n「代入＝xの場所に数字を置くだけ」と考えると怖くない。",
                hint = "x のところに 4 を入れて計算するだけ。",
            ),
            Question(
                id = "fn_02",
                topic = Topic.FUNCTION,
                difficulty = Difficulty.STANDARD,
                prompt = "2点 (1, 3) と (3, 7) を通る直線の傾きは？",
                answer = "2",
                explanation = "傾き = (yの増加量) ÷ (xの増加量) = (7 − 3) ÷ (3 − 1) = 4 ÷ 2 = 2。",
                hint = "傾き = yの変化 ÷ xの変化。",
            ),
            Question(
                id = "fn_03",
                topic = Topic.FUNCTION,
                difficulty = Difficulty.BASIC,
                prompt = "一次関数 y = ax + b のグラフで、b が表すものはどれ？",
                choices = listOf("傾き", "y切片（y軸との交点）", "x切片", "原点"),
                answer = "1",
                explanation = "b は y切片。グラフが y軸と交わる点の y座標。a が傾き。",
                hint = "x = 0 を代入すると y = b になる。",
            ),
            Question(
                id = "fn_04",
                topic = Topic.FUNCTION,
                difficulty = Difficulty.BASIC,
                prompt = "二次関数 y = 2x² で、x = 3 のときの y の値は？",
                answer = "18",
                explanation = "y = 2 × 3² = 2 × 9 = 18。2乗を先に計算するのがポイント。",
                hint = "まず 3² を計算してから 2 倍。",
            ),
            Question(
                id = "fn_05",
                topic = Topic.FUNCTION,
                difficulty = Difficulty.STANDARD,
                prompt = "y = ax² のグラフが点 (2, 8) を通るとき、a の値は？",
                answer = "2",
                explanation = "点を代入：8 = a × 2² = 4a。よって a = 8 ÷ 4 = 2。",
                hint = "点の座標を代入して a について解く。",
            ),
            Question(
                id = "fn_06",
                topic = Topic.FUNCTION,
                difficulty = Difficulty.STANDARD,
                prompt = "一次関数 y = −3x + 5 のグラフについて正しいものは？",
                choices = listOf(
                    "右上がりの直線",
                    "右下がりの直線",
                    "x軸に平行な直線",
                    "原点を通る直線",
                ),
                answer = "1",
                explanation = "傾きが −3（マイナス）なので、xが増えるとyは減る＝右下がり。",
                hint = "傾きの符号で向きが決まる。マイナスなら？",
            ),
            Question(
                id = "fn_07",
                topic = Topic.FUNCTION,
                difficulty = Difficulty.ADVANCED,
                prompt = "二次関数 y = x² で、x が 1 から 3 まで変わるときの変化の割合は？",
                answer = "4",
                explanation = "変化の割合 = (yの増加量)÷(xの増加量) = (3²−1²)÷(3−1) = (9−1)÷2 = 8÷2 = 4。",
                hint = "x=1,3 のときの y を出して、傾きと同じ式で計算。",
            ),
        )

    private val equationQuestions
        get() = listOf(
            Question(
                id = "eq_01",
                topic = Topic.EQUATION,
                difficulty = Difficulty.BASIC,
                prompt = "方程式 2x + 5 = 11 を解くと x = ？",
                answer = "3",
                explanation = "5 を移項：2x = 11 − 5 = 6。両辺を 2 で割って x = 3。",
                hint = "まず +5 を右へ移項（符号が変わる）。",
            ),
            Question(
                id = "eq_02",
                topic = Topic.EQUATION,
                difficulty = Difficulty.STANDARD,
                prompt = "方程式 3(x − 2) = 9 を解くと x = ？",
                answer = "5",
                explanation = "両辺を 3 で割ると x − 2 = 3。よって x = 5。（カッコを展開しても 3x−6=9 → 3x=15 → x=5）",
                hint = "両辺を 3 で割るのが速い。",
            ),
            Question(
                id = "eq_03",
                topic = Topic.EQUATION,
                difficulty = Difficulty.STANDARD,
                prompt = "連立方程式 x + y = 5、x − y = 1 のとき、x の値は？",
                answer = "3",
                explanation = "2式を足すと 2x = 6 → x = 3。（このとき y = 2）",
                hint = "2つの式を足すと y が消える。",
            ),
            Question(
                id = "eq_04",
                topic = Topic.EQUATION,
                difficulty = Difficulty.STANDARD,
                prompt = "x² + 5x + 6 を因数分解すると？",
                choices = listOf(
                    "(x + 1)(x + 6)",
                    "(x + 2)(x + 3)",
                    "(x + 1)(x + 5)",
                    "(x − 2)(x − 3)",
                ),
                answer = "1",
                explanation = "かけて 6、たして 5 になる2数は 2 と 3。よって (x+2)(x+3)。",
                hint = "「かけて定数項、たしてxの係数」の2数を探す。",
            ),
            Question(
                id = "eq_05",
                topic = Topic.EQUATION,
                difficulty = Difficulty.STANDARD,
                prompt = "√8 を a√b の形に簡単にすると？",
                choices = listOf("2√2", "4√2", "2√4", "8"),
                answer = "0",
                explanation = "8 = 4 × 2 なので √8 = √4 × √2 = 2√2。",
                hint = "中身を「平方数 × 残り」に分ける。8 = 4×2。",
            ),
            Question(
                id = "eq_06",
                topic = Topic.EQUATION,
                difficulty = Difficulty.BASIC,
                prompt = "計算しなさい： −3 − (−7)",
                answer = "4",
                explanation = "−(−7) は +7 と同じ。−3 + 7 = 4。マイナスのマイナスはプラス。",
                hint = "− (−7) = +7 に直す。",
            ),
            Question(
                id = "eq_07",
                topic = Topic.EQUATION,
                difficulty = Difficulty.ADVANCED,
                prompt = "2次方程式 x² − 5x + 6 = 0 の解のうち、小さい方の値は？",
                answer = "2",
                explanation = "(x−2)(x−3)=0 より x = 2, 3。小さい方は 2。",
                hint = "因数分解して (x−○)(x−△)=0 の形に。",
            ),
        )

    private val geometryQuestions
        get() = listOf(
            Question(
                id = "ge_01",
                topic = Topic.GEOMETRY,
                difficulty = Difficulty.BASIC,
                prompt = "直角を挟む2辺が 3 と 4 の直角三角形。斜辺の長さは？",
                answer = "5",
                explanation = "三平方の定理：3² + 4² = 9 + 16 = 25 = 5²。よって斜辺は 5。",
                hint = "斜辺² = 3² + 4²。",
            ),
            Question(
                id = "ge_02",
                topic = Topic.GEOMETRY,
                difficulty = Difficulty.STANDARD,
                prompt = "斜辺が 13、片方の直角を挟む辺が 5 の直角三角形。もう1辺の長さは？",
                answer = "12",
                explanation = "三平方の定理：13² − 5² = 169 − 25 = 144 = 12²。よって 12。",
                hint = "辺² = 斜辺² − わかっている辺²。",
            ),
            Question(
                id = "ge_03",
                topic = Topic.GEOMETRY,
                difficulty = Difficulty.STANDARD,
                prompt = "1辺が 1 の正方形の対角線の長さは？",
                choices = listOf("1", "√2", "2", "√3"),
                answer = "1",
                explanation = "対角線で直角三角形ができる。1² + 1² = 2 なので対角線 = √2。",
                hint = "対角線は1辺の √2 倍。",
            ),
            Question(
                id = "ge_04",
                topic = Topic.GEOMETRY,
                difficulty = Difficulty.ADVANCED,
                prompt = "相似比が 2 : 3 の2つの図形。面積比はいくつ？",
                choices = listOf("2 : 3", "4 : 9", "4 : 6", "8 : 27"),
                answer = "1",
                explanation = "面積比は相似比の2乗：2² : 3² = 4 : 9。（体積比なら3乗で 8:27）",
                hint = "面積は2乗、体積は3乗。",
            ),
            Question(
                id = "ge_05",
                topic = Topic.GEOMETRY,
                difficulty = Difficulty.BASIC,
                prompt = "半径 3 の円の面積は？（π を使って答える）",
                choices = listOf("6π", "9π", "3π", "12π"),
                answer = "1",
                explanation = "円の面積 = π × 半径² = π × 3² = 9π。",
                hint = "面積 = π r²。",
            ),
            Question(
                id = "ge_06",
                topic = Topic.GEOMETRY,
                difficulty = Difficulty.BASIC,
                prompt = "三角形の3つの内角の和は何度？",
                answer = "180",
                explanation = "三角形の内角の和は必ず 180°。これは図形問題の基本中の基本。",
                hint = "どんな三角形でも同じ値。",
            ),
        )

    private val probabilityQuestions
        get() = listOf(
            Question(
                id = "pr_01",
                topic = Topic.PROBABILITY,
                difficulty = Difficulty.BASIC,
                prompt = "サイコロを1回振るとき、偶数の目が出る確率は？",
                choices = listOf("1/6", "1/3", "1/2", "2/3"),
                answer = "2",
                explanation = "偶数は 2,4,6 の3通り。全部で6通りなので 3/6 = 1/2。",
                hint = "(あてはまる場合の数) ÷ (全部の場合の数)。",
            ),
            Question(
                id = "pr_02",
                topic = Topic.PROBABILITY,
                difficulty = Difficulty.STANDARD,
                prompt = "コインを2枚投げるとき、2枚とも表が出る確率は？",
                choices = listOf("1/2", "1/3", "1/4", "1/8"),
                answer = "2",
                explanation = "出方は (表,表)(表,裏)(裏,表)(裏,裏) の4通り。2枚とも表は1通りなので 1/4。",
                hint = "全部の出方を書き出してみる（4通り）。",
            ),
            Question(
                id = "pr_03",
                topic = Topic.PROBABILITY,
                difficulty = Difficulty.STANDARD,
                prompt = "サイコロを1回振るとき、3以上の目が出る確率は？",
                choices = listOf("1/2", "2/3", "1/3", "5/6"),
                answer = "1",
                explanation = "3以上は 3,4,5,6 の4通り。4/6 = 2/3。",
                hint = "3,4,5,6 が何通りか数える。",
            ),
            Question(
                id = "pr_04",
                topic = Topic.PROBABILITY,
                difficulty = Difficulty.ADVANCED,
                prompt = "2個のサイコロを同時に振るとき、目の和が 7 になる確率は？",
                choices = listOf("1/6", "1/9", "5/36", "1/12"),
                answer = "0",
                explanation = "和が7は (1,6)(2,5)(3,4)(4,3)(5,2)(6,1) の6通り。全部で36通りなので 6/36 = 1/6。",
                hint = "全部で 6×6 = 36通り。和が7の組を数える。",
            ),
            Question(
                id = "pr_05",
                topic = Topic.PROBABILITY,
                difficulty = Difficulty.STANDARD,
                prompt = "A, B, C の3人を1列に並べる並べ方は全部で何通り？",
                answer = "6",
                explanation = "3 × 2 × 1 = 6通り（3の階乗）。先頭3通り×次2通り×最後1通り。",
                hint = "3 × 2 × 1 で計算する。",
            ),
            Question(
                id = "pr_06",
                topic = Topic.PROBABILITY,
                difficulty = Difficulty.STANDARD,
                prompt = "データ 3, 1, 4, 1, 5 の中央値（メジアン）は？",
                answer = "3",
                explanation = "小さい順に並べると 1, 1, 3, 4, 5。まん中の値は 3。",
                hint = "まず小さい順に並べ替える。",
            ),
        )
}
