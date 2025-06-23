package com.doyoonkim.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.title
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    modifier: Modifier = Modifier,
    initialTime: Long? = null,
    onDismissed: (Int, Int, Int) -> Unit
) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        .apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    val localOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis())

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = initialTime ?: (calendar.timeInMillis + localOffset)
    )

    var pickerVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.wrapContentWidth()
    ) {
        Surface(
            modifier = Modifier.wrapContentSize()
                .background(Color.Transparent)
                .clip(RoundedCornerShape(10.dp))
                .clickable { pickerVisible = !pickerVisible },
            color = MaterialTheme.colorScheme.onAnyBackground
        ) {
            Text(
                text = datePickerState.selectedDateMillis!!.toFormattedString(),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.title,
                modifier = Modifier.padding(10.dp)
            )
        }

        if (pickerVisible) {
            DatePickerDialog(
                onDismissRequest =  {
                     datePickerState.selectedDateMillis?.let {
                         with(getInfo(it.toFormattedString())) {
                             onDismissed(this[0], this[1], this[2])
                         }
                     }.also { pickerVisible = !pickerVisible }
                },
                confirmButton = {  }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
        }
    }

}

private fun Long.toFormattedString() =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)

private fun getInfo(formattedDate: String) =
    formattedDate.split("-").map { it.toInt() }

@Preview(showBackground = true)
@Composable
fun DateTimePicker_Preview() {
//    DateTimePicker { Log.d("Received", "Selected: $it") }
}