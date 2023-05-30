package com.bayride.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bayride.common.reactive.addToCompositeDisposable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import com.bayride.common.reactive.SingleLiveEvent

abstract class BaseViewModel<S : Any, E> : ViewModel() {
  val store by lazy {
    ViewStateStore(this.initState())
  }

  val currentState: S
    get() = store.state

  private val _liveEvent: SingleLiveEvent<E> = SingleLiveEvent()
  val liveEvent: LiveData<E>
    get() = _liveEvent

  private val compositeDisposable = CompositeDisposable()

  abstract fun initState(): S

  override fun onCleared() {
    compositeDisposable.clear()
    super.onCleared()
  }

  fun Disposable.addToCompositeDisposable() {
    addToCompositeDisposable(compositeDisposable)
  }

  protected fun dispatchEvent(event: E) {
    _liveEvent.value = event
//    Log.d("LiveEvent:", "${_liveEvent.value}")
  }

  protected fun dispatchState(state: S) {
    store.dispatchState(state = state)
  }
}