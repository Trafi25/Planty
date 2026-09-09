package com.traffipart.polanty.domain.repository

interface PlantNameResolver {

    suspend fun resolveNames(scientificName : String) : List<String>

}