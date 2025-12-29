package com.doyoonkim.common

import androidx.compose.runtime.Composable
import com.doyoonkim.model.CollegeType
import com.doyoonkim.model.MajorCategory

class CollegeResource {
    companion object {
        @Composable
        fun getLocalizedCollegeString(category: String): Int {
            return getLocalizedString(category)
        }

        @Composable
        fun getLocalizedCollegeStringByMajor(major: String): Int {
            val localizedString = runCatching {
                val collegeByMajor = MajorCategory.valueOf(major).collegeType
                getLocalizedString(collegeByMajor.name)
            }

            localizedString.fold(
                onSuccess = { return it },
                onFailure = { return R.string.text_value_not_found }
            )
        }

        private fun getLocalizedString(name: String): Int {
            return when(name) {
                CollegeType.CONVERGENCE_TECH.name -> R.string.college_convergence_technology
                CollegeType.ENGINEERING.name -> R.string.college_engineering
                CollegeType.HUMANITIES.name -> R.string.college_humanities
                CollegeType.SOCIAL_SCIENCES.name -> R.string.college_social_science
                CollegeType.HEALTH_AND_LIFE_SCIENCE.name -> R.string.college_health_and_life_science
                CollegeType.RAILROAD_SCIENCE.name -> R.string.college_railroad
                else -> R.string.text_value_not_found
            }
        }

    }
}