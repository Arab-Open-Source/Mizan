package com.eec.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.eec.app.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

@Composable
fun rememberScreenScope(container: AppContainer, key: Any? = Unit): CoroutineScope {
    val scope = remember(key) { container.createScope() }
    DisposableEffect(scope) {
        onDispose { scope.cancel() }
    }
    return scope
}
