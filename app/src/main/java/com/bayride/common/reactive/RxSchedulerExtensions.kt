package com.bayride.common.reactive

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

fun Disposable.addToCompositeDisposable(compositeDisposable: CompositeDisposable) {
  compositeDisposable.add(this)
}

fun <T> Observable<T>.applyIO(): Observable<T> {
  return subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.applyComputation(): Observable<T> {
  return subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.toMainThread(): Observable<T> {
  return observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.applyIO(): Single<T> {
  return subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.applyComputation(): Single<T> {
  return subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.toMainThread(): Single<T> {
  return observeOn(AndroidSchedulers.mainThread())
}

fun Completable.applyIO(): Completable {
  return subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.applyComputation(): Completable {
  return subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.toMainThread(): Completable {
  return observeOn(AndroidSchedulers.mainThread())
}
