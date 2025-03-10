package com.example.baikt1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.baikt1.ui.theme.BaiKT1Theme


data class Product(
    val id: Int,
    val imageRes: Int,
    val name: String,
    val decription: String,
    val rating: Float,
    val price : String
)

class MainActivity<dp> : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaiKT1Theme {
                MainScreen()
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()

        Scaffold(
            topBar = {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route
                TopAppBar(
                    title = {
                        Text(
                            when (currentRoute) {
                                "shoe_list" -> "New Arrivals"
                                "shoe_detail/{shoeId}" -> "Shoe Details"
                                else -> "Shop"
                            },
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        if (currentRoute?.startsWith("shoe_detail") == true) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6A5ACD))
                )
            },
            bottomBar = {
                BottomAppBar(containerColor = Color(0xFF6A5ACD)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.White)
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White
                        )
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController,
                startDestination = "shoe_list",
                Modifier.padding(paddingValues)
            ) {
                composable("shoe_list") { ShoeListScreen(navController) }
                composable("shoe_detail/{shoeId}") { backStackEntry ->
                    val shoeId = backStackEntry.arguments?.getString("shoeId")?.toIntOrNull()
                    val shoe = sampleShoes.find { it.id == shoeId }
                    shoe?.let { ShoeDetailScreen(navController, it) }
                }
            }
        }
    }

    @Composable
    fun ShoeListScreen(navController: NavController) {
        LazyColumn {
            items(sampleShoes) { shoe ->
                ShoeItem(shoe) {
                    navController.navigate("shoe_detail/${shoe.id}")
                }
            }
        }
    }

    @Composable
    fun ShoeItem(shoe: Shoe, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable { onClick() },
            elevation = 5.dp,
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Image(
                    painter = painterResource(id = shoe.imageRes),
                    contentDescription = "Shoe Image",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(shoe.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.Yellow)
                        Text("${shoe.rating}", fontSize = 14.sp)
                    }
                    Text(
                        shoe.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }
        }
    }

    private fun Card(
        modifier: Modifier,
        elevation: Dp,
        shape: RoundedCornerShape,
        content: @Composable ColumnScope.() -> Unit
    ) {

    }

    @Composable
    fun ShoeDetailScreen(navController: NavController, shoe: Shoe) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = shoe.imageRes),
                contentDescription = "Shoe Image",
                modifier = Modifier.size(200.dp)
            )
            Text(shoe.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(shoe.description, fontSize = 16.sp)
            Text(shoe.price, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red)

            Button(onClick = { navController.navigate("shoe_list") }) {
                Text("Back to List")
            }
        }
    }

    // Dữ liệu giày mẫu
    data class Shoe(
        val id: Int,
        val name: String,
        val description: String,
        val price: String,
        val rating: Float,
        val imageRes: Int
    )

    val sampleShoes = listOf(
        Shoe(1, "Nike Air Max", "Comfortable running shoes", "$120", 4.5f, R.drawable.giay1),
        Shoe(2, "Adidas Ultraboost", "High-performance sports shoes", "$150", 4.7f, R.drawable.giay2),
        Shoe(3, "Puma RS-X", "Trendy lifestyle sneakers", "$100", 4.3f, R.drawable.giay3),
        Shoe(4, "New Balance 574", "Classic everyday wear", "$90", 4.6f, R.drawable.giay4),
    )
}


