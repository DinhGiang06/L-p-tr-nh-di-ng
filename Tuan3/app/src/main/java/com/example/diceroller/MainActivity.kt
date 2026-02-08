package com.example.diceroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.diceroller.ui.theme.DiceRollerTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerTheme {
                DiceApp()
            }
        }
    }
}

@Composable
fun DiceApp() {

    var diceValue by remember { mutableStateOf(1) }
    var rolling by remember { mutableStateOf(false) }
    var rotationTarget by remember { mutableStateOf(0f) }

    val rotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = "dice-rotation"
    )

    val diceImages = listOf(
        R.drawable.dice_1,
        R.drawable.dice_2,
        R.drawable.dice_3,
        R.drawable.dice_4,
        R.drawable.dice_5,
        R.drawable.dice_6
    )

    // ===== HIỆU ỨNG LĂN =====
    LaunchedEffect(rolling) {
        if (rolling) {
            repeat(8) {
                diceValue = Random.nextInt(1, 7)
                delay(80)
            }
            rolling = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5FF)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ===== XÚC XẮC =====
        Image(
            painter = painterResource(diceImages[diceValue - 1]),
            contentDescription = "Dice",
            modifier = Modifier
                .size(220.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ===== NÚT ROLL =====
        Button(
            enabled = !rolling,
            onClick = {
                rotationTarget += 720f   // quay 2 vòng
                rolling = true
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .width(140.dp)
                .height(52.dp)
        ) {
            Text(
                text = if (rolling) "Rolling..." else "Roll",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
