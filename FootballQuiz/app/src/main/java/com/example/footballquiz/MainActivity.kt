package com.example.footballquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = ProgressStore(this)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.statusBarsPadding().navigationBarsPadding().imePadding()) {
                        App(store)
                    }
                }
            }
        }
    }
}

@Composable
fun App(store: ProgressStore) {
    var idx by remember { mutableIntStateOf(questions.indexOfFirst { store.result(it.id) == null }) }
    if (idx == -1) {
        FinishScreen(store) { store.reset(); idx = 0 }
    } else {
        val q = questions[idx]
        key(q.id) {
            QuizScreen(q, idx + 1, questions.size, store) {
                idx = questions.indexOfFirst { store.result(it.id) == null }
            }
        }
    }
}

@Composable
fun QuizScreen(q: Question, number: Int, total: Int, store: ProgressStore, onNext: () -> Unit) {
    var used by remember { mutableIntStateOf(store.attempts(q.id)) }
    var solved by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    var wrongMsg by remember { mutableStateOf(false) }

    val failed = !solved && used >= 5
    val finished = solved || failed
    val shown = if (solved) used else minOf(used + 1, 5)

    fun submit() {
        if (finished || input.isBlank()) return
        used += 1
        if (q.isCorrect(input)) {
            solved = true
            wrongMsg = false
            store.saveResult(q.id, used)
        } else {
            wrongMsg = true
            if (used >= 5) store.saveResult(q.id, -1) else store.saveAttempts(q.id, used)
        }
        input = ""
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("⚽ 문제 $number / $total", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("누구일까요? 틀릴 때마다 다음 힌트가 열려요.")
        Text(
            "남은 기회  " + "❤️".repeat(5 - used.coerceAtMost(5)) + "🖤".repeat(used.coerceAtMost(5)),
            fontSize = 18.sp
        )

        for (i in 0 until 5) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (i < shown) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    if (i < shown) "힌트 ${i + 1}  ${q.hints[i]}" else "🔒 힌트 ${i + 1}",
                    Modifier.padding(14.dp)
                )
            }
        }

        if (!finished) {
            if (wrongMsg) Text("❌ 틀렸어요! 다음 힌트가 열렸어요.", color = MaterialTheme.colorScheme.error)
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("선수 이름") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() })
            )
            Button(onClick = { submit() }, Modifier.fillMaxWidth()) { Text("제출") }
        } else {
            Text(
                if (solved) "🎉 정답! ${q.displayName}  (+${6 - used}점)"
                else "😢 실패! 정답은 ${q.displayName}",
                fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            Button(onClick = onNext, Modifier.fillMaxWidth()) {
                Text(if (number == total) "결과 보기" else "다음 문제")
            }
        }
    }
}

@Composable
fun FinishScreen(store: ProgressStore, onReset: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text("🏆 모든 문제 완료!", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text("맞힌 문제: ${store.solvedCount()} / ${questions.size}", fontSize = 18.sp)
        Text("총점: ${store.totalScore()} / ${questions.size * 5}", fontSize = 18.sp)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onReset) { Text("처음부터 다시 하기") }
    }
}
