package com.example.composegraphic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.composegraphic.Lab3.Scene3D

@Composable
fun Lab3Screen(){
    Box(modifier = Modifier.fillMaxSize()) {
        Scene3D()
    }
}