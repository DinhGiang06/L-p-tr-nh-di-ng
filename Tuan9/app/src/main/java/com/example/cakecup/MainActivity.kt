package com.example.cakecup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo

class MainActivity : ComponentActivity() {
    private val viewModel: BlurViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF006064), // Màu xanh đậm như trong ảnh
                    onPrimary = Color.White
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BlurScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun BlurScreen(viewModel: BlurViewModel) {
    val workInfos by viewModel.outputWorkInfos.observeAsState()
    val radioOptions = listOf("A little blurred", "More blurred", "The most blurred")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    val context = LocalContext.current

    val workInfo = workInfos?.firstOrNull()
    val isRunning = workInfo?.state == WorkInfo.State.RUNNING || workInfo?.state == WorkInfo.State.ENQUEUED
    val isFinished = workInfo?.state?.isFinished ?: false

    LaunchedEffect(workInfo) {
        if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
            val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)
            viewModel.setOutputUri(outputImageUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Ảnh Cupcake phía trên
        Image(
            painter = painterResource(id = R.drawable.android_cupcake),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Blur Amount",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp)
        )

        // Nhóm Radio Buttons
        Column(Modifier.selectableGroup()) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null // null vì Row đã handle click
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Nút Start
        Button(
            onClick = {
                val levels = when (selectedOption) {
                    "A little blurred" -> 1
                    "More blurred" -> 2
                    else -> 3
                }
                viewModel.applyBlur(levels)
            },
            enabled = !isRunning,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            if (isRunning) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Start", fontSize = 16.sp)
            }
        }

        // Nút xem kết quả (nếu đã xong)
        if (isFinished && viewModel.outputUri != null && !isRunning) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(viewModel.outputUri, "image/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("See File")
            }
        }
        
        if (isRunning) {
            Text(
                text = "Processing...",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                color = Color.Gray
            )
        }
    }
}