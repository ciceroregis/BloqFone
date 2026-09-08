package br.com.bloqfone.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ConfigRepository(context: Context) {

    // Creates a local SharedPreferences file to save the user's settings
    private val prefs: SharedPreferences = context.getSharedPreferences("bloqfone_prefs", Context.MODE_PRIVATE)

    // Property to get and set the Focus Mode state
    var isFocusModeEnabled: Boolean
        get() = prefs.getBoolean("focus_mode", false)
        set(value) = prefs.edit { putBoolean("focus_mode", value) }
}