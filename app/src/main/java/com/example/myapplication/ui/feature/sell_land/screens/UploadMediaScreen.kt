package com.example.myapplication.ui.feature.sell_land.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.ui.feature.sell_land.components.SellLandTopBar
import com.example.myapplication.ui.feature.sell_land.viewmodel.SellLandViewModel

private data class RequiredDocument(
    val title: String,
    val backendType: String
)

private fun requiredDocumentsForSellerType(sellerType: String?): List<RequiredDocument> {
    return when (sellerType?.trim()?.lowercase()) {
        "multi_owner" -> listOf(
            RequiredDocument("Sale Deed (Registry Paper)", "sale_deed"),
            RequiredDocument("All Co-owner Aadhaar", "all_co_owner_aadhaar"),
            RequiredDocument("Joint Affidavit", "joint_affidavit"),
            RequiredDocument("Encumbrance Certificate (EC)", "encumbrance_certificate"),
            RequiredDocument("Latest Tax Receipt", "latest_tax_receipt")
        )

        "inherited_land" -> listOf(
            RequiredDocument("Original Sale Deed", "original_sale_deed"),
            RequiredDocument("Death Certificate", "death_certificate"),
            RequiredDocument("Legal Heir Certificate", "legal_heir_certificate"),
            RequiredDocument("All Heirs Aadhaar", "all_heirs_aadhaar"),
            RequiredDocument("Consent Affidavit (All Heirs)", "consent_affidavit_all_heirs"),
            RequiredDocument("Encumbrance Certificate (EC)", "encumbrance_certificate"),
            RequiredDocument("Latest Tax Receipt", "latest_tax_receipt")
        )

        else -> listOf(
            RequiredDocument("Sale Deed (Registry Paper)", "sale_deed"),
            RequiredDocument("Khata / Patta Certificate", "khata_patta"),
            RequiredDocument("Encumbrance Certificate (EC)", "encumbrance_certificate"),
            RequiredDocument("Latest Tax Receipt", "latest_tax_receipt"),
            RequiredDocument("Aadhaar / PAN", "aadhaar_pan")
        )
    }
}

@Composable
fun UploadMediaScreen(
    viewModel: SellLandViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    var images by remember(draft.images) { mutableStateOf(draft.images) }
    val sellerType = draft.sellerDetails?.sellerType
    val requiredDocs = remember(sellerType) { requiredDocumentsForSellerType(sellerType) }

    // Existing draft fields se initial uploaded docs detect
    val uploadedFromDraft = remember(draft.documents) {
        buildSet {
            val docs = draft.documents
            if (!docs.saleDeed.isNullOrEmpty()) add("sale_deed")
            if (!docs.khataCertificate.isNullOrEmpty()) add("khata_patta")
            if (!docs.encumbrance.isNullOrEmpty()) add("encumbrance_certificate")
            if (!docs.sellerIdentity.isNullOrEmpty()) add("seller_identity_proof")
        }
    }

    var uploadedDocTypes by remember(uploadedFromDraft) { mutableStateOf(uploadedFromDraft) }
    var currentDocType by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
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

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        val docType = currentDocType
        if (uri != null && !docType.isNullOrBlank()) {
            viewModel.uploadDocumentFromUri(
                documentType = docType,
                uri = uri,
                contentResolver = context.contentResolver,
                cacheDir = context.cacheDir
            )
            uploadedDocTypes = uploadedDocTypes + docType
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Land Photos",
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    AddMediaCard(
                        label = "Add Photo",
                        onClick = { photoPickerLauncher.launch("image/*") }
                    )
                }

                items(images) { image ->
                    MediaPreviewCard(
                        imageUri = image,
                        mediaBaseUrl = "http://10.208.82.22:8000",
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

            Text(
                text = "Land Documents",
                style = MaterialTheme.typography.titleMedium
            )

            requiredDocs.forEach { doc ->
                val uploaded = uploadedDocTypes.contains(doc.backendType)
                DocumentUploadCard(
                    title = doc.title,
                    uploaded = uploaded
                ) {
                    currentDocType = doc.backendType
                    documentPickerLauncher.launch("application/pdf")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val uploadedRequiredCount = requiredDocs.count { uploadedDocTypes.contains(it.backendType) }
            Text(
                text = "$uploadedRequiredCount/${requiredDocs.size} required documents uploaded",
                style = MaterialTheme.typography.bodySmall,
                color = if (uploadedRequiredCount == requiredDocs.size) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
            )

            val minPhotosValid = images.size >= 3
            val allDocsUploaded = requiredDocs.all { uploadedDocTypes.contains(it.backendType) }

            Button(
                onClick = {
                    viewModel.updateImages(images)
                    viewModel.saveDraft(step = 3)
                    onContinue()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = minPhotosValid && allDocsUploaded
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
        modifier = Modifier.size(120.dp),
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
    mediaBaseUrl: String
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
            if (uploaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
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
                tint = if (uploaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(title)
        }
    }
}
