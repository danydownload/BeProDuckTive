package com.example.beproducktive.ui.timer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimerSharedViewModel @Inject constructor() : ViewModel() {

    private val _timeLeft = MutableLiveData<String>()
    val timeLeft: LiveData<String> = _timeLeft

    private val _isTimerRunning = MutableLiveData<Boolean>()
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    private val _isTimerStarted = MutableLiveData<Boolean>()
    val isTimerStarted: LiveData<Boolean> = _isTimerStarted



    fun updateTimeLeft(time: String) {
//        Log.d("Timer_cd ", "ViewModel: TimeLeft UPDATED: $time")
        _timeLeft.value = time
    }

    fun updateIsTimerRunning(isRunning: Boolean) {
//        Log.d("Timer_cd ", "ViewModel: IsTimerRunning UPDATED: $isRunning")
        _isTimerRunning.value = isRunning
    }

    fun updateIsTimerStarted(isStarted: Boolean) {
//        Log.d("Timer_cd ", "ViewModel: IsTimerStarted UPDATED: $isStarted")
        _isTimerStarted.value = isStarted
    }




}