package com.example.myapplication.ui.feature.auth
//package com.example.myapplication.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.SignupRequest
//import com.google.firebase.appdistribution.gradle.ApiService
import com.example.myapplication.utils.normalizeMobile
import kotlinx.coroutines.launch

@Composable
fun SignupScreenWithOtpUI(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = context as? Activity

    // User fields
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val otpViewModel: FirebaseOtpViewModel = viewModel()
    val otpState by otpViewModel.uiState.collectAsState()

    var signupLoading by remember { mutableStateOf(false) }

    val api = remember(context) {
        ApiClient.init(context.applicationContext)
        ApiClient.apiService
    }

    LaunchedEffect(otpState.message) {
        val msg = otpState.message ?: return@LaunchedEffect
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        otpViewModel.clearMessage()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Background image
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
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            )
        ) {

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Create Account", fontSize = 26.sp, color = Color.White)

                Spacer(modifier = Modifier.height(20.dp))

                // Full Name
                OutlinedTextField(
                    value = fullname,
                    onValueChange = { fullname = it },
                    label = { Text("Full Name", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mobile + Send OTP
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = otpState.mobile,
                        onValueChange = { otpViewModel.updateMobile(it.filter { ch -> ch.isDigit() }) },
                        label = { Text("Mobile No", color = Color.White) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (activity == null) {
                                Toast.makeText(context, "Activity not available", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            otpViewModel.sendOtp(activity, "signup")
                        },
                        enabled = !otpState.loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(if (otpState.loading) "Please wait..." else "Send OTP", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // OTP + Verify Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = otpState.otp,
                        onValueChange = { otpViewModel.updateOtp(it.filter { ch -> ch.isDigit() }) },
                        enabled = otpState.otpSent && !otpState.otpVerified,
                        label = { Text("Enter OTP", color = Color.White) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            disabledTextColor = Color.White.copy(alpha = 0.4f),
                            disabledBorderColor = Color.White.copy(alpha = 0.4f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            otpViewModel.verifyOtp("signup")
                        },
                        enabled = otpState.otpSent && !otpState.otpVerified && !otpState.loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.4f))
                    ) {
                        val label = when {
                            otpState.loading -> "Please wait..."
                            otpState.otpVerified -> "Verified"
                            else -> "Verify"
                        }
                        Text(label, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (otpState.otpSent && !otpState.otpVerified) {
                    TextButton(
                        onClick = {
                            if (activity == null) {
                                Toast.makeText(context, "Activity not available", Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }
                            otpViewModel.resendOtp(activity, "signup")
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

                // Password

                var passwordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    singleLine = true
                )



                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password


                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password", color = Color.White) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    singleLine = true
                )


                Spacer(modifier = Modifier.height(20.dp))

                // SIGNUP BUTTON
                Button(
                    onClick = {

                        if (fullname.isEmpty() || email.isEmpty() || otpState.mobile.isEmpty() ||
                            otpState.otp.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                        ) {
                            Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!otpState.otpVerified) {
                            Toast.makeText(context, "Verify OTP first!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            signupLoading = true
                            try {
                                val res = api.signup(
                                    SignupRequest(
                                        fullname = fullname.trim(),
                                        email = email.trim(),
                                        mobile = normalizeMobile(otpState.mobile),
                                        password = password,
                                        confirm = confirmPassword
                                    )
                                )

                                val okBody = res.body()
                                val errBody = res.errorBody()?.string()

                                android.util.Log.e(
                                    "SIGNUP_DEBUG",
                                    "code=${res.code()} success=${res.isSuccessful} body=$okBody err=$errBody"
                                )

                                if (res.isSuccessful && okBody?.status == "Signup successful") {
                                    Toast.makeText(context, "Signup Successful!", Toast.LENGTH_LONG).show()
                                    navController.navigate("login")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Signup failed: code=${res.code()} msg=${okBody?.status ?: errBody ?: "unknown"}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("SIGNUP_DEBUG", "exception", e)
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                signupLoading = false
                            }
                        }


                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !signupLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.4f))
                ) {
                    Text("Sign Up", fontSize = 18.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Already have an account? Login", color = Color.White)
                }
            }
        }
    }
}



//@Preview(showSystemUi = true)
//@Composable
//fun SignupScreenWithOtpUIPreview() {
//    SignupScreenWithOtpUI()
//}
