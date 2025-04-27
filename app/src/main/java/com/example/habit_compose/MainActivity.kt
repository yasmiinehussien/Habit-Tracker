package com.example.habit_compose

import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.habit_compose.ui.theme.Habit_composeTheme
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Habit_composeTheme {
                val navController = rememberNavController()

                // HabitCategoryScreen(navController = navController)
                NavScreen()

            }
        }

    }
}



@Composable
fun HabitListFromDb(habits: List<Habit>,navController: NavController) {
    val categoryMap = habitCategories.associateBy { it.title }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .fillMaxSize()
    ) {
        if (habits.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("No habits yet", color = Color.Gray)
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your Habits", fontSize = 23.sp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    items(habits) { habit ->
                        val category = categoryMap[habit.categoryTag]
                        // or habit.categoryTag
                        HabitCardFromDb(habit, category, navController)
                    }
                }
            }
        }
    }
}
fun getUsername(): String {
    val user = FirebaseAuth.getInstance().currentUser
    return user?.displayName ?: user?.email?.substringBefore("@") ?: "Guest"
}



@Composable
fun HomeScreen(navController: NavController)
{
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var savedHabits by remember { mutableStateOf(listOf<Habit>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            val habits = db.habitDao().getAllHabits()
            withContext(Dispatchers.Main) {
                savedHabits = habits
            }
        }
    }


    val calenderData = CalenderData()
    val today = calenderData.today
    val weekDates = calenderData.getWeekDates()

    val selectedDate = rememberSaveable { mutableStateOf(today) }

    Column {
        HeadIcons()

        LazyRow {
            items(weekDates) { date ->
                DateBar(
                    day = date.day,
                    date = date.date.dayOfMonth.toString(),
                    isSelected = date.date == selectedDate.value,
                    onClick = { selectedDate.value = date.date }
                )
            }
        }

        HabitListFromDb(habits = savedHabits, navController = navController)
    }
}


@Composable
fun HeadIcons() {
    val username = getUsername()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 28.dp)
            .padding(top = 10.dp)
    ) {

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = {/*to notification screen */ }) {

                Icon(
                    imageVector = Icons.TwoTone.Notifications,
                    contentDescription = "notification icon",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Gray,
                )
            }


        }
        Text(
            // default for ui ,should take username from firebase
            text = "Hi ,$username ",
            fontFamily = FontFamily(Typeface.DEFAULT_BOLD),
            fontSize = 21.sp,
        )
        Text(
            text = "Let's make habits together!",
            color = Color.Gray,
            modifier = Modifier
                .padding(top = 10.dp)
        )

    }
}

@Composable
fun DateBar(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 3.dp, horizontal = 14.dp)
            .clip(RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.Black else Color(0xFFF6FEFF)
        )
    )
    {
        Column(
            modifier = Modifier
                .width(50.dp)
                .height(68.dp)
                .padding(4.dp)

        ) {
            Box(
                modifier = Modifier
                    .size(27.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp), clip = true
                    )
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)

                    .background(Color(0xFFFDFDFD))

            ) {

                Text(
                    text = date,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium,

                    )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp)

            ) {
                Text(
                    text = day,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF55C9D7)

                )


            }

        }
    }
}


@Composable
fun HabitList() {

    Box(
        modifier = Modifier
            .padding(16.dp)
            .padding(top=5.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(22.dp, RoundedCornerShape(20.dp), clip = false)
            .background(Color.White)
            .fillMaxSize()
    ) {
        if(habitCategories.isEmpty()){
            Column(

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.lifestyle),
                    contentDescription = "image life style ",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "You don't have any habits",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp)

                )
                Text(
                    text = "Let's add new habit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 9.dp)
                )
            }
        }else{

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Habits List", fontSize = 23.sp)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement =  Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp),


                    ) {
                    itemsIndexed(habitCategories) { index, habit ->
                        HabitCardStyled(habit = habit, index = index) {

                        }
                    }
                }




            }
        }
    }

}

@Composable
fun HabitCardGridStyle(){}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {

    NavScreen()
}


@Composable
fun HabitCardFromDb(habit: Habit, category: HabitCategory?, navController: NavController) {
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        animatedAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
        animatedOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        )
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
            .clickable {
                navController.navigate("habit_details/${habit.id}")
            }
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = animatedOffset.value
            }
            .fillMaxWidth()
            .then(Modifier.widthIn(max = 400.dp)) // ✅ Wider card with limit
            .height(260.dp)
            .padding(horizontal = 8.dp)
            .shadow(12.dp, RoundedCornerShape(26.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                painter = painterResource(id = category?.bgImage ?: R.drawable.back_yoga),
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
                        .size(100.dp)
                        .shadow(10.dp, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = category?.illustration ?: R.drawable.reading),
                        contentDescription = habit.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = habit.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFF1B1B1F),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = category?.tagColor?.copy(alpha = 0.15f) ?: Color.LightGray,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .height(34.dp) // ✅ Less height
                        .widthIn(min = 140.dp) // ✅ Wider button
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = category?.tag ?: "Habit",
                            color = category?.tagColor ?: Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}