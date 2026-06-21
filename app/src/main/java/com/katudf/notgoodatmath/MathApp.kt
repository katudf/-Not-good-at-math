package com.katudf.notgoodatmath

import android.app.Application
import com.katudf.notgoodatmath.data.ProgressRepository

/** アプリ本体。依存（リポジトリ）を生成して保持するシンプルなコンテナ。 */
class MathApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** ライブラリ無しの最小DI。アプリ規模に対して過剰にしないための判断。 */
class AppContainer(app: MathApp) {
    val progressRepository: ProgressRepository = ProgressRepository(app)
}
