package com.example.myapplication.ui.theme.auth

//package com.example.myapplication.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.launch

@Composable
fun OtpVerifyScreen(navController: NavController, mobile: String) {

    val context = LocalContext.current
    //val api = ApiClient.api
    val api =  ApiClient.apiService
    val scope = rememberCoroutineScope()

    var otp by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Enter OTP")

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") }
        )

        Button(
            onClick = {
                if (otp.isEmpty()) {
                    Toast.makeText(context, "Enter OTP", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                scope.launch {
                    loading = true
                    try {
                        val res = api.verifyOtpForForgot(
                            mobile = mobile.trim(),
                            otp = otp.trim()
                        )


                        if (res.isSuccessful && res.body()?.success == true) {
                            Toast.makeText(context, "OTP Verified", Toast.LENGTH_SHORT).show()
                            navController.navigate("reset_password?mobile=$mobile")
                        } else {
                            Toast.makeText(context, res.body()?.message ?: "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }


                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    } finally {
                        loading = false
                    }
                }
            }
        ) {
            Text("Verify OTP")
        }
    }
}
