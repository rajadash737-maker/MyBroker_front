package com.example.myapplication.ui.feature.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.ResetPasswordRequest
import kotlinx.coroutines.launch
import com.example.myapplication.utils.normalizeMobile


@Composable
fun ForgotPasswordScreen(navController: NavController) {

    val context = LocalContext.current
    val activity = context as? Activity

    ApiClient.init(context.applicationContext)
    val api = ApiClient.apiService

    val scope = rememberCoroutineScope()
    val otpViewModel: FirebaseOtpViewModel = viewModel()
    val otpState by otpViewModel.uiState.collectAsState()

    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    var step by remember { mutableStateOf(1) } // 1 = enter mobile, 2 = enter otp, 3 = reset password

    LaunchedEffect(otpState.message) {
        val msg = otpState.message ?: return@LaunchedEffect
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        otpViewModel.clearMessage()
    }
    LaunchedEffect(otpState.otpSent) {
        if (otpState.otpSent) step = 2
    }
    LaunchedEffect(otpState.otpVerified) {
        if (otpState.otpVerified) step = 3
    }


    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Forgot Password", fontSize = 26.sp, color = Color.White)
                Spacer(modifier = Modifier.height(20.dp))

                // ───────────────────────────
                // STEP 1 — ENTER MOBILE
                // ───────────────────────────
                if (step == 1) {

                    OutlinedTextField(
                        value = otpState.mobile,
                        onValueChange = { otpViewModel.updateMobile(it.filter { ch -> ch.isDigit() }) },
                        label = { Text("Mobile Number", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (activity == null) {
                                Toast.makeText(context, "Activity not available", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            otpViewModel.sendOtp(activity, "forgot_password")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !otpState.loading
                    ) {
                        Text(if (otpState.loading) "Please wait..." else "Send OTP", color = Color.White)
                    }
                }


                // ───────────────────────────
                // STEP 2 — VERIFY OTP
                // ───────────────────────────
                if (step == 2) {

                    OutlinedTextField(
                        value = otpState.otp,
                        onValueChange = { otpViewModel.updateOtp(it.filter { ch -> ch.isDigit() }) },
                        label = { Text("Enter OTP", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            otpViewModel.verifyOtp("forgot_password")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !otpState.loading
                    ) {
                        Text(if (otpState.loading) "Please wait..." else "Verify OTP", color = Color.White)
                    }

                    if (otpState.otpSent && !otpState.otpVerified) {
                        TextButton(
                            onClick = {
                                if (activity == null) {
                                    Toast.makeText(context, "Activity not available", Toast.LENGTH_SHORT).show()
                                    return@TextButton
                                }
                                otpViewModel.resendOtp(activity, "forgot_password")
                            },
                            enabled = otpState.canResend && !otpState.loading
                        ) {
                            val label = if (otpState.canResend) {
                                "Resend OTP"
                            } else {
                                "Resend in ${otpState.resendSeconds}s"
                            }
                            Text(label, color = Color.White)
                        }
                    }

                    TextButton(onClick = { step = 1 }) {
                        Text("Change Mobile", color = Color.White)
                    }
                }

                // ───────────────────────────
                // STEP 3 — RESET PASSWORD
                // ───────────────────────────
                if (step == 3) {

                    var showPass1 by remember { mutableStateOf(false) }
                    var showPass2 by remember { mutableStateOf(false) }

                    // NEW PASSWORD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("New Password", color = Color.White) },
                        visualTransformation = if (showPass1) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass1 = !showPass1 }) {
                                Icon(
                                    imageVector = if (showPass1) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
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

                    Spacer(Modifier.height(12.dp))

                    // CONFIRM PASSWORD
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Confirm Password", color = Color.White) },
                        visualTransformation = if (showPass2) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass2 = !showPass2 }) {
                                Icon(
                                    imageVector = if (showPass2) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
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

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (password != confirm) {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            scope.launch {
                                try {
                                    val res = api.resetPassword(
                                        ResetPasswordRequest(
                                            mobile = normalizeMobile(otpState.mobile),
                                            password = password,
                                            confirm = confirm
                                        )
                                    )
                                    val status = res.body()?.status

                                    if (res.isSuccessful && status == "Password reset successful") {
                                        Toast.makeText(context, "Password Reset Successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login")
                                    } else {
                                        Toast.makeText(context, status ?: "Failed", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, e.message ?: "Network error", Toast.LENGTH_SHORT).show()
                                }
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset Password", color = Color.White)
                    }
                }
            }
        }
    }
}


//
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewForgotPasswordUI() {
//    MaterialTheme { ForgotPasswordScreen() }
//}
