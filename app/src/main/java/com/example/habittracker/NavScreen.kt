package com.example.habittracker

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius


@Composable
fun NavScreen(modifier: Modifier = Modifier) {

    val navIconList = listOf(
        NavIcon("Home", Icons.Default.Home),
        NavIcon("Add", Icons.Default.AddCircle),
        NavIcon("Stats", Icons.Default.InsertChart),
        NavIcon("Personal", Icons.Default.Person)
    )


    var selectedIndex by remember { mutableStateOf(0) }


    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 100.dp else 80.dp)
                    .padding(bottom = 16.dp)
            ) {
                AnimatedNavigationBar(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    selectedIndex = selectedIndex,
                    cornerRadius = shapeCornerRadius(cornerRadius = 24.dp),
                    ballAnimation = Parabolic(

                        tween(durationMillis = 500)
                    ),
                    indentAnimation = Height(tween(durationMillis = 300)),
                    barColor = MaterialTheme.colorScheme.surface,
                    ballColor = Color(0xFF3B773D)
                ) {

                    navIconList.forEachIndexed { index, item ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    selectedIndex = index
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(if (isTablet) 30.dp else 24.dp),
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selectedIndex == index)
                                    Color(0xFF4CAF50)
                                else
                                    Color.Gray
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> HomeScreen()
                else -> PlaceholderScreen()
            }
        }

    }
}


@Composable
fun PlaceholderScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Screen coming soon...", color = Color.Gray)
    }
}


@Preview(showSystemUi = true)
@Composable
fun NavScreenPreview() {
    NavScreen()
}

