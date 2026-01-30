package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.NavigationGraph

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost

import androidx.navigation.compose.rememberNavController




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme()
            }
        }
    }

@Composable
fun MyApplicationTheme() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // ✅ LOGIN CHECK (PASTE HERE)
    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val email = prefs.getString("EMAIL", null)

    val startDestination = if (email == null) {
        "login"
    } else {
        "home"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        // ✅ ONLY ONE NavHost (VERY IMPORTANT)
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            NavigationGraph(navController)
        }
    }
}
