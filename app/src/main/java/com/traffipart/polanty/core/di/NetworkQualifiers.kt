package com.traffipart.polanty.core.di

import javax.inject.Qualifier

/**
 * Qualifier for the PlantNet API networking components.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlantNetClient

/**
 * Qualifier for the Perenual API networking components.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PerenualClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GbifClient