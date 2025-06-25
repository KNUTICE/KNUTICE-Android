package com.doyoonkim.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground

@Composable
fun TopAppBarDropdownMenu(
    modifier: Modifier = Modifier,
    iconVector: ImageVector,
    iconTint: Color,
    menuContainerColor: Color,
    menuContentColor: Color,
    menuOptions: List<String>,
    onMenuSelected: (Int) -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = { isMenuExpanded = !isMenuExpanded }
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = "Dropdown Menu",
                tint = iconTint
            )
        }
        
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = !isMenuExpanded },
            shape = RoundedCornerShape(15.dp),
            containerColor = menuContainerColor,
        ) {
            menuOptions.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            textAlign = TextAlign.Center,
                            color = menuContentColor
                        )
                    },
                    onClick = {
                        onMenuSelected(index)
                    }
                )
            }
        }
    }


}