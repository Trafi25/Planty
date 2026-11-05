package com.traffipart.polanty.domain.model.health

/** Overall visible plant condition; uncertainty is distinct from a healthy assessment. */
enum class PlantHealthStatus {
    Healthy,
    NeedsAttention,
    Uncertain,
}
