package com.example.footballquiz

import android.content.Context

/** 기기 내부(SharedPreferences)에만 저장. 로그인/서버 없음. */
class ProgressStore(ctx: Context) {
    private val p = ctx.getSharedPreferences("quiz_progress", Context.MODE_PRIVATE)

    /** 완료 결과: null=미완료, -1=실패, 1~5=맞힌 시도 횟수 */
    fun result(id: Int): Int? = if (p.contains("r$id")) p.getInt("r$id", -1) else null
    fun saveResult(id: Int, value: Int) = p.edit().putInt("r$id", value).apply()

    /** 진행 중인 문제에서 지금까지 사용한 시도 횟수 (앱을 껐다 켜도 유지) */
    fun attempts(id: Int): Int = p.getInt("a$id", 0)
    fun saveAttempts(id: Int, n: Int) = p.edit().putInt("a$id", n).apply()

    fun reset() = p.edit().clear().apply()

    /** 점수: 적게 시도할수록 높음 (1번만에 5점 ... 5번만에 1점, 실패 0점) */
    fun totalScore(): Int = questions.sumOf { q ->
        val r = result(q.id) ?: 0
        if (r in 1..5) 6 - r else 0
    }
    fun solvedCount(): Int = questions.count { (result(it.id) ?: -1) in 1..5 }
}
