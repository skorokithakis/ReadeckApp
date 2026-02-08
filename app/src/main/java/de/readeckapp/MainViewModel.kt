package de.readeckapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.readeckapp.domain.BookmarkRepository
import de.readeckapp.domain.model.Theme
import de.readeckapp.io.prefs.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val settingsDataStore: SettingsDataStore,
    private val bookmarkRepository: BookmarkRepository
): ViewModel() {
    val theme = settingsDataStore.themeFlow.map {
        it?.let { Theme.valueOf(it) } ?: Theme.SYSTEM
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Theme.SYSTEM
    )

    suspend fun quickAddBookmark(url: String): Boolean {
        return try {
            bookmarkRepository.createBookmark(title = "", url = url)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to quick-add bookmark for URL: $url")
            false
        }
    }
}