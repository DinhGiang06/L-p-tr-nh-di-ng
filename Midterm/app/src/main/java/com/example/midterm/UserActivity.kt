package com.example.midterm
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UserScreen(onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(onLogout: () -> Unit) {
    var productList by remember { mutableStateOf(listOf<Product>()) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("products").addSnapshotListener { snapshots, _ ->
            if (snapshots != null) {
                productList = snapshots.map { it.toObject(Product::class.java) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Khám phá Sản Phẩm", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFF6C5CE7))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF4F5FA))) {
            items(productList) { product ->
                UserProductCard(product)
            }
        }
    }
}

@Composable
fun UserProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val bitmap = remember(product.fileUrl) {
                if (product.fileUrl.isNotEmpty()) {
                    val bytes = Base64.decode(product.fileUrl, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } else null
            }

            if (bitmap != null) {
                Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            } else {
                Box(Modifier.size(100.dp).background(Color.LightGray))
            }

            Column(Modifier.padding(start = 16.dp)) {
                Text(product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(product.category, color = Color.Gray, fontSize = 14.sp)
                Text("${product.price}đ", color = Color(0xFF6C5CE7), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}