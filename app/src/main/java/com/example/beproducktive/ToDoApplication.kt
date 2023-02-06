package com.example.beproducktive

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*
 * Necessario per il funzionamento di Dagger Hilt.
 * Inoltre bisogna andare dentro AndroidManifest ed inserire:
 * android:name=".ToDoApplication"
 */
@HiltAndroidApp
class ToDoApplication : Application() {
}