package com.example.authenication
import androidx.compose.ui.text.style.TextAlign
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import coil.compose.AsyncImage


sealed class Screen(val rout: String) {
    object Signin : Screen("signin_screen")
    object Home : Screen("home_screen")
    object Signup : Screen("signup_screen")
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Khởi tạo Firebase
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5) // Nền xám nhạt toàn app
                ) {
                    Mynavigation()
                }
            }
        }
    }
}


@Composable
fun Mynavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Signin.rout
    ) {
        composable(Screen.Signin.rout) { SignIn(navController) }
        composable(Screen.Home.rout) { HomeScreen(navController) }
        composable(Screen.Signup.rout) { SignUp(navController) }
    }
}

@Composable
fun SignIn(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val imageUrl = "https://tinhocnews.com/wp-content/uploads/2024/05/pizza-vector-3-1024x1024.jpg"

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phần Ảnh Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Lớp phủ tối nhẹ để chữ MY APPLICATION nổi bật
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )
            Text(
                text = "MY APPLICATION",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Spacer(modifier = Modifier.height(-40.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 12.dp,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )
                Text(
                    text = "Đăng nhập để tiếp tục",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Ô Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF6C5CE7)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ô Mật khẩu
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF6C5CE7)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))


                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate(Screen.Home.rout) {
                                            popUpTo(Screen.Signin.rout) { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3436))
                ) {
                    Text("SIGN IN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))


        TextButton(
            onClick = { navController.navigate(Screen.Signup.rout) },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                "Don't have an account? Sign Up",
                color = Color(0xFF636E72),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun SignUp(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("TẠO TÀI KHOẢN", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email mới") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu (6+ ký tự)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("ĐĂNG KÝ")
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Quay lại Đăng nhập")
        }
    }
}


@Composable
fun HomeScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()

    val shipperImageUrl = "https://cdn.tgdd.vn/Files/2020/03/27/1244964/7_800x450.jpg"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1C40F)), // Màu vàng chủ đạo
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Chữ PIZZERIA có bóng đổ nhẹ
        Text(
            text = "PIZZERIA",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFD63031), // Màu đỏ đậm
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Câu slogan
        Text(
            text = "Delivering\nDeliciousness right\nto your door!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436),
            textAlign = TextAlign.Center,
            lineHeight = 35.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Box(modifier = Modifier.size(15.dp, 5.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(5.dp)))
            Spacer(modifier = Modifier.width(5.dp))
            Box(modifier = Modifier.size(15.dp, 5.dp).background(Color.Black, RoundedCornerShape(5.dp)))
            Spacer(modifier = Modifier.width(5.dp))
            Box(modifier = Modifier.size(15.dp, 5.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(5.dp)))
        }

        Spacer(modifier = Modifier.height(40.dp))


        Button(
            onClick = { /* Xử lý đặt hàng */ },
            modifier = Modifier
                .width(200.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD63031)),
            shape = RoundedCornerShape(25.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("START ORDER", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nút SignOut nhỏ hơn
        Button(
            onClick = {
                firebaseAuth.signOut()
                navController.navigate(Screen.Signin.rout) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .width(120.dp)
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB33939)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("SignOut", fontSize = 12.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))
        AsyncImage(
            model = shipperImageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
    }
}