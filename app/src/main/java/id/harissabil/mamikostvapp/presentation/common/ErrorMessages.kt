package id.harissabil.mamikostvapp.presentation.common

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserMessage(): String = when (this) {
    is UnknownHostException, is ConnectException ->
        "You appear to be offline. Check your connection and try again."

    is SocketTimeoutException ->
        "The server took too long to respond. Please try again."

    is HttpException -> when (code()) {
        404 -> "We couldn't find that show."
        429 -> "Too many requests. Wait a moment and try again."
        in 500..599 -> "TVMaze is having problems right now. Please try again shortly."
        else -> "The request failed (HTTP ${code()}). Please try again."
    }

    is IOException ->
        "Something went wrong with the network. Check your connection and try again."

    else -> "Something went wrong. Please try again."
}
