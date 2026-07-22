package com.doyoonkim.common.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurpleStrong
import kotlinx.coroutines.CoroutineScope

// Necessary States.
class ReorderState(
    val listState: LazyListState,
    private val scope: CoroutineScope, // CoroutineScope to perform necessary reordering animation.
    private val onMove: (from: Int, to: Int) -> Unit,
    private val onDragEnds: () -> Unit
) {
    // Index of dragged item
    var draggedItemIndex by mutableStateOf<Int?>(null)
        private set     // allow private access to its setter. Value update can only be happened within this class scope.

    // Applied Offset value of item to be dragged.
    var draggedItemOffset by mutableFloatStateOf(0f)
        private set     // allow private access to its setter. Value update can only be happened within this class scope.

    // State Variables for Internal Physics Calculation
    // functions to perform dragging animation and necessary calculation.
    fun onDragStarts(offset: Offset) {
        // Get exact item touched and to be dragged.
        listState.layoutInfo.visibleItemsInfo
            .firstOrNull { visibleItem ->
                // Iterate visible item and check which item is relevant to provided offset.y.
                offset.y.toInt() in visibleItem.offset..(visibleItem.offset + visibleItem.size)
            }?.also { visibleItemInfo ->
                draggedItemIndex = visibleItemInfo.index
                draggedItemOffset =
                    0f  // Overall dragged distance. Starting from 0f (Drag not yet performed.)
            }
    }

    fun onDrag(offset: Offset) {
        // Dragging. Main purpose of this function: Calculate each drag distance, and maintain cumulative total offset to be applied when drag is completed.
        val currentItemIndex = draggedItemIndex
            ?: return   // Early return when draggedItemIndex is not yet initialized.

        // Get each drag distance from dragAmount: Offset provided by graphicsLayer.
        // Each drag distance to be added to overall offset to get cumulative distance to be applied vertically when dragging is completed.
        draggedItemOffset += offset.y

        // Perform 'Visual/Structural Swap.'
        // Point of 'Swap': The center coordinate enters area of adjacent visible item.
        // Get current items' snapshot before making visual and structural swap.
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        val currentItem = visibleItems.firstOrNull { visibleItem ->
            visibleItem.index == currentItemIndex
        } ?: return

        // Get ABSOLUTE Y offset of item being dragged.
        // currentItem's offset is positive value while draggedItemOffset is negative (moving up: Negative Offset.)
        val absoluteYOffset = currentItem.offset + draggedItemOffset

        // Check whether dragged item's center coordinate is entered area of adjacent item.
        val adjacentItem = visibleItems.firstOrNull { visibleItem ->
            // visibleItem.offset + visibleItem.size -> 'vertical range of adjacent item.
            // This evaluation would ultimately return the very first direct vertical adjacent item.
            // Without visibleItem.index != draggedItemIndex, this evaluation would return exactly the same element of dragged item.
            visibleItem.index != draggedItemIndex &&
                    (absoluteYOffset.toInt() + (currentItem.size / 2)) in visibleItem.offset..(visibleItem.offset + visibleItem.size)
        } ?: return

        // Perform Visual/Structural Swap.
        onMove(currentItemIndex, adjacentItem.index)

        // Update position (index) of item being dragged.
        draggedItemIndex = adjacentItem.index

        // Add 'Swapped' distance to the cumulative offset tracking value.
        draggedItemOffset += currentItem.offset - adjacentItem.offset       // Move up by adjacent item's height (vertical offset)
    }

    fun onDragInterrupted() {
        // Drag completed. Clear-out necessary tracking states.
        draggedItemIndex = null
        draggedItemOffset = 0f

        onDragEnds()
    }
}

@Composable
fun rememberReorderState(
    listState: LazyListState,
    onMove: (from: Int, to: Int) -> Unit,
    onDragEnds: () -> Unit
): ReorderState {
    val scope = rememberCoroutineScope()
    return remember(listState) {
        ReorderState(listState, scope, onMove, onDragEnds)
    }
}

// Modifier extend function to detect user gesture and initiate necessary animation.
fun Modifier.reorderable(state: ReorderState): Modifier =
    this.then(
        Modifier.pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = { offset -> state.onDragStarts(offset) },
                onDrag = { pointerInputChange, dragAmount ->
                    pointerInputChange.consume()        // Prevent detected gesture event repeatedly consumed by others. (ex: Vertical Scroll enabled in parent LazyColumn.)
                    // Programmatically prevent onDrag calling if element goes outside of list boundaries.
                    state.onDrag(dragAmount)
                },
                onDragEnd = { state.onDragInterrupted() },
                onDragCancel = { state.onDragInterrupted() }
            )
        }
    )

// Modifier extend function to allow LazyColumn item can be 'reorderable'
fun Modifier.reorderableItem(state: ReorderState, idx: Int): Modifier =
    this.then(
        // Utilize graphicsLayer to perform animation in right Composition Phase.
        Modifier
            .graphicsLayer {
                if (state.draggedItemIndex == idx) {
                    // Provide visual feedback to indicate item is selected, and ready to be reordered.
                    // Vertical Movement, while scaling it slightly, and dropping shadow for visual distinction.
                    with(state.listState.layoutInfo.visibleItemsInfo) {
                        if (idx == 0 && state.draggedItemOffset < 0f
                            || idx == lastIndex && state.draggedItemOffset > 0f
                        ) {
                            // Element hits either very top or bottom element, and user is trying to drag
                            // element outside of the content-safe area
                            return@graphicsLayer    // Consume visual vertical translation.
                        }
                    }

                    translationY = state.draggedItemOffset
                    scaleX = 1.05f
                    scaleY = 1.05f
                    shadowElevation = 8f
                }
            }
            .zIndex(
                // If item is selected, and ready to be reorder, hover it to provide visual feedback.
                if (state.draggedItemIndex == idx) 1f else 0f
            )
    )


@Composable
fun <T> ReorderableLazyColumn(
    modifier: Modifier = Modifier,
    title: String,
    elements: List<T>,
    onItemDrag: (from: Int, to: Int) -> Unit,
    onItemDragged: () -> Unit,
    itemContent: @Composable ((T) -> Unit)
) {
    val listState = rememberLazyListState()

    // ReorderState
    val reorderState = rememberReorderState(
        listState = listState,
        onMove = { from, to -> onItemDrag(from, to) },
        onDragEnds = onItemDragged
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.title
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .reorderable(reorderState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Is it safe to provide element's hashcode as a primary key to prevent Compose from randomly recycle the UI while dragging is happening.
            itemsIndexed(elements, key = { _, element -> element.hashCode() }) { idx, item ->
                // Check whether item is being dragged or not. (for visual feedback.)
                val isDragged = reorderState.draggedItemIndex == idx
                // Apply visual elevation to item being dragged.
                val dragElevation by animateDpAsState(if (isDragged) 8.dp else 0.dp)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Transparent)
                        .reorderableItem(reorderState, idx)
                        .shadow(dragElevation, RoundedCornerShape(10.dp)),
                    color = if (isDragged) MaterialTheme.colorScheme.variantPurpleStrong else MaterialTheme.colorScheme.secondaryBackground
                ) {
                    itemContent(item)
                }

                /*
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Apply the visual graphics layer translation
                        .reorderableItem(reorderState, index)
                        .shadow(elevation, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDragging) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = major.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = major.college, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                 */
            }
        }
    }
}

data class MajorTopic(
    val id: Int,
    val name: String,
    val college: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReorderableLazyColumn_Preview() {
    // Simulating the MVI ViewModel State
    var majorsList by remember {
        mutableStateOf(
            listOf(
                MajorTopic(1, "Computer Information Technology", "Polytechnic Institute"),
                MajorTopic(2, "Computer Science", "College of Science"),
                MajorTopic(3, "Software Engineering", "Engineering")
            )
        )
    }

    val listState = rememberLazyListState()

    // Initialize our custom physics engine
    val reorderState = rememberReorderState(
        listState = listState,
        onMove = { fromIndex, toIndex ->
            // Transient visual update (Optimistic UI)
            majorsList = majorsList.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
        },
        onDragEnds = {
            // MVI Intent Trigger: Fire Intent.UpdateMajorOrder(majorsList) here
            // This is where you would dispatch to your ViewModel and update Proto DataStore
            println("Drag finished. New Top Priority Major ID: ${majorsList.first().id}")
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Widget Priority",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                // Attach the gesture interceptor to the parent LazyColumn
                .reorderable(reorderState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // THE "MUST" RULE: You MUST provide a stable `key` using the strict ID.
            // If you omit this, Compose will recycle the views randomly during the drag,
            // destroying the visual translation and causing violent flickering.
            itemsIndexed(items = majorsList, key = { _, item -> item.id }) { index, major ->

                val isDragging = reorderState.draggedItemIndex == index

                // Animate the shadow so it pops up smoothly when long-pressed
                val elevation by animateDpAsState(
                    if (isDragging) 8.dp else 0.dp,
                    label = "elevation"
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Apply the visual graphics layer translation
                        .reorderableItem(reorderState, index)
                        .shadow(elevation, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDragging) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = major.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = major.college,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}