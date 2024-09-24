package com.example.aleksandarsekulovski_flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.aleksandarsekulovski_flashcardapp.ui.theme.AleksandarSekulovskiFlashcardAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AleksandarSekulovskiFlashcardAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlashcardsQuizApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class Flashcard(val question: String, val answer: String)

@Composable
fun FlashcardsQuizApp(modifier: Modifier = Modifier) {
    val flashcards = remember {
        listOf(
            Flashcard("What is the capital of France?", "Paris"),
            Flashcard("Who painted the Mona Lisa?", "Leonardo da Vinci"),
            Flashcard("What is the largest planet in our solar system?", "Jupiter")
        )
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var isQuizComplete by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isQuizComplete) {
            QuestionCard(flashcards[currentQuestionIndex].question)

            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = { Text("Your Answer") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val currentFlashcard = flashcards[currentQuestionIndex]
                    val isCorrect = userAnswer.trim().equals(currentFlashcard.answer, ignoreCase = true)

                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (isCorrect) "Correct!" else "Incorrect. The correct answer is ${currentFlashcard.answer}."
                        )
                    }

                    if (isCorrect) {
                        currentQuestionIndex++
                        userAnswer = ""

                        if (currentQuestionIndex >= flashcards.size) {
                            isQuizComplete = true
                            scope.launch {
                                snackbarHostState.showSnackbar("Quiz completed! Great job!")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer")
            }
        } else {
            Text("Quiz Complete!")
            Button(
                onClick = {
                    currentQuestionIndex = 0
                    userAnswer = ""
                    isQuizComplete = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restart Quiz")
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun QuestionCard(question: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = question,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardsQuizAppPreview() {
    AleksandarSekulovskiFlashcardAppTheme {
        FlashcardsQuizApp()
    }
}