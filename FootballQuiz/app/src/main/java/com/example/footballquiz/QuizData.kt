package com.example.footballquiz

/**
 * 문제 데이터. 새 문제는 아래 목록에 Question(...)을 추가하면 됩니다.
 * - id: 겹치지 않는 번호 (저장 키로 사용되므로 한번 정하면 바꾸지 마세요)
 * - hints: 정확히 5개, 어려운 힌트 -> 쉬운 힌트 순서
 * - answers: 정답으로 인정할 표기들 (한글/영문 등). 대소문자·공백은 무시됩니다.
 */
data class Question(
    val id: Int,
    val hints: List<String>,
    val answers: List<String>,
    val displayName: String
) {
    fun isCorrect(input: String): Boolean {
        val x = normalize(input)
        return x.isNotEmpty() && answers.any { normalize(it) == x }
    }
}

private fun normalize(s: String) =
    s.lowercase().filter { it.isLetterOrDigit() }

val questions = listOf(
    Question(
        id = 1,
        hints = listOf(
            "1992년 강원도 춘천에서 태어났다.",
            "독일 함부르크 유소년팀에서 성장했다.",
            "바이어 레버쿠젠을 거쳐 2015년 프리미어리그 팀으로 이적했다.",
            "2021-22 시즌 프리미어리그 득점왕을 공동 수상했다.",
            "토트넘 홋스퍼에서 뛰었던 한국 대표팀 주장이다."
        ),
        answers = listOf("손흥민", "son heungmin", "heungmin son", "son"),
        displayName = "손흥민"
    ),
    Question(
        id = 2,
        hints = listOf(
            "1981년 서울에서 태어났다.",
            "일본 J리그 교토 퍼플상가에서 프로 생활을 시작했다.",
            "네덜란드 PSV 에인트호벤을 거쳐 잉글랜드로 진출했다.",
            "맨체스터 유나이티드에서 프리미어리그 우승을 4회 경험했다.",
            "'두 개의 심장'이라는 별명으로 불렸다."
        ),
        answers = listOf("박지성", "park jisung", "jisung park"),
        displayName = "박지성"
    ),
    Question(
        id = 3,
        hints = listOf(
            "1987년 아르헨티나 로사리오에서 태어났다.",
            "어린 시절 성장호르몬 결핍 치료를 받았다.",
            "발롱도르를 8회 수상했다.",
            "2022 카타르 월드컵에서 조국에 우승을 안겼다.",
            "FC 바르셀로나에서 오랫동안 등번호 10번을 달았다."
        ),
        answers = listOf("리오넬 메시", "메시", "lionel messi", "messi"),
        displayName = "리오넬 메시"
    )
)
