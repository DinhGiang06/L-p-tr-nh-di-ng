package com.example.firestore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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



class CourseDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirestoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val courseList = remember { mutableStateListOf<Course>() }

                    // Lắng nghe dữ liệu thời gian thực từ Firebase
                    LaunchedEffect(Unit) {
                        val db = FirebaseFirestore.getInstance()
                        db.collection("Courses").addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.w("FirestoreError", "Lắng nghe thất bại.", error)
                                return@addSnapshotListener
                            }

                            if (snapshot != null) {
                                courseList.clear()
                                for (d in snapshot.documents) {
                                    val c = d.toObject(Course::class.java)
                                    if (c != null) {
                                        c.courseID = d.id // Đảm bảo gán đúng ID từ Document
                                        courseList.add(c)
                                    }
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                        Text(
                            text = "Danh sách & Chỉnh sửa",
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        )

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            // SỬA LỖI 1: Dùng hàm `items` và truyền `key` để UI cập nhật chính xác item bị xóa
                            items(
                                items = courseList,
                                key = { course -> course.courseID ?: course.hashCode() }
                            ) { item ->
                                CourseItemCard(item, context)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseItemCard(course: Course, context: Context) {
    // SỬA LỖI 2: Thêm courseID vào remember để State được reset đúng với dữ liệu mới
    val name = remember(course.courseID) { mutableStateOf(course.courseName ?: "") }
    val duration = remember(course.courseID) { mutableStateOf(course.courseDuration ?: "") }
    val description = remember(course.courseID) { mutableStateOf(course.courseDescription ?: "") }

    Card(
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Tên khóa học") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = duration.value,
                onValueChange = { duration.value = it },
                label = { Text("Thời gian") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // NÚT CẬP NHẬT
                Button(
                    onClick = {
                        updateData(course.courseID, name.value, duration.value, description.value, context)
                    },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text("Cập nhật")
                }

                // NÚT XÓA
                Button(
                    onClick = {
                        deleteData(course.courseID, context)
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Xóa", color = Color.White)
                }
            }
        }
    }
}

// Hàm Cập nhật
private fun updateData(id: String?, name: String, dur: String, desc: String, context: Context) {
    if (id.isNullOrEmpty()) {
        Toast.makeText(context, "Lỗi: Không tìm thấy ID khóa học!", Toast.LENGTH_SHORT).show()
        return
    }

    val db = FirebaseFirestore.getInstance()
    // Khuyên dùng mapOf để update các trường cụ thể thay vì dùng .set() đè lên toàn bộ Document
    val updatedData = mapOf(
        "courseName" to name,
        "courseDuration" to dur,
        "courseDescription" to desc
    )

    db.collection("Courses").document(id).update(updatedData)
        .addOnSuccessListener {
            Toast.makeText(context, "Cập nhật Database thành công!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Lỗi cập nhật: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("FirestoreError", "Lỗi Cập nhật: ", e)
        }
}

// Hàm Xóa
private fun deleteData(id: String?, context: Context) {
    if (id.isNullOrEmpty()) {
        Toast.makeText(context, "Lỗi: Không tìm thấy ID khóa học để xóa!", Toast.LENGTH_SHORT).show()
        return
    }

    val db = FirebaseFirestore.getInstance()
    db.collection("Courses").document(id).delete()
        .addOnSuccessListener {
            Toast.makeText(context, "Đã xóa vĩnh viễn khỏi Database!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            // HIỂN THỊ LỖI CỤ THỂ ĐỂ BIẾT TẠI SAO DATABASE KHÔNG XÓA ĐƯỢC
            Toast.makeText(context, "Xóa Database thất bại: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("FirestoreError", "Lỗi Xóa: ", e)
        }
}