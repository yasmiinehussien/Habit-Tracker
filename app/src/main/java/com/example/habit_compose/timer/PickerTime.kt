

package com.example.habit_compose.timer


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anhaki.picktime.PickHourMinuteSecond
import com.anhaki.picktime.utils.PickTimeFocusIndicator
import com.anhaki.picktime.utils.PickTimeTextStyle

@Composable
fun PickerTime(modifier: Modifier=Modifier,onTimeChanged:(Int, Int, Int)->Unit){
    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }
    var second by remember { mutableIntStateOf(0) }
    val containerColor=MaterialTheme.colorScheme.surface

    Column (
        modifier

            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(28.dp))
        PickHourMinuteSecond(

            initialHour = hour,
            onHourChange = { hour = it },
            initialMinute = minute,
            onMinuteChange = { minute = it },
            initialSecond = second,
            onSecondChange = { second = it },
            containerColor = containerColor,
            horizontalSpace = 28.dp,
            selectedTextStyle = PickTimeTextStyle(
                color = Color(0xFF5A504B),
                fontSize = 29.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
            ),
            unselectedTextStyle = PickTimeTextStyle(
                color = Color(0xFFBDB9B7),
                fontSize = 29.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,

                ),
            focusIndicator = PickTimeFocusIndicator(
                enabled = true,
                widthFull = false,
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(2.dp, Color(0xFFAE56BD)),

                ),

            )

        Text(
            text = "Hour  :  Minute  :  Second",
            modifier
                .padding(top = 14.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 23.sp


        )


    }
    onTimeChanged(hour, minute, second)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PickerTimePreview() {



}