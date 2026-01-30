//package com.example.myapplication
//
//import androidx.compose.ui.graphics.vector.ImageVector
//
//
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.pager.HorizontalPager
//
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//
//import androidx.compose.material.icons.filled.Map
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.Dp
//import kotlinx.coroutines.delay
//import androidx.compose.animation.*
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.foundation.lazy.items
//
//
//
//
//
//
//// ========================================================
//// HOME SCREEN
//// ========================================================
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreenExample(navController: NavController) {
//
//    var selectedItem by remember { mutableStateOf("Home") }
//    var showLandDialog by remember { mutableStateOf(false) }
//
//
//    val bannerImages = listOf(
//        R.drawable.banner1,
//        R.drawable.banner2,
//        R.drawable.banner3
//    )
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("MyBroker App", fontWeight = FontWeight.Bold) },
//                actions = {
//                    //NOTIFCATION
//                    IconButton(onClick = { navController.navigate("notifications") }) {
//                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
//                    }
//
//                    // STATUS BAR
//                    IconButton(onClick = { showLandDialog = true }) {
//                        Icon(
//                            Icons.Default.HomeWork,
//                            contentDescription = "My Land"
//                        )
//                    }
//
//                    // PROFILE BAR
//                    IconButton(onClick = { navController.navigate("profile") }) {
//                        Icon(Icons.Default.Person, contentDescription = "Profile")
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            NavigationBar(containerColor = Color(0xFFEEEEEE)) {
//
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//                    label = { Text("Home") },
//                    selected = selectedItem == "Home",
//                    onClick = { selectedItem = "Home" }
//                )
//
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
//                    label = { Text("Search") },
//                    selected = selectedItem == "Search",
//                    onClick = { selectedItem = "Search" }
//                )
//
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
//                    label = { Text("Map") },
//                    selected = selectedItem == "Map",
//                    onClick = {
//                        selectedItem = "Map"
//                        navController.navigate("map")
//                    }
//                )
//
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
//                    label = { Text("Settings") },
//                    selected = selectedItem == "Settings",
//                    onClick = { navController.navigate("settings") }
//                )
//
//            }
//        }
//    ) { padding ->
//
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .background(Color(0xFFF7F9FC))
//                .padding(16.dp),
//            contentPadding = PaddingValues(bottom = 80.dp)
//        ) {
//
//            item {
//                AnimatedVisibility(
//                    visible = true,
//                    enter = slideInHorizontally(initialOffsetX = { -300 }, animationSpec = tween(700))
//                ) {
//                    Column {
//                        Text(
//                            text = "Welcome Back, Raja 👋",
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text("Explore your dashboard", color = Color.Gray, fontSize = 14.sp)
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            item {
//                SlidingBanner(images = bannerImages)
//                Spacer(modifier = Modifier.height(20.dp))
//            }
//
//            item {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    DashboardCard(
//                        title = "Buy Land",
//                        icon = Icons.Default.ShoppingCart,
//                        color = Color(0xFF6C63FF),
//                        width = 150.dp,
//                        height = 110.dp,
//                        onClick = {
//                            navController.navigate("map")    // or "search"
//                        }
//                    )
//
//                    DashboardCard(
//                        title = "Sell Land",
//                        icon = Icons.Default.Place,
//                        color = Color(0xFFFF6584),
//                        width = 150.dp,
//                        height = 110.dp,
//                        onClick = {
//                            navController.navigate("sell_land")   // 👈 EXACT ROUTE MATCH
//                        }
//                    )
//
//                }
//                Spacer(modifier = Modifier.height(20.dp))
//            }
//
//            item {
//                Text(
//                    text = "Recent Listings",
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//            }
//
//            items(
//                listOf(
//                    Triple("Green Valley Plot", "₹25,00,000", R.drawable.sample_land1),
//                    Triple("Hilltop View", "₹40,00,000", R.drawable.sample_land2)
//                )
//            ) { (name, price, image) ->
//                PropertyCard(name = name, price = price, imageRes = image)
//            }
//        }
//        if (showLandDialog) {
//            AlertDialog(
//                onDismissRequest = { showLandDialog = false },
//                title = {
//                    Text("Choose Land Type", fontWeight = FontWeight.Bold)
//                },
//                text = {
//                    Column {
//
//                        // ✅ SELLER LAND
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    showLandDialog = false
//                                    navController.navigate("seller_lands") // ✅ FIXED
//                                }
//                                .padding(12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Store,
//                                contentDescription = "Seller Land",
//                                tint = Color(0xFF6C63FF)
//                            )
//                        }
//
//
//                        Divider()
//
//                        // ✅ BUYER LAND
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    showLandDialog = false
//                                    navController.navigate("buyer_lands")
//                                }
//                                .padding(12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.ShoppingCart,
//                                contentDescription = "Buyer Land",
//                                tint = Color(0xFFFF6584)
//                            )
//
//                        }
//                    }
//                },
//                confirmButton = {},
//                dismissButton = {
//                    TextButton(onClick = { showLandDialog = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//
//    }
//}
//
//
//// ========================================================
//// COMPOSABLES BELOW
//// ========================================================
//@Composable
//fun DashboardCard(
//    title: String,
//    icon: ImageVector,
//    color: Color,
//    width: Dp,
//    height: Dp,
//    onClick: () -> Unit      // 👈 ADD THIS
//) {
//    Card(
//        modifier = Modifier
//            .width(width)
//            .height(height)
//            .clickable { onClick() },    // 👈 ADD THIS
//        colors = CardDefaults.cardColors(containerColor = color),
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(8.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
//            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
//        }
//    }
//}
//
//@Composable
//fun PropertyCard(name: String, price: String, imageRes: Int) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(6.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .background(Color.White)
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = imageRes),
//                contentDescription = name,
//                modifier = Modifier
//                    .size(90.dp)
//                    .clip(RoundedCornerShape(12.dp))
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column {
//                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                Text(price, color = Color(0xFF6C63FF), fontWeight = FontWeight.SemiBold)
//            }
//        }
//    }
//}
//
//@Composable
//fun SlidingBanner(images: List<Int>) {
//
//    val pagerState = rememberPagerState(pageCount = { images.size })
//    val scope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(3000)
//            val next = (pagerState.currentPage + 1) % images.size
//            pagerState.animateScrollToPage(next)
//        }
//    }
//
//    HorizontalPager(
//        state = pagerState,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(180.dp)
//            .clip(RoundedCornerShape(16.dp))
//    ) { page ->
//        Image(
//            painter = painterResource(id = images[page]),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}