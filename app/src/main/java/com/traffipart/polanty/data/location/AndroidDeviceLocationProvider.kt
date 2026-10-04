package com.traffipart.polanty.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.traffipart.polanty.domain.location.DeviceLocationProvider
import com.traffipart.polanty.domain.location.GeoCoordinates
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Implementation of [DeviceLocationProvider] using Google Play Services [FusedLocationProviderClient].
 *
 * Checks for runtime [Manifest.permission.ACCESS_COARSE_LOCATION] before requesting the current
 * balanced power accuracy location coordinates.
 *
 * @property context Application context used for checking runtime permissions.
 * @property locationClient Fused location provider client for fetching device coordinates.
 */
class AndroidDeviceLocationProvider
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
        private val locationClient: FusedLocationProviderClient,
    ) : DeviceLocationProvider {
        override suspend fun getCurrentLocation(): GeoCoordinates? {
            val hasPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return null
            return suspendCancellableCoroutine { continuation ->
                val cancellationToken = CancellationTokenSource()
                locationClient
                    .getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cancellationToken.token,
                    ).addOnSuccessListener { location ->
                        if (!continuation.isActive) {
                            return@addOnSuccessListener
                        }
                        val coords = location?.let { GeoCoordinates(latitude = it.latitude, longitude = it.longitude) }
                        continuation.resume(coords)
                    }.addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                continuation.invokeOnCancellation {
                    cancellationToken.cancel()
                }
            }
        }
    }
