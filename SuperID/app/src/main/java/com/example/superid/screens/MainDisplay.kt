package com.example.superid.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.example.superid.functions.AddPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val activity: Class<out Activity>
)

class MainDisplayActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            val activity = this@MainDisplayActivity


            val items = listOf(
                NavigationItem(
                    title = "home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    activity = MainDisplayActivity::class.java
                ),
                NavigationItem(
                    title = "qrcode",
                    selectedIcon = Icons.Filled.QrCodeScanner,
                    unselectedIcon = Icons.Outlined.QrCodeScanner,
                    activity = QRScanActivity::class.java
                ),
                NavigationItem(
                    title = "verification",
                    selectedIcon = Icons.Filled.AccountCircle,
                    unselectedIcon = Icons.Outlined.AccountCircle,
                    activity = VerifyEmailActivity::class.java

                )
            )

            var selectedItemIndex by rememberSaveable {
                mutableStateOf(0)
            }

            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            var isVerified by remember { mutableStateOf(user?.isEmailVerified ?: false) }

            LaunchedEffect(Unit) {
                user?.reload()?.addOnCompleteListener {
                    isVerified = auth.currentUser?.isEmailVerified ?: false
                }
            }

            Scaffold(
                containerColor = Color.White,
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.8.dp)
                                .background(Color.Black)
                        )
                    NavigationBar(
                        containerColor = Color.White
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedItemIndex == index,
                                onClick = {
                                    when (item.title) {
                                        "verification" -> {
                                            if (!isVerified) {
                                                selectedItemIndex = index
                                                val intent = Intent(activity, item.activity)
                                                activity.startActivity(intent)
                                                activity.finish()
                                            } else {
                                                Toast.makeText(activity, "Your email is already verified.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        "qrcode" -> {
                                            if (isVerified) {
                                                selectedItemIndex = index
                                                val intent = Intent(activity, item.activity)
                                                activity.startActivity(intent)
                                                activity.finish()
                                            } else {
                                                Toast.makeText(activity, "Please verify your email first.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        else -> {
                                            selectedItemIndex = index
                                            val intent = Intent(activity, item.activity)
                                            activity.startActivity(intent)
                                            activity.finish()
                                        }
                                    }
                                },


                                icon = {

                                    val iconColor = when (item.title) {
                                        "verification" -> if (isVerified) Color(0xFF4CAF50) else Color.Gray
                                        else -> Color(0xFF003366)
                                    }

                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,
                                        contentDescription = item.title,
                                        tint = iconColor,
                                        modifier = Modifier
                                            .size(32.dp)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Black.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                    }
                }
            ) { paddingValues ->
                MainApp(this@MainDisplayActivity, paddingValues)
            }
        }
    }
}




@Composable
fun MainApp(activity: Activity, paddingValues: PaddingValues){

    var searchText by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddPasswordDialog by remember { mutableStateOf(false) }

    val customFontBlack = FontFamily(Font(R.font.rubikblack))

    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid

    com.example.superid.functions.HomeMenu(
        onAllPasswordsClick = {
            val intent = Intent(activity, AllPasswordsActivity::class.java)
            activity.startActivity(intent)
        },
        onEditCategoriesClick = {
            val intent = Intent(activity, EditCategoriesActivity::class.java)
            activity.startActivity(intent)
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier.fillMaxSize()
                        .fillMaxSize()
                        .background(color = Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                    Spacer(modifier = Modifier.height(30.dp))

                    com.example.superid.functions.Searchbar(
                        searchInfo = searchText,
                        onTextChange = { searchText = it },
                        onSearchClick = { searchQuery = searchText }
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    if (searchQuery.isNotBlank()) {
                        com.example.superid.functions.InfoCard(searchQuery, uid.toString())
                    }

                }

                ExtendedFloatingActionButton(
                    onClick = { showAddPasswordDialog = true },
                    containerColor = Color(0xFF003366),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Plus sign, Add password",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Password",
                            color = Color.White,
                            fontFamily = customFontBlack,
                            fontSize = 18.sp,
                        )
                    }
                }

                if (showAddPasswordDialog) {
                    if (uid != null) {
                        AddPassword(
                            onDismiss = { showAddPasswordDialog = false },
                            uid
                        )
                    }
                }
            }
        }
    )

}
