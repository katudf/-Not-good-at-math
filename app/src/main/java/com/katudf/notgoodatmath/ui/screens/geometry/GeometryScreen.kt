package com.katudf.notgoodatmath.ui.screens.geometry

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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.katudf.notgoodatmath.ui.theme.ColorEquation
import com.katudf.notgoodatmath.ui.theme.ColorFunction
import com.katudf.notgoodatmath.ui.theme.ColorGeometry
import kotlin.math.roundToInt
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeometryScreen(onBack: () -> Unit) {
    var a by rememberSaveable { mutableFloatStateOf(3f) }
    var b by rememberSaveable { mutableFloatStateOf(4f) }
    val c = sqrt(a * a + b * b)

    val gridColor = MaterialTheme.colorScheme.surfaceVariant
    val labelColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("図形で遊ぶ（三平方の定理）") },
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
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "a² + b² = c²",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${fmt(a)}² + ${fmt(b)}² = ${fmt(a * a)} + ${fmt(b * b)} = ${fmt(c * c)}  →  c = ${"%.2f".format(c)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
                    drawPythagoras(a, b, c, gridColor, labelColor)
                }
            }

            SideSlider("辺 a = ${fmt(a)}", a, { a = it.roundToInt().toFloat() }, ColorFunction)
            SideSlider("辺 b = ${fmt(b)}", b, { b = it.roundToInt().toFloat() }, ColorEquation)

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                ),
            ) {
                Text(
                    text = "青の正方形（a²）と緑の正方形（b²）の面積をたすと、ちょうどオレンジの正方形（c²）の面積に等しくなる。これが三平方の定理。a, b を動かして、いつでも成り立つことを確かめよう。",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SideSlider(label: String, value: Float, onValueChange: (Float) -> Unit, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
        Slider(value = value, onValueChange = onValueChange, valueRange = 1f..6f, steps = 4)
    }
}

private fun DrawScope.drawPythagoras(a: Float, b: Float, c: Float, gridColor: Color, labelColor: Color) {
    // 直角を C(0,0)、A(a,0)、B(0,b) に置く。各辺の外側に正方形を描く。
    val triC = floatArrayOf(0f, 0f)
    val triA = floatArrayOf(a, 0f)
    val triB = floatArrayOf(0f, b)

    val sqA = listOf(0f to 0f, a to 0f, a to -a, 0f to -a)            // 辺CA上 → 下方向
    val sqB = listOf(0f to 0f, 0f to b, -b to b, -b to 0f)            // 辺CB上 → 左方向
    val sqC = listOf(a to 0f, 0f to b, b to (a + b), (a + b) to a)    // 斜辺AB上 → 外側

    val allPoints = sqA + sqB + sqC
    val minX = allPoints.minOf { it.first }
    val maxX = allPoints.maxOf { it.first }
    val minY = allPoints.minOf { it.second }
    val maxY = allPoints.maxOf { it.second }

    val pad = size.minDimension * 0.08f
    val scale = minOf(
        (size.width - 2 * pad) / (maxX - minX),
        (size.height - 2 * pad) / (maxY - minY),
    )

    fun map(mx: Float, my: Float) = Offset(
        x = pad + (mx - minX) * scale,
        y = size.height - (pad + (my - minY) * scale),
    )

    fun polygon(points: List<Pair<Float, Float>>): Path {
        val path = Path()
        points.forEachIndexed { i, (x, y) ->
            val p = map(x, y)
            if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        path.close()
        return path
    }

    // 正方形（塗り＋枠）
    drawPath(polygon(sqA), color = ColorFunction.copy(alpha = 0.25f))
    drawPath(polygon(sqA), color = ColorFunction, style = Stroke(width = 4f))
    drawPath(polygon(sqB), color = ColorEquation.copy(alpha = 0.25f))
    drawPath(polygon(sqB), color = ColorEquation, style = Stroke(width = 4f))
    drawPath(polygon(sqC), color = ColorGeometry.copy(alpha = 0.25f))
    drawPath(polygon(sqC), color = ColorGeometry, style = Stroke(width = 4f))

    // 三角形
    val tri = Path().apply {
        val pc = map(triC[0], triC[1]); moveTo(pc.x, pc.y)
        val pa = map(triA[0], triA[1]); lineTo(pa.x, pa.y)
        val pb = map(triB[0], triB[1]); lineTo(pb.x, pb.y)
        close()
    }
    drawPath(tri, color = labelColor.copy(alpha = 0.12f))
    drawPath(tri, color = labelColor, style = Stroke(width = 5f))

    // 面積ラベル
    val paint = android.graphics.Paint().apply {
        color = labelColor.toArgb()
        textSize = scale * 0.55f
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
    }
    fun label(text: String, cx: Float, cy: Float) {
        val p = map(cx, cy)
        drawContext.canvas.nativeCanvas.drawText(text, p.x, p.y + paint.textSize / 3, paint)
    }
    label("a²=${fmt(a * a)}", a / 2, -a / 2)
    label("b²=${fmt(b * b)}", -b / 2, b / 2)
    label("c²=${fmt(c * c)}", (a + b) / 2, (a + b) / 2)
}

private fun fmt(v: Float): String =
    if (v == v.toLong().toFloat()) v.toLong().toString() else "%.1f".format(v)
