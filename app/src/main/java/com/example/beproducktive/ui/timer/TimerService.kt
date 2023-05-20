package com.example.beproducktive.ui.timer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.beproducktive.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerService : Service() {

    private var mediaPlayerStart: MediaPlayer? = null
    private var mediaPlayerEnd: MediaPlayer? = null

    private var timeCountDown: CountDownTimer? = null
    private var timeSelected: Int = (0.10 * 60).toInt()
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isTimerRunning = true
    private var isTimeStarted = false
    private var pauseCount = 0

    override fun onCreate() {
        super.onCreate()

        mediaPlayerStart = MediaPlayer.create(this, R.raw.tick_tick)
        mediaPlayerEnd = MediaPlayer.create(this, R.raw.ring)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Timer_cd ", "onStartCommand: ")

        intent?.let {
            Log.d("Timer_cd ", "onStartCommand: ${it.action}")
            when (it.action) {
                "START_TIMER" -> startTimer()
                "PAUSE_TIMER" -> pauseTimer()
                "RESET_TIMER" -> resetTimer()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseTimer() {
        timeCountDown?.cancel()
        isTimerRunning = true
        sendTimerRunningBroadcast(isTimerRunning)
    }

    private fun startTimer() {
        timeCountDown = object : CountDownTimer(
            timeSelected * 1000L - pauseOffSet * 1000,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                timeProgress++
                pauseOffSet = timeSelected.toLong() - millisUntilFinished / 1000
                val timeLeftInSeconds = (timeSelected - timeProgress)
                val timeLeftInMinutes = timeLeftInSeconds / 60
                val timeLeftInFormattedString =
                    String.format("%02d:%02d", timeLeftInMinutes, timeLeftInSeconds % 60)

                sendTimerTickBroadcast(timeLeftInFormattedString)

                isTimerRunning = false
                sendTimerRunningBroadcast(isTimerRunning)

                isTimeStarted = true
                sendTimerIsStartedBroadcast(isTimeStarted)
            }

            override fun onFinish() {
                if (pauseCount != 0) {
                    resetTimer()
                    pauseCount = 0
                } else {
                    startPause()
                    pauseCount++
                }

                sendTimerFinishBroadcast()

                // Play the end sound
                mediaPlayerEnd?.start()
            }
        }
        timeCountDown?.start()
        isTimeStarted = true
        sendTimerIsStartedBroadcast(isTimeStarted)
        mediaPlayerStart?.start()
        isTimerRunning = false
        sendTimerRunningBroadcast(isTimerRunning)
    }

    private fun resetTimer() {
        timeProgress = 0
        pauseOffSet = 0
        isTimerRunning = true
        isTimeStarted = false

        timeCountDown?.cancel()
        timeCountDown = null

        timeSelected = (0.10 * 60).toInt()
        val timeLeftInFormattedString = "50:00"
        sendTimerTickBroadcast(timeLeftInFormattedString)
        sendTimerRunningBroadcast(isTimerRunning)
        sendTimerIsStartedBroadcast(isTimeStarted)
    }

    private fun startPause() {
        timeSelected = (1 * 10).toInt()

        timeCountDown?.cancel()
        timeCountDown = object : CountDownTimer(
            timeSelected * 1000L,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeftInSeconds = millisUntilFinished / 1000
                val timeLeftInMinutes = timeLeftInSeconds / 60
                val timeLeftInFormattedString =
                    String.format("%02d:%02d", timeLeftInMinutes, timeLeftInSeconds % 60)

                sendTimerTickBroadcast(timeLeftInFormattedString)
            }

            override fun onFinish() {
                mediaPlayerEnd?.start()
                pauseCount = 0
                resetTimer()
                sendTimerFinishBroadcast()
            }
        }
        timeCountDown?.start()
        isTimeStarted = true
        sendTimerIsStartedBroadcast(isTimeStarted)
//        mediaPlayerStart?.start()
        isTimerRunning = false
        sendTimerRunningBroadcast(isTimerRunning)
    }

    private fun sendTimerTickBroadcast(timeLeftInFormattedString: String) {
        val intent = Intent(ACTION_TIMER_TICK)
        intent.putExtra(EXTRA_TIME_REMAINING, timeLeftInFormattedString)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendTimerFinishBroadcast() {
        val intent = Intent(ACTION_TIMER_FINISH)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendTimerRunningBroadcast(isTimerRunning: Boolean) {
        val intent = Intent(TIME_RUNNING)
        intent.putExtra(TIME_RUNNING, isTimerRunning)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendTimerIsStartedBroadcast(isTimeStarted: Boolean) {
        val intent = Intent(ACTION_TIMER_STARTED)
        intent.putExtra(TIMER_STARTED, isTimeStarted)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    companion object {
        private const val CHANNEL_ID = "TimerServiceChannel"
        private const val CHANNEL_NAME = "Timer Service Channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_TIMER_TICK = "TIMER_TICK"
        const val ACTION_TIMER_FINISH = "TIMER_FINISH"
        const val ACTION_TIMER_STARTED = "TIMER_STARTED"
        const val EXTRA_TIME_REMAINING = "extra_time_remaining"
        const val TIME_RUNNING = "time_running"
        const val TIMER_STARTED = "timer_started"
    }
}
