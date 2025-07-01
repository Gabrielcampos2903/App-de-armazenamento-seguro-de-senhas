package com.example.superid.functions

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.example.superid.screens.QRScanActivity
import kotlinx.coroutines.launch

@Composable
fun HomeMenu(
    content: @Composable () -> Unit,
    onAllPasswordsClick: () -> Unit,
    onEditCategoriesClick: () -> Unit,

) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = Modifier
            .fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color.White),
                drawerContainerColor = Color.White,
                drawerContentColor = Color.White,
            )
            {
                DrawerContent(onAllPasswordsClick = onAllPasswordsClick, onEditCategoriesClick = onEditCategoriesClick)
            }
        }
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopBar(onOpenDrawer = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                }
                )
            }
        ) { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    onAllPasswordsClick: () -> Unit,
    onEditCategoriesClick: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(color = Color.White)) {

        Column(
            modifier = Modifier.padding(15.dp)
        ){
            Text(
                text = "MENU",
                fontSize = 35.sp,
                color = Color(0xFF003366),
                fontFamily = FontFamily(Font(R.font.rubikblack)))

            HorizontalDivider()

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Lock, contentDescription = "Lock") },
                label = {
                    Text(
                        text = "All Passwords",
                        fontSize = 17.sp,
                        color = Color(0xFF003366),
                        fontFamily = FontFamily(Font(R.font.rubikregular)))
                },
                selected = false,
                onClick = onAllPasswordsClick
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Edit, contentDescription = "Pen") },
                label = {
                    Text(
                        text = "Edit categories",
                        fontSize = 17.sp,
                        color = Color(0xFF003366),
                        fontFamily = FontFamily(Font(R.font.rubikregular)))
                },
                selected = false,
                onClick = onEditCategoriesClick
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onOpenDrawer: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Super ID",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF003366),
                    fontFamily = FontFamily(Font(R.font.rubikblack)))
            }
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF003366),
                    modifier = Modifier.size(40.dp))
            }
        },
        actions = { Spacer(modifier = Modifier.size(48.dp)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}