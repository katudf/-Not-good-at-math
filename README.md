# 数学きらい克服 📈 (Not Good at Math)

高校受験に向けて、**苦手な数学をグラフィカルに克服する**ための Android アプリ。
中学数学（高校受験の範囲）を、見て・動かして・解いて身につけることをねらった自習用アプリです。

> 受験生本人が、自分のペースで「苦手脱出」できることを目標に設計しています。

---

## ✨ 特徴

### 4分野をカバー（中学数学）
- 📈 **関数・グラフ**（一次関数・二次関数）
- 🧮 **方程式・計算**（方程式・連立・因数分解・平方根）
- 📐 **図形**（三平方の定理・相似・円）
- 🎲 **確率・統計**（確率・場合の数・データの活用）

### グラフィカルで体感的
- **グラフで遊ぶ**: スライダーで係数 a, b, c を動かすと、一次関数・二次関数のグラフがリアルタイムに変化。傾き・切片・放物線の開き方を「体で」理解できます。
- **図形で遊ぶ**: 三平方の定理を、3つの辺の上に正方形を描いて可視化。`a² + b² = c²` がいつでも成り立つことを、辺を動かしながら確かめられます。

### 解いて伸ばす
- ○×・選択・数値入力に対応した**問題演習**。
- 1問ごとに**即時採点＋やさしい解説＋ヒント**。
- 間違えた問題は**忘却曲線（ライトナー方式）**にもとづいて自動で復習リストに登録。

### 続けたくなる仕組み（ゲーム要素）
- 解くと **XP** が貯まり **レベル＆称号**アップ。
- **連続学習日数（ストリーク）**。
- 達成に応じて **バッジ** を獲得。
- 分野別の**正答率**を可視化して、苦手がひと目で分かります。

---

## 🧱 技術スタック（モダン構成）

| 項目 | 採用技術 |
| --- | --- |
| 言語 | Kotlin 2.1 |
| UI | Jetpack Compose + Material 3（Dynamic Color 対応） |
| 画面遷移 | Navigation Compose |
| 状態管理 | ViewModel + Kotlin Flow |
| 永続化 | DataStore (Preferences) + kotlinx.serialization |
| 描画 | Compose Canvas（グラフ・図形を自前描画） |
| ビルド | Android Gradle Plugin 8.9 / Gradle 8.14 / Version Catalog |

- `minSdk 26` / `targetSdk 35` / `compileSdk 35`
- 外部 DB やネットワーク不要。**完全オフライン・端末内完結**。

---

## 📂 構成

```
app/src/main/java/com/katudf/notgoodatmath/
├── MainActivity.kt            # エントリポイント
├── MathApp.kt                 # Application + 最小DIコンテナ
├── data/
│   ├── QuestionBank.kt        # 問題データ（解説・ヒント付き）
│   ├── ProgressRepository.kt  # DataStore による学習データの保存
│   └── model/                 # Topic / Question / ProfileState など
├── domain/
│   ├── Gamification.kt        # レベル・称号・バッジ
│   └── ReviewScheduler.kt     # 間隔反復（忘却曲線）
└── ui/
    ├── navigation/            # ルート定義と NavGraph
    ├── theme/                 # 色・タイポグラフィ
    ├── components/            # 共通コンポーザブル
    └── screens/               # home / graph / geometry / quiz / review / progress
```

設計の方針：アプリ規模に対して過剰にならないよう、Hilt 等は使わず**軽量な手動DI**にしています。

---

## 🚀 ビルド方法

Android SDK が入った環境（Android Studio 推奨）で：

```bash
# デバッグ APK をビルド
./gradlew assembleDebug

# 端末／エミュレータにインストール
./gradlew installDebug
```

または **Android Studio** でプロジェクトを開いて Run ▶ するだけです。

> 初回ビルド時に AGP・Compose 等の依存をネットワークから取得します。
> （Android Studio の SDK Manager で API 35 と最新のビルドツールを入れておいてください）

---

## 🗺️ 今後のアイデア

- 関数グラフを指でドラッグ＆2点間の傾きを表示
- 相似・円周角など図形の追加インタラクティブ教材
- 問題数の拡充と過去問モード
- 学習リマインダー通知
