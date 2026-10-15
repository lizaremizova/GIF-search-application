package com.lizaremizova.gifsearch.core.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    /** Emits `true` while the device has a validated internet connection. */
    val isOnline: Flow<Boolean>
}
