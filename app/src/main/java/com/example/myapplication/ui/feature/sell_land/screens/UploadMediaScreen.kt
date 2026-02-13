package com.example.myapplication.ui.feature.sell_land.screens



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip

import coil.request.ImageRequest



@Composable
fun UploadMediaScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    var images by remember(draft.images) { mutableStateOf(draft.images) }

    val documents = draft.documents

    val saleDeed = documents.saleDeed
    val khata = documents.khataCertificate
    val ec = documents.encumbrance
    val sellerId = documents.sellerIdentity



    var currentDocType by remember { mutableStateOf("") }

    val context = LocalContext.current

    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents()
        ) { uris ->
            if (uris.isNotEmpty()) {
                images = images + uris.map { it.toString() }

                uris.forEach { uri ->
                    viewModel.uploadPhotoFromUri(
                        uri = uri,
                        contentResolver = context.contentResolver,
                        cacheDir = context.cacheDir
                    )
                }
            }
        }


    val documentPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {



                val backendDocType = when (currentDocType) {
                    "sale" -> "sale_deed"
                    "khata" -> "khata_patta"
                    "ec" -> "encumbrance_certificate"
                    "id" -> "seller_identity_proof"
                    else -> "other"
                }

                viewModel.uploadDocumentFromUri(
                    documentType = backendDocType,
                    uri = it,
                    contentResolver = context.contentResolver,
                    cacheDir = context.cacheDir
                )
            }
        }

    Scaffold(
        topBar = {
            SellLandTopBar(
                title = "Upload Media",
                onBack = onBack,
                onSaveDraft = {
                    viewModel.updateImages(images)
                    viewModel.saveDraft(step = 3)
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())   // 👈 ADD HERE
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {


        // ---------------- IMAGES ----------------
            Text(
                text = "Land Photos",
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                item {
                    AddMediaCard(
                        label = "Add Photo",
                        onClick = {
                            photoPickerLauncher.launch("image/*")
                        }
                    )

                }
                items(images) { image ->
                    MediaPreviewCard(
                        imageUri = image,
                        mediaBaseUrl = "http://10.181.27.22:8000",
                        onRemove = { viewModel.removePhotoByUrl(image) }
                    )

                }

            }

            Text(
                text = "${images.size}/10 photos uploaded",
                style = MaterialTheme.typography.bodySmall
            )

            val remaining = (3 - images.size).coerceAtLeast(0)

            Text(
                text = if (remaining == 0) {
                    "Minimum 3 photos requirement completed"
                } else {
                    "Minimum 3 photos required. Add $remaining more."
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (remaining == 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
            )

            HorizontalDivider()



            // ---------------- DOCUMENTS ----------------
            Text(
                text = "Land Documents",
                style = MaterialTheme.typography.titleMedium
            )

            DocumentUploadCard(
                title = "Sale Deed (Registry Paper)",
                uploaded = !saleDeed.isNullOrEmpty()

            ) {
                currentDocType = "sale"
                documentPickerLauncher.launch("application/pdf")

            }

            DocumentUploadCard(
                title = "Khata / Patta Certificate",
                uploaded = !khata.isNullOrEmpty()
            ) {
                currentDocType = "khata"
                documentPickerLauncher.launch("application/pdf")

            }

            DocumentUploadCard(
                title = "Encumbrance Certificate (EC)",
                uploaded = !ec.isNullOrEmpty()

            ) {
                currentDocType = "ec"
                documentPickerLauncher.launch("application/pdf")

            }

            DocumentUploadCard(
                title = "Seller Identity Proof",
                uploaded = !sellerId.isNullOrEmpty()
            ) {
                currentDocType = "id"
                documentPickerLauncher.launch("application/pdf")

            }

            Spacer(modifier = Modifier.weight(1f))


            // 👇 ADD THIS EXACTLY HERE
            val allDocsUploaded =
                        !saleDeed.isNullOrEmpty() &&
                        !khata.isNullOrEmpty() &&
                        !ec.isNullOrEmpty() &&
                        !sellerId.isNullOrEmpty()

            val minPhotosValid = images.size >= 3

            Button(
                onClick = {
                    viewModel.updateImages(images)
                    viewModel.saveDraft(step = 3)
                    onContinue()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = minPhotosValid && allDocsUploaded   // 👈 CHANGE THIS
            ) {
                Text("Continue")
            }
        }
    }
}


@Composable
fun AddMediaCard(
    label: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .size(120.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label)
        }
    }
}


@Composable
fun MediaPreviewCard(
    imageUri: String,
    onRemove: () -> Unit,
    mediaBaseUrl: String // e.g. "http://10.181.27.22:8000"
) {
    val resolvedUrl = remember(imageUri, mediaBaseUrl) {
        resolveMediaUrl(imageUri, mediaBaseUrl)
    }

    Card(
        modifier = Modifier.size(120.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(resolvedUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2))
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

private fun resolveMediaUrl(raw: String, baseUrl: String): String {
    val value = raw.trim()
    if (
        value.startsWith("content://") ||
        value.startsWith("file://") ||
        value.startsWith("http://") ||
        value.startsWith("https://")
    ) return value

    val base = baseUrl.trimEnd('/')
    return if (value.startsWith("/")) "$base$value" else "$base/$value"
}


@Composable
fun DocumentUploadCard(
    title: String,
    uploaded: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        border = BorderStroke(
            1.dp,
            if (uploaded) Color.Green else MaterialTheme.colorScheme.primary
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (uploaded) Icons.Default.Check else Icons.Default.UploadFile,
                contentDescription = null,
                tint = if (uploaded) Color.Green else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(title)
        }
    }
}

