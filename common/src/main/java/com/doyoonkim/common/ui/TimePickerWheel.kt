package com.doyoonkim.common.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.containerBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.textPurple
import com.doyoonkim.common.theme.title
import kotlinx.coroutines.flow.filter

/**
 * @author kimdoyoon
 * Created 6/20/25 at 9:54 PM
 */

@Composable
fun TimePickerWheel(
    modifier: Modifier,
    initialHourSelected: Int,
    initialMinuteSelected: Int,
    onSelected: (String) -> Unit
) {
    val hours = (0..23).map { if (it < 10) "0$it" else it.toString() }.toList()
    val minutes = (0..59).map { if (it< 10) "0$it" else it.toString() }.toList()
    var hourSelected by remember { mutableIntStateOf(initialHourSelected.coerceIn(0, 23)) }
    var minuteSelected by remember { mutableIntStateOf(initialMinuteSelected.coerceIn(0, 59)) }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Transparent),
        color = MaterialTheme.colorScheme.containerBackground
    ) {
        Column(
            modifier = Modifier.wrapContentSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelPicker(
                    modifier = Modifier.background(Color.Transparent)
                        .padding(start = 35.dp, end = 35.dp),
                    elements = hours,
                    initialItemIndex = initialHourSelected,
                ) {

                }

                Text(
                    text = ":",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.title,
                    modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
                )

                WheelPicker(
                    modifier = Modifier.background(Color.Transparent)
                        .padding(start = 35.dp, end = 35.dp),
                    elements = minutes,
                    initialItemIndex = initialMinuteSelected
                ) {

                }
            }
        }
    }
}

@Composable
internal fun WheelPicker(
    modifier: Modifier = Modifier,
    elements: List<String>,
    initialItemIndex: Int = 0,
    numberOfVisibleItem: Int = 3,
    onSelected: (String) -> Unit
) {
    // List State
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialItemIndex
    )
    // Selection State
    var selectedItem by remember { mutableIntStateOf(initialItemIndex) }

    val columnHeight = numberOfVisibleItem * 40
    val spacerCount = numberOfVisibleItem / 2
    val itemHeightPx = with(LocalDensity.current) { 40.dp.toPx() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { isScrolling ->
                // Filter status in order to emit value when isScrollInProgress finishes scrolling.
                !isScrolling
            }.collect {
                val firstVisibleItem = listState.firstVisibleItemIndex
                val offset = listState.firstVisibleItemScrollOffset

                // Determine items to be selected, when scroll stops, and two elements takes the center area.
                val offsetFraction = offset / itemHeightPx
                // When two elements takes the center area, and element on bottom takes more than half of the center area, take bottom element as selected.
                val adjusted = if (offsetFraction > 0.5) 1 else 0
                val indexInList = firstVisibleItem + adjusted - spacerCount
                // Prevent Unexpected IndexOutOfBoundsException
                val clampedIndex = (indexInList + 1).coerceIn(0, elements.lastIndex)

                selectedItem = clampedIndex
            }
    }

    LaunchedEffect(selectedItem) {
        listState.animateScrollToItem(selectedItem)
        onSelected(elements[selectedItem])
    }

    LaunchedEffect(initialItemIndex) {
        listState.animateScrollToItem(initialItemIndex)
    }

    LazyColumn(
        modifier = modifier
            .height(columnHeight.dp),
        state = listState,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Spacers to be inserted to top and bottom, to make very first and very last element
        // possible to be shown on center.
        items(spacerCount) { Spacer(Modifier.height(30.dp)) }

        itemsIndexed(elements) { index, item ->
            WheelPickerItem(
                content = item,
                isHighlighted = index == selectedItem
            )
        }

        items(spacerCount) { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
internal fun WheelPickerItem(
    content: String,
    isHighlighted: Boolean = false
) {
    Text(
        text = content,
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        textAlign = TextAlign.Center,
        color = if (isHighlighted) {
            MaterialTheme.colorScheme.textPurple
        } else {
            MaterialTheme.colorScheme.subTitle
        },
        modifier = Modifier.height(40.dp)
            .wrapContentHeight(Alignment.CenterVertically)
            .background(Color.Transparent)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun TimePickerWheel_Preview() {
    /*
    var state by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.wrapContentHeight()
                .padding(end = 20.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Date",
                modifier = Modifier.clickable {
                    state = !state
                }
            )

            if (state) {
                DatePickerDialog(
                    onDismissRequest = {  },
                    confirmButton = {
                        TextButton(
                            onClick = {  }
                        ) {
                            Text("Confirm")
                        }
                    }
                ) {


                    DatePicker(state = rememberDatePickerState())
                }
            }
        }
    }
     */

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val window = LocalWindowInfo.current

        val height = with(window) { this.containerSize / 7 }.height
        val width = with(window) { this.containerSize / 3 }.width

        Dialog(
            onDismissRequest = {  },
            properties = DialogProperties(
                usePlatformDefaultWidth = true
            )
        ) {
            TimePickerWheel(
                modifier = Modifier.width(width.dp).height(height.dp),
                initialHourSelected = 13,
                initialMinuteSelected = 20
            ) { }
        }
    }




}