package com.example.webbrowser.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.webbrowser.viewmodel.MainViewModel
import com.google.accompanist.web.*

@SuppressLint("JavascriptInterface")
@Composable
fun WebBrowser(viewModel: MainViewModel, modifier: Modifier) {

    // Directly access the state from the ViewModel
    val url = viewModel.url
    val textFieldValue = viewModel.textFieldValue
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    // State to control visibility of the Bottom Bar and TextField
    val showBottomBarAndTextField = remember { mutableStateOf(true) }

    val navigator = rememberWebViewNavigator()

    // WebView client to handle page loading and tracking scroll position via JavaScript
    val webClient = remember {
        object : AccompanistWebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("Accompanist WebView", "Page started loading for $url")
                viewModel.onPageLoadingStarted()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("Accompanist WebView", "Page finished loading for $url")
                viewModel.onPageLoadingFinished()
                view?.evaluateJavascript(
                    """
                    var lastScrollTop = 0;
                    window.onscroll = function() {
                        var currentScrollTop = document.documentElement.scrollTop || document.body.scrollTop;
                        if (currentScrollTop > lastScrollTop && currentScrollTop > 50) {
                            Android.onScrollDown();
                        } else if (currentScrollTop < lastScrollTop || currentScrollTop <= 20) {
                            Android.onScrollUp();
                        }
                        lastScrollTop = currentScrollTop <= 0 ? 0 : currentScrollTop; 
                    };
                    """.trimIndent(),
                    null
                )
            }
        }
    }

    // Interface to communicate with JavaScript
    class WebAppInterface(val showBottomBarAndTextField: MutableState<Boolean>) {
        @JavascriptInterface
        fun onScrollDown() {
            showBottomBarAndTextField.value = false
        }

        @JavascriptInterface
        fun onScrollUp() {
            showBottomBarAndTextField.value = true
        }
    }

    // WebView state and navigator
    val webViewState = rememberWebViewState(url = url)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Show the TextField and Bottom Navigation bar if the condition is met
        if (showBottomBarAndTextField.value) {
            // URL TextField
            Row(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(9f)
                        .padding(top = 16.dp),
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        viewModel.updateUrl(newValue)
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.goToUrl(textFieldValue)
                        }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = MaterialTheme.colors.onSurface,
                        textColor = MaterialTheme.colors.onSurface,
                        unfocusedLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        backgroundColor = MaterialTheme.colors.surface
                    ),
                    placeholder = {
                        Text(
                            text = "Enter URL",
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    singleLine = true
                )

                // Go button
                IconButton(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.goToUrl(textFieldValue) }
                ) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go")
                }

                // Show error icon if any
                if (error != null) {
                    Icon(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red
                    )
                }
            }
        }

        // WebView component with the Accompanist WebView
        WebView(
            state = webViewState,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            navigator = navigator,
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
                webView.addJavascriptInterface(WebAppInterface(showBottomBarAndTextField), "Android")
            },
            client = webClient
        )

        // Show the progress bar when loading
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Show the Bottom Navigation Bar
        if (showBottomBarAndTextField.value) {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.onBackground,
                contentColor = MaterialTheme.colors.background
            ) {
                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back") },
                    selected = false,
                    onClick = { navigator.navigateBack() }
                )
                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward") },
                    selected = false,
                    onClick = { navigator.navigateForward() }
                )
                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh") },
                    selected = false,
                    onClick = { navigator.reload() }
                )
            }
        }
    }
}