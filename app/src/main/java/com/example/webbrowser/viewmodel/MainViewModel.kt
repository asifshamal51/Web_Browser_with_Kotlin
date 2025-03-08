package com.example.webbrowser.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    // URL and loading state for WebView
    var url by mutableStateOf("https://google.com")
    var textFieldValue by mutableStateOf(url)
    var isLoading by mutableStateOf(false)

    // Update URL value
    fun updateUrl(newUrl: String) {
        textFieldValue = newUrl
    }

    // Navigate to new URL
    fun goToUrl(newUrl: String) {
        url = newUrl
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
