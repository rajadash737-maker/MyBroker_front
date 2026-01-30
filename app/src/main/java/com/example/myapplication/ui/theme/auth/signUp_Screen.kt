package com.example.myapplication.ui.theme.auth
//package com.example.myapplication.ui

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
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.network.ApiClient
//import com.google.firebase.appdistribution.gradle.ApiService
import kotlinx.coroutines.launch

@Composable
fun SignupScreenWithOtpUI(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // User fields
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // States
    var otpSent by remember { mutableStateOf(false) }
    var otpVerified by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    //val api = remember { ApiClient.retrofit.create(ApiService::class.java) }
    val api = ApiClient.apiService


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
                        value = mobile,
                        onValueChange = { mobile = it.filter { it.isDigit() } },
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
                            if (mobile.length != 10) {
                                Toast.makeText(context, "Enter valid 10-digit mobile", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                    /*
                            scope.launch {
                                loading = true
                                try {
                                    val res = api.sendOtp(mobile)
                                    if (res.isSuccessful && res.body()?.success == true) {
                                        otpSent = true
                                        Toast.makeText(context, "OTP Sent!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed: ${res.body()?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    loading = false
                                }
                            }

                     */
                            scope.launch {
                                loading = true
                                try {
                                    val res = api.sendOtp(mobile)
                                    if (res.isSuccessful && res.body()?.status == "success") {
                                        otpSent = true
                                        Toast.makeText(context, "OTP Sent!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed: ${res.body()?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    loading = false
                                }
                            }

                        },
                        enabled = !loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.4f)
                        )
                    ) {
                        Text("Send OTP", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // OTP + Verify Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it.filter { it.isDigit() } },
                        enabled = otpSent && !otpVerified,
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
                            if (otp.length < 4) {
                                Toast.makeText(context, "Enter valid OTP", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            scope.launch {
                                loading = true
                                try {
                                    val res = api.verifyOtp(mobile, otp)
                                    if (res.isSuccessful && res.body()?.success == true) {
                                        otpVerified = true
                                        Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Invalid OTP!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        enabled = otpSent && !otpVerified && !loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.4f))
                    ) {
                        Text(if (otpVerified) "Verified" else "Verify", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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

                        if (fullname.isEmpty() || email.isEmpty() || mobile.isEmpty() ||
                            otp.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                        ) {
                            Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!otpVerified) {
                            Toast.makeText(context, "Verify OTP first!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            loading = true
                            try {
                                val res = api.signup(
                                    fullname = fullname,
                                    email = email,
                                    mobile = mobile,
                                    otp = otp,
                                    password = password,
                                    confirm = confirmPassword
                                )
                                if (res.isSuccessful && res.body()?.success == true) {
                                    Toast.makeText(context, "Signup Successful!", Toast.LENGTH_LONG).show()
                                    navController.navigate("login")
                                } else {
                                    Toast.makeText(context, res.body()?.message ?: "Signup Failed", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
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
