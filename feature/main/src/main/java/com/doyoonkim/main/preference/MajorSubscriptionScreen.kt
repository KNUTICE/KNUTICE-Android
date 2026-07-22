package com.doyoonkim.main.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.CollegeResource
import com.doyoonkim.common.MajorResources
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.NavButtonType
import com.doyoonkim.common.ui.TopAppBarWithNavButton
import com.doyoonkim.model.CollegeType
import com.doyoonkim.model.MajorCategory

@Composable
fun MajorSubscriptionScreen(

) {

    val majors = MajorCategory.entries.groupBy { it.collegeType }
    Scaffold(
        topBar = {
            TopAppBarWithNavButton(
                modifier = Modifier.fillMaxWidth(),
                titleText = "학과 구독 변경",
                navButtonType = NavButtonType.BACK,
                onBackPressed = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Current Selection

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                majors.forEach { (college, majors) ->
                    // Later on, replaced by DEPRECATED type.
                    if (college != CollegeType.UNSPECIFIED) {
                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(MaterialTheme.colorScheme.secondaryBackground),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(
                                        CollegeResource.getLocalizedCollegeString(college.name)
                                    ),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.title
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 5.dp)
                                )
                            }
                        }
                    }

                    // List Majors.
                    itemsIndexed(majors) { idx, major ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = stringResource(MajorResources.getLocalizedString(major.name)),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.subTitle
                                ),
                                modifier = Modifier.weight(9f)
                            )
                            RadioButton(
                                selected = false,
                                onClick = {

                                },
                                modifier = Modifier
                                    .wrapContentSize()
                                    .weight(1f),
                                enabled = true,
                                colors = RadioButtonDefaults.colors().copy(
                                    selectedColor = MaterialTheme.colorScheme.variantPurple,
                                    unselectedColor = MaterialTheme.colorScheme.subTitle
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovableColumn(
    modifier: Modifier,
    title: String,
    elements: List<String>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
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

        // Element
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MajorSubscriptionScreen_Preview() {
    MajorSubscriptionScreen()
}