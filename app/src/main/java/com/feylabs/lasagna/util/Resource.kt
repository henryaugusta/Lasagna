package com.feylabs.lasagna.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Null<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    class Success<T>(data: T,message: String?=null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}