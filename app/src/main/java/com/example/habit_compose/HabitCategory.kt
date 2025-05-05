package com.example.habit_compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

data class HabitCategory(
    val title: String,
    val tag: String,
    val bgImage: Int,
    val illustration: Int,
    val tagColor: Color
)

val habitCategories = listOf(
    HabitCategory("Eat Healthy", "Nutrition", R.drawable.back_yoga, R.drawable.eat_healthy, Color(0xFFFF7043)),
    HabitCategory("Don't smoke", "Smoking", R.drawable.bc_cigratte, R.drawable.ciggrate, Color(0xFF29B6F6)),
    HabitCategory("Glass of water", "Health", R.drawable.back_water, R.drawable.water1, Color(0xFFAB47BC)),
    HabitCategory("Yoga", "Meditation", R.drawable.back_yoga, R.drawable.yoga1, Color(0xFF66BB6A)),
    HabitCategory("Exercise", "Sport", R.drawable.sport_back, R.drawable.sport2, Color(0xFFEF5350)),
    HabitCategory("Reading", "Focus", R.drawable.back_water, R.drawable.reading, Color(0xFFFFA726)),
    HabitCategory("Journaling", "Mindfulness", R.drawable.back_yoga, R.drawable.reading, Color(0xFF5C6BC0)),
    HabitCategory("Sleep Early", "Health", R.drawable.sport_back, R.drawable.sleep, Color(0xFF26A69A)),
    HabitCategory("Pray", "Religion", R.drawable.back_yoga, R.drawable.pray, Color(0xFF8D6E63)),
    HabitCategory("Quran", "Religion", R.drawable.back_water, R.drawable.quran, Color(0xFF8D6E63)),
    HabitCategory("Gratitude", "Positivity", R.drawable.back_yoga, R.drawable.grateful, Color(0xFF8D6E63)),
    HabitCategory("Time Management", "Productivity", R.drawable.back_water, R.drawable.time_mangament, Color(0xFF7E57C2)),
    HabitCategory("Learn New Skill", "Growth", R.drawable.back_water, R.drawable.learning_newskill, Color(0xFFEC407A))
)

@Composable
fun HabitCategoryScreen(navController: NavController) {
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Filter habits based on search query
    val filteredHabits = remember(searchQuery.text) {
        if (searchQuery.text.isEmpty()) {
            habitCategories
        } else {
            habitCategories.filter { habit ->
                habit.title.contains(searchQuery.text, ignoreCase = true) ||
                        habit.tag.contains(searchQuery.text, ignoreCase = true)
            }
        }
    }

    Surface(
        color = Color(0xFFF5F7FA),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            if (showSearchBar) {
                // Search bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text("Search habits...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (searchQuery.text.isNotEmpty()) {
                                searchQuery = TextFieldValue("")
                            } else {
                                showSearchBar = false
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            } else {
                // Header with title and search icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Your Habits",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B1B1B),
                        letterSpacing = 0.5.sp
                    )

                    IconButton(onClick = { showSearchBar = true }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF1B1B1B),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(filteredHabits) { index, habit ->
                    HabitCardStyled(habit = habit, index = index) {
                        navController.navigate("habit_form/${habit.title}")
                    }
                }
            }
        }
    }
}

@Composable
fun HabitCardStyled(habit: HabitCategory, index: Int, onClick: () -> Unit) {
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        delay(index * 70L) // Faster stagger (50ms per card)
        animatedAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
        )
        animatedOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 180, easing = LinearOutSlowInEasing)
        )
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            }
            .fillMaxWidth()
            .height(260.dp)
            .shadow(12.dp, RoundedCornerShape(26.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFDFF5EC), Color.Transparent)
                        )
                    )
            )

            Image(
                painter = painterResource(id = habit.bgImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .alpha(0.07f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = habit.illustration),
                        contentDescription = habit.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = habit.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    color = Color(0xFF1B1B1F)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = habit.tagColor.copy(alpha = 0.15f),
                    shadowElevation = 0.dp
                ) {
                    Text(
                        text = habit.tag,
                        color = habit.tagColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}