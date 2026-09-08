package br.com.bloqfone.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.bloqfone.data.ConfigRepository

class MainViewModel(private val configRepository: ConfigRepository) : ViewModel() {

    // State variable that triggers UI recomposition when changed
    var isFocusModeOn by mutableStateOf(configRepository.isFocusModeEnabled)
        private set

    // Updates both the UI state and the SharedPreferences file
    fun toggleFocusMode(enabled: Boolean) {
        isFocusModeOn = enabled
        configRepository.isFocusModeEnabled = enabled
    }
}

// Factory class required to pass the Context into our ViewModel
class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(ConfigRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
