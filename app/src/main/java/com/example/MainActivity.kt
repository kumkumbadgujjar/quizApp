package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SimpleQuizTheme

// Data class representing each quiz question
data class Question(
  val question: String,
  val options: List<String>,
  val correctAnswer: String
)

// Question pool containing unique questions with distinct options and answers
val questionPool = listOf(
  Question(
    question = "What is the capital of France?",
    options = listOf("Paris", "London", "Berlin", "Madrid"),
    correctAnswer = "Paris"
  ),
  Question(
    question = "Which planet is known as the Red Planet?",
    options = listOf("Earth", "Mars", "Jupiter", "Venus"),
    correctAnswer = "Mars"
  ),
  Question(
    question = "Which language is primarily used for Android development?",
    options = listOf("Kotlin", "HTML", "SQL", "CSS"),
    correctAnswer = "Kotlin"
  ),
  Question(
    question = "How many days are there in a week?",
    options = listOf("5", "6", "7", "8"),
    correctAnswer = "7"
  ),
  Question(
    question = "Which is the largest ocean on Earth?",
    options = listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"),
    correctAnswer = "Pacific Ocean"
  )
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SimpleQuizTheme {
        QuizApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizApp() {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Simple Quiz",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("app_bar_title")
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
      )
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    QuizScreen(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    )
  }
}

@Composable
fun QuizScreen(modifier: Modifier = Modifier) {
  // Use the pool of questions
  val questions = remember { questionPool }

  // State management
  var currentQuestionIndex by remember { mutableIntStateOf(0) }
  var selectedAnswer by remember { mutableStateOf<String?>(null) }
  var isSubmitted by remember { mutableStateOf(false) }
  var isAnswerCorrect by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }
  var isQuizCompleted by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .verticalScroll(rememberScrollState())
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 520.dp),
      horizontalAlignment = Alignment.Start
    ) {
      if (isQuizCompleted) {
        // --- Final Screen: Quiz Completed ---
        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Quiz Completed!",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("completed_title"),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF6F8FA)
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Your Score: $score / ${questions.size}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E6091),
              modifier = Modifier.testTag("score_text")
            )

            Spacer(modifier = Modifier.height(8.dp))

            val feedbackText = when {
              score == questions.size -> "Excellent! Perfect score!"
              score >= 3 -> "Good job! You passed the quiz."
              else -> "Keep practicing and try again!"
            }

            Text(
              text = feedbackText,
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFF4B5563)
            )
          }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
          onClick = {
            currentQuestionIndex = 0
            selectedAnswer = null
            isSubmitted = false
            isAnswerCorrect = false
            score = 0
            isQuizCompleted = false
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("restart_button")
        ) {
          Text(
            text = "Restart Quiz",
            fontSize = 16.sp
          )
        }
      } else {
        // --- Question Screen ---
        val currentQuestion = questions[currentQuestionIndex]

        // 1. App Title
        Text(
          text = "Simple Quiz",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.testTag("title_text")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Question Number
        Text(
          text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF4B5563),
          modifier = Modifier.testTag("question_number_text")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Question Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF6F8FA)
          )
        ) {
          Text(
            text = currentQuestion.question,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937),
            modifier = Modifier
              .padding(16.dp)
              .testTag("question_text")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. RadioButton Answer Options
        Text(
          text = "Select an option:",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF4B5563)
        )

        Spacer(modifier = Modifier.height(8.dp))

        currentQuestion.options.forEachIndexed { index, option ->
          val isSelected = (selectedAnswer == option)

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .selectable(
                selected = isSelected,
                enabled = !isSubmitted,
                role = Role.RadioButton,
                onClick = {
                  if (!isSubmitted) {
                    selectedAnswer = option
                  }
                }
              )
              .testTag("option_row_$index"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = isSelected,
              onClick = {
                if (!isSubmitted) {
                  selectedAnswer = option
                }
              },
              enabled = !isSubmitted,
              modifier = Modifier.testTag("radio_option_$index")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = option,
              style = MaterialTheme.typography.bodyLarge,
              color = if (isSubmitted && !isSelected) Color(0xFF9CA3AF) else Color(0xFF111827),
              modifier = Modifier.testTag("label_option_$index")
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5 & 7. Submit vs Next Question Button
        if (!isSubmitted) {
          // Submit Button (Enabled ONLY when an answer has been selected)
          Button(
            onClick = {
              if (selectedAnswer != null) {
                isSubmitted = true
                if (selectedAnswer == currentQuestion.correctAnswer) {
                  isAnswerCorrect = true
                  score += 1
                } else {
                  isAnswerCorrect = false
                }
              }
            },
            enabled = selectedAnswer != null,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("submit_button")
          ) {
            Text(
              text = "Submit",
              fontSize = 16.sp
            )
          }
        } else {
          // 6. Result Message after submission
          val resultText = if (isAnswerCorrect) "Correct!" else "Incorrect!"
          val resultColor = if (isAnswerCorrect) Color(0xFF15803D) else Color(0xFFB91C1C)

          Text(
            text = resultText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = resultColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("result_text")
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 7. Next Question Button
          val isLastQuestion = (currentQuestionIndex == questions.size - 1)

          Button(
            onClick = {
              if (isLastQuestion) {
                isQuizCompleted = true
              } else {
                // Advance to the next unique question
                currentQuestionIndex += 1
                // Reset selected option
                selectedAnswer = null
                // Reset submission and result status
                isSubmitted = false
                isAnswerCorrect = false
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("next_button")
          ) {
            Text(
              text = "Next Question",
              fontSize = 16.sp
            )
          }
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
  SimpleQuizTheme {
    QuizScreen()
  }
}
