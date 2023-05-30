package com.bayride.common.reactive

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function

/**
 * The operator map each element of current [Observable] into [Single], then perform each [Single]
 * in sequence, and take just the first one success.
 * If don't have any [Single] success, it will return error [NoSuchElementException].
 *
 * @param T the source value type.
 * @param R the result value type.
 * @param mapper [Function] the function that received each source [T] value and transforms them
 * into [Single] with result value type [R].
 * @return [Single] with result value type [R]. The first one success [Single] or error [NoSuchElementException].
 */
fun <T, R> Observable<T>.flatMapSingleUntilSuccess(mapper: Function<T, Single<R>>): Single<R> {
  return flatMapMaybe { item ->
    mapper.apply(item)
      .flatMapMaybe { result -> Maybe.just(result) }
      .onErrorResumeNext { Maybe.empty() }
  }
    .take(1)
    .singleOrError()
}