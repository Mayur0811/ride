package com.bayride.common.reactive

import io.reactivex.rxjava3.core.CompletableEmitter
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.SingleEmitter

fun <T> ObservableEmitter<T>.checkDisposed(): ObservableEmitter<T>? {
    return if (isDisposed) {
        null
    } else {
        this
    }
}

fun <T> SingleEmitter<T>.checkDisposed(): SingleEmitter<T>? {
    return if (isDisposed) {
        null
    } else {
        this
    }
}

fun <T> CompletableEmitter.checkDisposed(): CompletableEmitter? {
    return if (isDisposed) {
        null
    } else {
        this
    }
}
