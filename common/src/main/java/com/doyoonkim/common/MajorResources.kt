package com.doyoonkim.common

import androidx.compose.runtime.Composable
import com.doyoonkim.model.MajorCategory

class MajorResources {
    companion object {
        @Composable
        fun getLocalizedString(category: String): Int {
            return when (category) {
                MajorCategory.MECHANICAL_ENGINEERING.name -> R.string.major_mechanical_engineering
                MajorCategory.AUTOMOTIVE_ENGINEERING.name -> R.string.major_automotive_engineering
                MajorCategory.AERONAUTICAL_AND_MECHANICAL_DESIGN_ENGINEERING.name -> R.string.major_aeronautical_and_mechanical_design_engineering
                MajorCategory.ELECTRICAL_ENGINEERING.name -> R.string.major_electrical_engineering
                MajorCategory.ELECTRONIC_ENGINEERING.name -> R.string.major_electronic_engineering
                MajorCategory.COMPUTER_ENGINEERING.name -> R.string.major_computer_engineering
                MajorCategory.COMPUTER_SCIENCE.name -> R.string.major_computer_science
                MajorCategory.AI_ROBOTICS_ENGINEERING.name -> R.string.major_ai_robotics_engineering
                MajorCategory.BIOMEDICAL_ENGINEERING.name -> R.string.major_biomedical_engineering
                MajorCategory.PRECISION_MEDICINE_MEDICAL_DEVICE.name -> R.string.major_precision_medicine_medical_device
                MajorCategory.CIVIL_ENGINEERING.name -> R.string.major_civil_engineering
                MajorCategory.ENVIRONMENTAL_ENGINEERING.name -> R.string.major_environmental_engineering
                MajorCategory.URBAN_AND_TRANSPORTATION_ENGINEERING.name -> R.string.major_urban_and_transportation_engineering
                MajorCategory.CHEMICAL_AND_BIOLOGICAL_ENGINEERING.name -> R.string.major_chemical_and_biological_engineering
                MajorCategory.MATERIALS_SCIENCE_AND_ENGINEERING.name -> R.string.major_materials_science_and_engineering
                MajorCategory.POLYMER_SCIENCE_AND_ENGINEERING.name -> R.string.major_polymer_science_and_engineering
                MajorCategory.INDUSTRIAL_AND_MANAGEMENT_ENGINEERING.name -> R.string.major_industrial_and_management_engineering
                MajorCategory.SAFETY_ENGINEERING.name -> R.string.major_safety_engineering
                MajorCategory.ARCHITECTURAL_ENGINEERING.name -> R.string.major_architectural_engineering
                MajorCategory.ARCHITECTURE.name -> R.string.major_architecture
                MajorCategory.INDUSTRIAL_DESIGN.name -> R.string.major_industrial_design
                MajorCategory.COMMUNICATION_DESIGN.name -> R.string.major_communication_design
                MajorCategory.ENGLISH_LANGUAGE_AND_LITERATURE.name -> R.string.major_english_language_and_literature
                MajorCategory.CHINESE_LANGUAGE.name -> R.string.major_chinese_language
                MajorCategory.KOREAN_LANGUAGE_AND_LITERATURE.name -> R.string.major_korean_language_and_literature
                MajorCategory.MUSIC.name -> R.string.major_music
                MajorCategory.SPORTS_MEDICINE.name -> R.string.major_sports_medicine
                MajorCategory.SPORTS_INDUSTRY.name -> R.string.major_sports_industry
                MajorCategory.PUBLIC_ADMINISTRATION.name -> R.string.major_public_administration
                MajorCategory.PUBLIC_ADMINISTRATION_AND_INFORMATION_CONVERGENCE.name -> R.string.major_public_administration_and_information_convergence
                MajorCategory.BUSINESS_ADMINISTRATION.name -> R.string.major_business_administration
                MajorCategory.CONVERGENCE_MANAGEMENT.name -> R.string.major_convergence_management
                MajorCategory.INTERNATIONAL_TRADE_AND_BUSINESS.name -> R.string.major_international_trade_and_business
                MajorCategory.SOCIAL_WELFARE.name -> R.string.major_social_welfare
                MajorCategory.AIRLINE_SERVICE.name -> R.string.major_airline_service
                MajorCategory.AERONAUTICAL_SCIENCE_AND_FLIGHT_OPERATION.name -> R.string.major_aeronautical_science_and_flight_operation
                MajorCategory.EARLY_CHILDHOOD_EDUCATION.name -> R.string.major_early_childhood_education
                MajorCategory.MEDIA_AND_CONTENTS.name -> R.string.major_media_and_contents
                MajorCategory.NURSING.name -> R.string.major_nursing
                MajorCategory.PHYSICAL_THERAPY.name -> R.string.major_physical_therapy
                MajorCategory.PARAMEDICINE.name -> R.string.major_paramedicine
                MajorCategory.FOOD_SCIENCE_AND_TECHNOLOGY.name -> R.string.major_food_science_and_technology
                MajorCategory.FOOD_AND_NUTRITION.name -> R.string.major_food_and_nutrition
                MajorCategory.BIOTECHNOLOGY.name -> R.string.major_biotechnology
                MajorCategory.EARLY_CHILDHOOD_SPECIAL_EDUCATION.name -> R.string.major_early_childhood_special_education
                MajorCategory.RAILROAD_MANAGEMENT_AND_LOGISTICS.name -> R.string.major_railroad_management_and_logistics
                MajorCategory.DATA_SCIENCE.name -> R.string.major_data_science
                MajorCategory.ARTIFICIAL_INTELLIGENCE.name -> R.string.major_artificial_intelligence
                MajorCategory.RAILROAD_OPERATION_SYSTEMS_ENGINEERING.name -> R.string.major_railroad_operation_systems_engineering
                MajorCategory.RAILWAY_VEHICLE_SYSTEM_ENGINEERING.name -> R.string.major_railway_vehicle_system_engineering
                MajorCategory.RAILROAD_INFRASTRUCTURE_ENGINEERING.name -> R.string.major_railroad_infrastructure_engineering
                MajorCategory.RAILROAD_ELECTRICAL_AND_INFORMATION_ENGINEERING.name -> R.string.major_railroad_electrical_and_information_engineering
                else -> R.string.title_major_select
            }
        }

        @Composable
        fun getDrawable(): Int = R.drawable.college_notice
    }
}