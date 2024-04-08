package spl.cards.app.util

import spl.cards.app.util.Constants.ResultStatus

sealed class Result<T> {

    companion object {

        fun <T> notSet() = NotSet<T>(status = ResultStatus.NOT_SET)
        fun <T> success(data: T) = Success(status = ResultStatus.SUCCESS, data = data)
        fun <T> failure(status: ResultStatus, message: String? = null, data: T? = null) = Failure(status = status, message = message, data = data)
    }

    abstract val status: ResultStatus

    data class NotSet<T>(override val status: ResultStatus) : Result<T>()
    data class Success<T>(override val status: ResultStatus, val data: T) : Result<T>()
    data class Failure<T>(override val status: ResultStatus, val message: String? = null, val data: T? = null) : Result<T>()

    fun handleResult(success: (data: T) -> Unit, failure: (status: ResultStatus) -> Unit) {
        when (this) {
            is Success -> success(data)
            is Failure -> failure(status)
            else       -> {
            }
        }
    }

    fun isNotSet(): NotSet<T>? = if (this is NotSet) {
        this
    } else {
        null
    }

    fun isSuccess(): T? = if (this is Success) {
        data
    } else {
        null
    }

    fun isFailure(): Failure<T>? = if (this is Failure) {
        this
    } else {
        null
    }
}
