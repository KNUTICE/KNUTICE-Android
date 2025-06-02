package com.doyoonkim.common.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    modifier: Modifier = Modifier,
    initialTime: Long = System.currentTimeMillis(),
    onDateTimeConfirmed: (Long?) -> Unit
) {
    val calendar = Calendar.getInstance().also {
        it.timeInMillis = initialTime
    }
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Input,
        initialSelectedDateMillis = initialTime
    )
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    var selectedDateTime by remember { mutableStateOf<Long?>(null) }
    var confirmEnabled by remember { mutableStateOf<Boolean>(false) }
    LaunchedEffect(
        datePickerState.selectedDateMillis, timePickerState.hour, timePickerState.minute
    ) {
        if (datePickerState.selectedDateMillis == null) {
            confirmEnabled = false
        } else {
            val target = combineDateTime(
                datePickerState.selectedDateMillis!!,
                Pair(timePickerState.hour, timePickerState.minute)
            )
            confirmEnabled = target!! > (calendar.timeInMillis ?: (target + 1))
        }
    }

    Surface(
        modifier = modifier.wrapContentSize().background(Color.Transparent),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
            TimeInput(
                state = timePickerState
            )
            TextButton(
                onClick = {
                    selectedDateTime = combineDateTime(
                        datePickerState.selectedDateMillis!!,
                        Pair(timePickerState.hour, timePickerState.minute)
                    )
                    Log.d("DateTimePicker", "$selectedDateTime")
                    onDateTimeConfirmed(selectedDateTime)
                },
                enabled = confirmEnabled,
                colors = ButtonDefaults.textButtonColors()
            ) { Text(stringResource(R.string.btn_confirm)) }

        }
    }

}

private fun Long.toFormattedDate(f: SimpleDateFormat): String {
    return f.format(Date(this).time)
}

private fun String.toMillis(f: SimpleDateFormat): Long? {
    return f.parse(this)?.time
}

private fun combineDateTime(d: Long, t: Pair<Int, Int>): Long? {
    val date = LocalDate.parse(d.toFormattedDate(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())))
    val time = LocalTime.of(t.first, t.second)
    return LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

private fun Long.dropSeconds(): Long? {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd a Hm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
    }
    return this.toFormattedDate(dateFormat).toMillis(dateFormat)
}

@Preview(showBackground = true)
@Composable
fun DateTimePicker_Preview() {
//    DateTimePicker { Log.d("Received", "Selected: $it") }
}