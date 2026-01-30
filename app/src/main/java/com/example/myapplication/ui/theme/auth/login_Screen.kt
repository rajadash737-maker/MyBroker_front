package com.example.myapplication.ui.theme.auth
//package com.example.myapplication  // <-- change to your actual package name

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.launch

//import com.example.myapplication.network.LoginResponse



@Composable
fun Login(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Retrofit API

    //val api = remember { ApiClient.retrofit.create(ApiService::class.java) }
    val api = ApiClient.apiService



    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Login Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "User Login",
                    fontSize = 28.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Email or Mobile", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                //val passwordVisible = false
                var passwordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.White) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible   // <-- WORKS NOW
                            }
                        ) {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (passwordVisible) "Hide password" else "Show password"

                            Icon(
                                imageVector = icon,
                                contentDescription = description,
                                tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )


                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                        if (username.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            loading = true

                            try {
                                val res = api.login(username.trim(), password.trim())


                                if (res.isSuccessful && res.body()?.status == "success") {

                                    val body = res.body()

                                    // ---------------- SAVE USER DATA ----------------
                                    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                                    prefs.edit()
                                        .putString("USER_ID", body?.user?.id.toString())
                                        .putString("USER_EMAIL", body?.user?.email ?: "")
                                        .putString("USER_MOBILE", body?.user?.mobile ?: "")
                                        .putString("USER_NAME", body?.user?.fullname ?: "")
                                        .apply()

                                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()

                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }


                                else {

                                    Toast.makeText(context, res.body()?.message ?: "Login Failed", Toast.LENGTH_SHORT).show()
                                }


                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                loading = false
                            }
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (loading) "Please wait..." else "Login", color = Color.White)
                }

                TextButton(
                    onClick = { navController.navigate("forgot") }
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { navController.navigate("signup") }) {
                    Text("Create Account", color = Color.White)
                }
            }
        }
    }
}


