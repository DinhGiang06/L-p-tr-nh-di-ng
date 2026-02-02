package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProfileCard()
            }
        }
    }
}

@Composable
fun ProfileCard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ===== CARD TRÊN =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_camera),
                contentDescription = "Avatar",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Full Name",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Title",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ===== CARD DƯỚI =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {

            Text(
                text = "📞 +00 (00) 000 000",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🌐 @socialmediahandle",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "✉ email@domain.com",
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileCard() {
    MaterialTheme {
        ProfileCard()
    }
}
