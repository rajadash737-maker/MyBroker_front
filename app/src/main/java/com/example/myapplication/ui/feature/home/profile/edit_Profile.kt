package com.example.myapplication.ui.feature.home.profile


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.UpdateProfileRequest
import com.example.myapplication.utils.DateUtils
import kotlinx.coroutines.launch

import android.app.DatePickerDialog
import java.util.Calendar






//import com.example.myapplication.ui.theme.home.profile.UploadImageToServer









@Composable
fun EditProfileScreen(
    onSaveClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    val scroll = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var profileImageName by remember { mutableStateOf("") }
    var showDobPicker by remember { mutableStateOf(false) }



    // ----------------- BACKEND VARIABLES -----------------
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val api = remember(context) {
        ApiClient.init(context.applicationContext)
        ApiClient.apiService
    }



    // ----------------- AUTO LOAD PROFILE FROM BACKEND -----------------
    LaunchedEffect(Unit) {
        try {
            val res = api.getMyProfile()
            val err = res.errorBody()?.string()

            if (res.isSuccessful) {
                val p = res.body() ?: return@LaunchedEffect
                fullName = p.fullname ?: ""
                userName = p.username ?: ""
                email = p.email ?: ""
                mobile = p.mobile ?: ""
                address = p.address ?: ""
                dob = DateUtils.apiToUi(p.dateOfBirth)
                gender = (p.gender ?: "male").replaceFirstChar { it.uppercase() }
            } else {
                Toast.makeText(context, "Load failed: ${res.code()} ${err ?: ""}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Load error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .background(Color(0xFFF2F2F8))
            .padding(16.dp)
    ) {

        ProfileImageSection { uploadedName ->
            profileImageName = uploadedName   // ✅ STORE IT
        }


        Spacer(modifier = Modifier.height(20.dp))

        ProfileInputField("Full Name", fullName) { fullName = it }
        ProfileInputField("Username", userName) { userName = it }
        ProfileInputField("Email", email) { email = it }
        ProfileInputField("Mobile Number", mobile) { mobile = it }
        ProfileInputField("Address", address) { address = it }
        DobField(
            value = dob,
            onClick = {
                showDobPicker = true
            }
        )


        Spacer(modifier = Modifier.height(12.dp))

        Text("Gender", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            GenderOption("Male", gender) { gender = it }
            Spacer(modifier = Modifier.width(10.dp))
            GenderOption("Female", gender) { gender = it }
            Spacer(modifier = Modifier.width(10.dp))
            GenderOption("Other", gender) { gender = it }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- DOB CALENDAR PICKER ----------------
        if (showDobPicker) {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    dob = "%02d/%02d/%04d".format(
                        dayOfMonth,
                        month + 1,
                        year
                    )
                    showDobPicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis() // ❌ future DOB
            }.show()
        }



        // ----------------- SAVE BUTTON (BACKEND UPDATE) -----------------
        Button(
            onClick = {
                scope.launch {
                    try {
                        val req = UpdateProfileRequest(
                            fullname = fullName,
                            username = userName,
                            email = email,
                            mobile = mobile,
                            address = address,
                            dateOfBirth = dob, // DD/MM/YYYY or YYYY-MM-DD
                            gender = gender.lowercase()
                        )
                        val res = api.updateMyProfile(req)
                        val err = res.errorBody()?.string()

                        if (res.isSuccessful) {
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Update failed: ${res.code()} ${err ?: ""}", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Update error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A5AE0)
            )
        ) {
            Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}



// ============================================================================
// PROFILE IMAGE
// ============================================================================


@Composable
fun ProfileImageSection(
    onImageUploaded: (String) -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var serverImage by remember { mutableStateOf<String?>(null) }

    // Load from backend shared preferences
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        serverImage = prefs.getString("PROFILE_IMAGE", null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri

        if (uri != null) {
            scope.launch {
                val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)

                UploadImageToServer.uploadProfileImage(
                    context = context,
                    uri = uri
                ) { imageUrl ->
                    serverImage = imageUrl

                    prefs.edit()
                        .putString("PROFILE_IMAGE", imageUrl)
                        .apply()

                    onImageUploaded(imageUrl)
                }
            }
        }

    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        // --- DISPLAY FROM BACKEND IF AVAILABLE ---
        val imageModel = when {
            imageUri != null -> imageUri
            !serverImage.isNullOrBlank() -> ApiClient.absoluteUrl(serverImage) ?: serverImage
            else -> null
        }

        if (imageModel != null) {
            Image(
                painter = rememberAsyncImagePainter(imageModel),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
        }


        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { launcher.launch("image/*") }) {
            Text("Change Photo")
        }
    }
}


// ============================================================================
// INPUT FIELD
// ============================================================================
@Composable
fun ProfileInputField(label: String, value: String, onValueChange: (String) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A4A4A)
            )

            Spacer(modifier = Modifier.height(8.dp))


            TextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                    .padding(horizontal = 6.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF6A5AE0),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}


//
//================CHALANDER========================
//


@Composable
fun DobField(
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "Date of Birth (DD/MM/YYYY)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A4A4A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (value.isBlank()) "Select date" else value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (value.isBlank()) Color.Gray else Color.Black
            )
        }
    }
}


// ============================================================================
// GENDER OPTION
// ============================================================================
@Composable
fun GenderOption(
    option: String,
    selectedGender: String,
    onSelect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onSelect(option) }
            .padding(4.dp)
    ) {
        RadioButton(
            selected = selectedGender == option,
            onClick = { onSelect(option) }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(option)
    }


 }

//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun EditProfileScreenPreview() {
//    EditProfileScreen()
//}
