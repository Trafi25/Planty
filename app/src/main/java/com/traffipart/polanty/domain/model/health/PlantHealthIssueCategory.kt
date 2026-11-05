package com.traffipart.polanty.domain.model.health

/** Categories of possible plant health issues; unknown findings retain an explicit fallback. */
enum class PlantHealthIssueCategory {
    Overwatering,
    Underwatering,
    LightStress,
    NutrientDeficiency,
    Pests,
    Disease,
    PhysicalDamage,
    Unknown,
}
