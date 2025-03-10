package com.example.webbrowser.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    // URL and loading state for WebView
    var url by mutableStateOf("https://google.com")
    var textFieldValue by mutableStateOf(url)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // Update URL value
    fun updateUrl(newUrl: String) {
        textFieldValue = newUrl
        error = null // Clear any previous error
    }

    // Navigate to new URL
    fun goToUrl(newUrl: String) {
        val formattedUrl = formatUrl(newUrl)
        if (isValidUrl(formattedUrl)) {
            url = formattedUrl
            error = null
        } else {
            error = "Invalid URL"
        }
    }

    // Format URL to ensure it starts with https://
    private fun formatUrl(input: String): String {
        return when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            input.contains(".") -> "https://$input"
            else -> "https://www.google.com/search?q=${input.replace(" ", "+")}"
        }
    }

    // Validate URL
    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }

    // Set loading state
    fun setLoadingState(loading: Boolean) {
        isLoading = loading
    }

    // Update the loading state based on WebView status
    fun onPageLoadingStarted() {
        setLoadingState(true)
    }

    fun onPageLoadingFinished() {
        setLoadingState(false)
    }
}