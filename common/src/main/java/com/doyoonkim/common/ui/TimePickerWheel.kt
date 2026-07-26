package com.doyoonkim.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import kotlinx.coroutines.flow.filter
import java.util.Calendar

/**
 * @author kimdoyoon
 * Created 6/20/25 at 9:54 PM
 */

@Composable
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    initialTime: Long? = null,
    onDismissed: (Int, Int) -> Unit
) {
    val calendar = Calendar.getInstance()
        .apply { initialTime?.let { timeInMillis = it } }

    val hours = (0..23).map { if (it < 10) "0$it" else it.toString() }.toList()
    val minutes = (0..59).map { if (it < 10) "0$it" else it.toString() }.toList()
    var hourSelected by remember {
        mutableIntStateOf(calendar[Calendar.HOUR_OF_DAY])
    }
    var minuteSelected by remember {
        mutableIntStateOf(calendar[Calendar.MINUTE])
    }

    var pickerVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.wrapContentSize()
    ) {
        Surface(
            modifier = Modifier.wrapContentSize()
                .background(Color.Transparent)
                .clip(RoundedCornerShape(10.dp))
                .clickable { pickerVisible = !pickerVisible },
            color = MaterialTheme.colorScheme.onAnyBackground
        ) {
            Text(
                text = "${hours[hourSelected]}:${minutes[minuteSelected]}",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.title,
                modifier = Modifier.padding(
                    PaddingValues(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
                )
            )
        }

        if (pickerVisible) {
            Dialog(
                onDismissRequest = {
                    onDismissed(hourSelected, minuteSelected)
                    pickerVisible = !pickerVisible
                }
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Transparent),
                    color = MaterialTheme.colorScheme.secondaryBackground
                ) {
                    Column(
                        modifier = Modifier.wrapContentSize()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.text_select_time),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.title,
                            modifier = Modifier.padding(15.dp)
                        )
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WheelPicker(
                                modifier = Modifier.background(Color.Transparent)
                                    .padding(start = 35.dp, end = 35.dp),
                                elements = hours,
                                initialItemIndex = hourSelected
                            ) {
                                hourSelected = it
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
                                initialItemIndex = minuteSelected
                            ) {
                                minuteSelected = it
                            }
                        }
                        Spacer(Modifier.height(30.dp))
                    }
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
    onSelected: (Int) -> Unit
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
        onSelected(selectedItem)
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
        items(spacerCount) { Spacer(Modifier.height(40.dp)) }

        itemsIndexed(elements) { index, item ->
            WheelPickerItem(
                content = item,
                isHighlighted = index == selectedItem
            )
        }

        items(spacerCount) { Spacer(Modifier.height(40.dp)) }
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
            MaterialTheme.colorScheme.variantPurple
        } else {
            MaterialTheme.colorScheme.subTitle
        },
        modifier = Modifier.height(40.dp)
            .wrapContentHeight(Alignment.CenterVertically)
            .background(Color.Transparent)
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun TimePickerWheel_Preview() {
}
