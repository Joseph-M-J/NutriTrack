package com.example.nutritrack.util

sealed class RemoteResource<T> {
    class Loading<T>: RemoteResource<T>()
    data class Success<T>(val data: T): RemoteResource<T>()
    data class Error<T>(val message: String): RemoteResource<T>()
}