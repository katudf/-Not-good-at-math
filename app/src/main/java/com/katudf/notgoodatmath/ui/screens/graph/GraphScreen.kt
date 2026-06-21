package com.katudf.notgoodatmath.ui.screens.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.katudf.notgoodatmath.ui.theme.ColorFunction
import com.katudf.notgoodatmath.ui.theme.Coral
import kotlin.math.roundToInt

private const val RANGE = 10f // 表示範囲 -RANGE..RANGE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(onBack: () -> Unit) {
    var mode by rememberSaveable { mutableIntStateOf(0) } // 0=一次, 1=二次
    var a by rememberSaveable { mutableFloatStateOf(1f) }
    var b by rememberSaveable { mutableFloatStateOf(0f) }
    var c by rememberSaveable { mutableFloatStateOf(0f) }

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("グラフで遊ぶ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = mode == 0,
                    onClick = { mode = 0 },
                    shape = SegmentedButtonDefaults.itemShape(0, 2),
                ) { Text("一次関数") }
                SegmentedButton(
                    selected = mode == 1,
                    onClick = { mode = 1 },
                    shape = SegmentedButtonDefaults.itemShape(1, 2),
                ) { Text("二次関数") }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Text(
                    text = equationText(mode, a, b, c),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ColorFunction,
                    modifier = Modifier.padding(16.dp),
                )
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(12.dp),
                ) {
                    drawGrid(surfaceVariant, onSurface)
                    drawFunction(mode, a, b, c)
                }
            }

            // 係数スライダー
            CoefficientSlider(
                label = "a（傾き / 開き方）",
                value = a,
                onValueChange = { a = it },
                valueRange = -5f..5f,
                color = ColorFunction,
            )
            CoefficientSlider(
                label = if (mode == 0) "b（切片）" else "b",
                value = b,
                onValueChange = { b = it },
                valueRange = -8f..8f,
                color = MaterialTheme.colorScheme.secondary,
            )
            if (mode == 1) {
                CoefficientSlider(
                    label = "c（切片）",
                    value = c,
                    onValueChange = { c = it },
                    valueRange = -8f..8f,
                    color = Coral,
                )
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                ),
            ) {
                Text(
                    text = hintText(mode),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun CoefficientSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    color: Color,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = fmt(value),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
        Slider(
            value = value,
            onValueChange = { onValueChange((it * 2).roundToInt() / 2f) }, // 0.5刻み
            valueRange = valueRange,
        )
    }
}

private fun equationText(mode: Int, a: Float, b: Float, c: Float): String {
    return if (mode == 0) {
        "y = ${fmt(a)}x ${signed(b)}"
    } else {
        "y = ${fmt(a)}x² ${signedTerm(b, "x")} ${signed(c)}"
    }
}

private fun hintText(mode: Int): String = if (mode == 0) {
    "スライダーで a を変えると傾きが、b を変えると上下の位置（切片）が動くよ。a がマイナスだと右下がりになるのを確かめてみよう。"
} else {
    "a が大きいほどグラフは細く、小さいほど横に広がる。a がマイナスだと上下がひっくり返る（上に凸）。放物線の形を体で覚えよう。"
}

// ---- 描画ロジック ----

private fun DrawScope.drawGrid(gridColor: Color, axisColor: Color) {
    val w = size.width
    val h = size.height
    val unit = w / (RANGE * 2)
    // 補助線（軸は別で描くので i==0 は飛ばす）
    for (i in -RANGE.toInt()..RANGE.toInt()) {
        if (i == 0) continue
        val x = w / 2 + i * unit
        val y = h / 2 + i * unit
        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
        drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
    }
    // 軸
    drawLine(axisColor, Offset(w / 2, 0f), Offset(w / 2, h), strokeWidth = 3f)
    drawLine(axisColor, Offset(0f, h / 2), Offset(w, h / 2), strokeWidth = 3f)

    // 目盛りの数字（5刻み）
    val paint = android.graphics.Paint().apply {
        color = axisColor.copy(alpha = 0.7f).toArgb()
        textSize = unit * 0.7f
        isAntiAlias = true
    }
    listOf(-5, 5).forEach { i ->
        val x = w / 2 + i * unit
        drawContext.canvas.nativeCanvas.drawText(i.toString(), x - unit * 0.2f, h / 2 + unit * 0.9f, paint)
        val y = h / 2 - i * unit
        drawContext.canvas.nativeCanvas.drawText(i.toString(), w / 2 + unit * 0.2f, y + unit * 0.3f, paint)
    }
}

private fun DrawScope.drawFunction(mode: Int, a: Float, b: Float, c: Float) {
    val w = size.width
    val h = size.height
    val unit = w / (RANGE * 2)

    fun toPixel(mx: Float, my: Float) = Offset(
        x = w / 2 + mx * unit,
        y = h / 2 - my * unit,
    )

    val path = Path()
    var started = false
    var px = 0f
    while (px <= w) {
        val mx = (px - w / 2) / unit
        val my = if (mode == 0) a * mx + b else a * mx * mx + b * mx + c
        val p = toPixel(mx, my)
        if (my in -RANGE * 1.5f..RANGE * 1.5f) {
            if (!started) {
                path.moveTo(p.x, p.y); started = true
            } else {
                path.lineTo(p.x, p.y)
            }
        } else {
            started = false
        }
        px += 2f
    }
    drawPath(path, color = ColorFunction, style = Stroke(width = 6f))

    // 注目点：一次なら切片、二次なら頂点
    if (mode == 0) {
        drawCircle(Coral, radius = 9f, center = toPixel(0f, b))
    } else if (a != 0f) {
        val vx = -b / (2 * a)
        val vy = a * vx * vx + b * vx + c
        if (vy in -RANGE..RANGE && vx in -RANGE..RANGE) {
            drawCircle(Coral, radius = 9f, center = toPixel(vx, vy))
        }
    }
}

// ---- 数値フォーマット ----

private fun fmt(v: Float): String =
    if (v == v.toLong().toFloat()) v.toLong().toString() else v.toString()

private fun signed(v: Float): String =
    if (v >= 0) "+ ${fmt(v)}" else "- ${fmt(-v)}"

private fun signedTerm(v: Float, suffix: String): String =
    if (v >= 0) "+ ${fmt(v)}$suffix" else "- ${fmt(-v)}$suffix"
