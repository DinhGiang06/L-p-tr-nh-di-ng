package com.example.firestore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firestore.ui.theme.FirestoreTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirestoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirebaseUI(LocalContext.current)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseUI(context: Context) {
    val courseName = remember { mutableStateOf("") }
    val courseDuration = remember { mutableStateOf("") }
    val courseDescription = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thêm Khóa Học Mới",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = courseName.value,
            onValueChange = { courseName.value = it },
            label = { Text("Tên khóa học") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = courseDuration.value,
            onValueChange = { courseDuration.value = it },
            label = { Text("Thời gian") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = courseDescription.value,
            onValueChange = { courseDescription.value = it },
            label = { Text("Mô tả") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Nút Thêm
        Button(
            onClick = {
                if (TextUtils.isEmpty(courseName.value) ||
                    TextUtils.isEmpty(courseDuration.value) ||
                    TextUtils.isEmpty(courseDescription.value)) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else {
                    addDataToFirebase(courseName.value, courseDuration.value, courseDescription.value, context)
                    courseName.value = ""
                    courseDuration.value = ""
                    courseDescription.value = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Lưu Khóa Học", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nút Xem Danh Sách
        Button(
            onClick = {
                context.startActivity(Intent(context, CourseDetailsActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text(text = "Xem Danh Sách", modifier = Modifier.padding(8.dp))
        }
    }
}

// Hàm lưu dữ liệu có sinh ID
private fun addDataToFirebase(name: String, duration: String, description: String, context: Context) {
    val db = FirebaseFirestore.getInstance()

    // Tạo 1 document rỗng để lấy ID ngẫu nhiên
    val newCourseRef = db.collection("Courses").document()
    val generatedId = newCourseRef.id

    // Gắn ID vào Object Course
    val course = Course(name, duration, description, generatedId)

    // Đẩy dữ liệu vào Document vừa tạo
    newCourseRef.set(course)
        .addOnSuccessListener {
            Toast.makeText(context, "Thêm khóa học thành công!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Lỗi khi lưu: " + e.message, Toast.LENGTH_LONG).show()
        }
}