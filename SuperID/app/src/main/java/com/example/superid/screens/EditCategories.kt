package com.example.superid.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.example.superid.functions.DropdownItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.firestore.FieldValue

class EditCategoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EditCategories(this)
        }
    }
}

@Composable
fun EditCategories(activity: Activity) {
    var selectedCategory by remember { mutableStateOf<DropdownItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val customFontBlack = FontFamily(
        Font(R.font.rubikblack)
    )

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        IconButton(
            onClick = {
                val intent = Intent(activity, MainDisplayActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 30.dp, start = 10.dp)


        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF003366),
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Delete\nCategory",
                fontSize = 50.sp,
                fontFamily = customFontBlack,
                fontWeight = FontWeight.Black,
                color = Color(0xFF003366),
                textAlign = TextAlign.Center
            )

            Spacer( modifier = Modifier.height(10.dp))

            DropDownMenu(
                selectedItem = selectedCategory,
                onItemSelected = { selectedCategory = it },
                activity = activity
            )

            Spacer( modifier = Modifier.height(40.dp))

            Text(
                text = "Add new Category",
                fontSize = 50.sp,
                fontFamily = customFontBlack,
                fontWeight = FontWeight.Black,
                color = Color(0xFF003366),
                textAlign = TextAlign.Center
            )

            Spacer( modifier = Modifier.height(10.dp))

            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF003366),
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new category"
                )
            }
        }
    }



    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = "New Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF003366)
                )
            },
            text = {
                com.example.superid.functions.TextFields(
                    info = newCategoryName,
                    onValueChange = { newCategoryName = it},
                    title = "Category name: "
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            val auth = FirebaseAuth.getInstance()
                            val db = Firebase.firestore
                            val uid = auth.currentUser?.uid


                            db.collection("user")
                                .document(uid.toString())
                                .collection("categorys")
                                .document("allcategorys")
                                .update("category", FieldValue.arrayUnion(newCategoryName))
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Category added", Toast.LENGTH_SHORT).show()
                                    showAddDialog = false
                                    newCategoryName = ""
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error on saving category", Toast.LENGTH_SHORT).show()
                                }



                        }else{
                            Toast.makeText(context, "Missing info", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        "Save",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        newCategoryName = ""
                    }
                ) {
                    Text("Cancel", color = Color(0xFF003366))
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    selectedItem: DropdownItem?,
    onItemSelected: (DropdownItem?) -> Unit,
    activity: Activity
) {
    var expanded by remember { mutableStateOf(false) }
    var itemList by remember { mutableStateOf<List<DropdownItem>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf<DropdownItem?>(null) }

    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
    val uid = auth.currentUser?.uid
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        val document = db.collection("user")
            .document(uid.toString())
            .collection("categorys")
            .document("allcategorys")
            .get()
            .await()

        val categories = document.get("category") as? List<String>
        categories?.let { list ->
            itemList = list.map { DropdownItem(it) }
        }
    }

    fun deleteCategory(item: DropdownItem) {

        0
        val protectedCategories = listOf("Site", "App", "Num pad")


        if (protectedCategories.contains(item.title)) {
            Toast.makeText(activity, "This category cannot be deleted", Toast.LENGTH_SHORT).show()
            return
        }

        val newList = itemList.filterNot { it.title == item.title }
        val categoryNames = newList.map { it.title }

        db.collection("user")
            .document(uid.toString())
            .collection("categorys")
            .document("allcategorys")
            .update("category", categoryNames)
            .addOnSuccessListener {
                itemList = newList
                onItemSelected(null)
                Toast.makeText(context, "Category Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error on deleting category", Toast.LENGTH_LONG).show()
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(10.dp)),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    value = selectedItem?.title ?: "",
                    onValueChange = { },
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = "Ícone de edição")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    placeholder = {
                        Text(
                            text = "Choose a category",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.rubikregular)),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF003366),
                        unfocusedTextColor = Color(0xFF003366),
                        focusedBorderColor = Color(0xFF003366),
                        unfocusedBorderColor = Color(0xFF003366),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                itemList.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.rubikregular))
                                )
                            }
                        },
                        onClick = {
                            expanded = false
                            showDeleteDialog = item
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }


    if (showDeleteDialog != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = Color.White,
            title = {
                Text(
                    text = "Delete Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF003366)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this category \"${showDeleteDialog?.title}\"?",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteDialog?.let {
                            deleteCategory(it)
                        }
                        showDeleteDialog = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("Cancel", color = Color(0xFF003366))
                }
            }
        )
    }
}

