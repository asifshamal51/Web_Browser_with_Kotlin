package com.example.webbrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.webbrowser.ui.screens.WebBrowser
import com.example.webbrowser.ui.theme.WebBrowserTheme
import com.example.webbrowser.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebBrowserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebBrowser(
                       viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

