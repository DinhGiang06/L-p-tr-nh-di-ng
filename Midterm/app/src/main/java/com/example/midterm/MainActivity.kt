package com.example.midterm
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AdminScreen(
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        // Chuyển hướng về LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdminScreen(onLogout: () -> Unit) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var editingProductId by remember { mutableStateOf<String?>(null) }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var productList by remember { mutableStateOf(listOf<Product>()) }
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            db.collection("products").addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    productList = snapshots.map { doc ->
                        doc.toObject(Product::class.java).apply { id = doc.id }
                    }
                }
            }
        }

        val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ADMIN PANEL", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6C5CE7))
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FD))) {

                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên sản phẩm") }, modifier = Modifier.fillMaxWidth())
                        Row(Modifier.padding(vertical = 8.dp)) {
                            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Loại") }, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = price, onValueChange = { price = it },
                                label = { Text("Giá") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { pickImageLauncher.launch("image/*") }) {
                                Text("📷 Chọn ảnh")
                            }
                            Text(
                                text = if (selectedImageUri != null) "Đã chọn ảnh" else "Chưa có ảnh",
                                modifier = Modifier.padding(start = 8.dp),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Button(
                            onClick = {
                                if (name.isEmpty() || category.isEmpty() || price.isEmpty()) {
                                    Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val base64 = selectedImageUri?.let { encodeImageToBase64(it) }
                                saveProduct(name, category, price, base64, editingProductId) {
                                    name = ""; category = ""; price = ""; editingProductId = null; selectedImageUri = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
                        ) {
                            Text(if (editingProductId == null) "LƯU SẢN PHẨM" else "CẬP NHẬT SẢN PHẨM")
                        }
                    }
                }


                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                    items(productList) { product ->
                        AdminProductItem(
                            product = product,
                            onEdit = {
                                name = product.name
                                category = product.category
                                price = product.price
                                editingProductId = product.id
                            },
                            onDelete = { db.collection("products").document(product.id).delete() }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AdminProductItem(product: Product, onEdit: () -> Unit, onDelete: () -> Unit) {
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                ProductImage(product.fileUrl, Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)))
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(product.name, fontWeight = FontWeight.Bold)
                    Text("Loại: ${product.category}", fontSize = 12.sp)
                    Text("${product.price} VNĐ", color = Color(0xFF6C5CE7), fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Blue) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
            }
        }
    }

    @Composable
    fun ProductImage(base64: String?, modifier: Modifier) {
        val bitmap = remember(base64) {
            if (!base64.isNullOrEmpty()) {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } else null
        }
        if (bitmap != null) {
            Image(bitmap = bitmap, contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
        } else {
            Box(modifier = modifier.background(Color.LightGray), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        }
    }

    private fun encodeImageToBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) { null }
    }

    private fun saveProduct(name: String, category: String, price: String, base64: String?, id: String?, onComplete: () -> Unit) {
        val data = mutableMapOf<String, Any>(
            "name" to name,
            "category" to category,
            "price" to price
        )

        if (base64 != null) data["fileUrl"] = base64

        val task = if (id == null) {
            db.collection("products").add(data)
        } else {
            db.collection("products").document(id).update(data)
        }

        task.addOnSuccessListener { onComplete() }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}