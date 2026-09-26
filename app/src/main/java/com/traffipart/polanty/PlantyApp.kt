package com.traffipart.polanty

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PlantyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        if (BuildConfig.DEBUG) {
            Firebase.appCheck
                .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            Log.d(
                TAG,
                "Firebase App Check initialized with DebugAppCheckProviderFactory. " +
                    "To enable App Check enforcement for Gemini API calls, register the Logcat debug secret token in Firebase Console.",
            )
            Firebase.appCheck.getToken(false).addOnSuccessListener {
                Log.d(TAG, "App Check token requested successfully.")
            }.addOnFailureListener { e ->
                Log.w(TAG, "App Check token request failed: ${e.message}", e)
            }
        } else {
            Firebase.appCheck
                .installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        }
    }

    private companion object {
        const val TAG = "PlantyApp"
    }
}
