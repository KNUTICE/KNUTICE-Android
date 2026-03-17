package com.doyoonkim.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.doyoonkim.model.MealCategory

class MealResources {
    companion object {

        @Composable
        fun getLocalizedName(category: MealCategory) =
            when (category) {
                MealCategory.STUDENT_CAFETERIA -> stringResource(R.string.title_student_cafeteria)
                MealCategory.STAFF_CAFETERIA -> stringResource(R.string.title_staff_cafeteria)
            }

        @Composable
        fun getLocalizedDescription(category: MealCategory) =
            when (category) {
                MealCategory.STUDENT_CAFETERIA -> stringResource(R.string.description_student_cafeteria_channel)
                MealCategory.STAFF_CAFETERIA -> stringResource(R.string.description_staff_cafeteria_channel)
            }

    }
}