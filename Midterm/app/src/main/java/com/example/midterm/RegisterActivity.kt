package com.example.midterm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RegisterScreen(
                    onRegisterSuccess = { finish() },
                    onBackToLogin = { finish() }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        AsyncImage(
            model = "https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcSI7ae8qWk3wVAHB_RIm1O607ZfupFVZBEyVvaY72O_VfgFEE_0xBxj6iP0Nnyj4sXL_rZqRKavSpPXODYV0_GGXpOt5stWQWSHpcPaCMy-hideUK-iKpHiqjvKYWQByozvGwvbvDjB_sg&usqp=CAc",
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CREATE ACCOUNT", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Mật khẩu") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Nhập lại mật khẩu") }, modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = {
                    if (password == confirmPassword && password.length >= 6) {
                        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { onRegisterSuccess() }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
            ) {
                Text("ĐĂNG KÝ NGAY")
            }

            Text(
                "Đã có tài khoản? Đăng nhập",
                modifier = Modifier.padding(top = 16.dp).clickable { onBackToLogin() },
                color = Color(0xFF6C5CE7)
            )
        }
    }
}