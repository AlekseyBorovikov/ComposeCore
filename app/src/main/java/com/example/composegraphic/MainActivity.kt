package com.example.composegraphic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composegraphic.ui.theme.ComposeGraphicTheme
import kotlinx.coroutines.launch

private var appBarTitle = mutableStateOf("Graphic")
private sealed class DrawerScreens(
    val route: String,
    val title: String,
    val screenToLoad: @Composable () -> Unit,
){
    object Lab1: DrawerScreens("lab1","1 Базовые алгоритмы", {Lab1Screen()})
    object Lab2: DrawerScreens("lab2","2 Звёздное небо", {Lab2Screen()})
    object Lab3: DrawerScreens("lab3","3 Алгоритм художника", {Lab3Screen()})
}

private val screens = listOf(
    DrawerScreens.Lab1,
    DrawerScreens.Lab2,
    DrawerScreens.Lab3
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeGraphicTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    NavigationDrawer()
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Blue)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ){
        Text("Компьютерная графика", color = Color.White, fontSize = 20.sp)
    }
}

@Composable
private fun Drawer(
    modifier: Modifier = Modifier,
    selectedScreen: DrawerScreens,
    onMenuSelected: ((drawerScreen: DrawerScreens) -> Unit)? = null
){
    Column(modifier.fillMaxSize()) {
        DrawerHeader()
        screens.forEach { screen ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        color = if (screen.route == selectedScreen.route) Color.Blue else Color.White,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .fillMaxWidth()
                    .clickable(onClick = {
                        onMenuSelected?.invoke(screen)
                    })
                    .padding(vertical = 8.dp, horizontal = 16.dp)

            ){
                Text(
                    text = screen.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (screen.route == selectedScreen.route) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun NavigationDrawer() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var currentScreen: DrawerScreens by remember { mutableStateOf(DrawerScreens.Lab3) }
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Drawer(
                selectedScreen = currentScreen,
                onMenuSelected = { drawerScreen: DrawerScreens ->
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                    currentScreen = drawerScreen
                }
            )
        },
        content = {
            currentScreen.screenToLoad()
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { scope.launch { scaffoldState.drawerState.open() } }
                    ) { Icon(Icons.Filled.Menu, "menu") }
                },
                title = { Text(currentScreen.title) }
            )
        },
    )
}