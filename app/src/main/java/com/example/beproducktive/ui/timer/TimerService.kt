package com.example.beproducktive.ui.timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.ui.MainActivity
import com.example.beproducktive.ui.tasks.TasksFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerService : Service() {

    private var mediaPlayerStart: MediaPlayer? = null
    private var mediaPlayerEnd: MediaPlayer? = null

    private var timeCountDown: CountDownTimer? = null
    private var timeSelected: Int = (0.5 * 60).toInt()
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isTimerRunning = true
    private var isTimeStarted = false
    private var pauseCount = 0

    private var referencedTaskId = -1
    private var referencedTask: Task? = null

    var formattedTimeForNotification = "50:00"

    override fun onCreate() {
        super.onCreate()

        mediaPlayerStart = MediaPlayer.create(this, R.raw.tick_tick)
        mediaPlayerEnd = MediaPlayer.create(this, R.raw.ring)
    }

    override fun onBind(intent: Intent): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
//            Log.d("Timer_cd ", "onStartCommand: ${it.action}")
            when (it.action) {
                "START_TIMER" -> {
                    intent.extras?.let { bundle ->
                        referencedTaskId = bundle.getInt("taskId")
                        referencedTask = bundle.getParcelable("task")
//                        Log.d("Timer_cd ", "referencedTask: ${referencedTask!!.taskId}")
//                        Log.d("Timer_cd ", "onStartCommand: taskId: $referencedTaskId")
                    }
                    startTimer()
                }
                "PAUSE_TIMER" -> pauseTimer()
                "RESET_TIMER" -> {
                    resetTimer()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    else
                        stopForeground(true)
                }
                "DISMISS" -> {
                    resetTimer()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    } else {
                        stopForeground(true)
                    }
                    stopSelf()
                }
            }
        }
//        return super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }


    private fun createNotification(timeLeftInFormattedString: String, text: String = "Timer is running"): Notification {

        val intent = Intent(this, MainActivity::class.java).apply {
            action = "TIMER_FRAGMENT"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", referencedTaskId)
            putExtra("task", referencedTask)
        }


        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BeProductive")
            .setContentText("$text: $timeLeftInFormattedString")
            .setSmallIcon(R.drawable.ic_action_checklist_notif)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false) // Allow the notification to be dismissed
            .addAction(
                R.drawable.ic_action_notification_off, "Dismiss",
                getDismissPendingIntent(this)
            )
            .build()
    }

    private fun getDismissPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, TimerService::class.java)
        intent.action = "DISMISS"
        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = createNotification(formattedTimeForNotification)
        startForeground(NOTIFICATION_ID, notification)
    }


    private fun pauseTimer() {
        timeCountDown?.cancel()
        isTimerRunning = true
        sendTimerRunningBroadcast(isTimerRunning)
    }


    private fun startTimer() {
        startForegroundService()

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

                formattedTimeForNotification = timeLeftInFormattedString

                sendTimerTickBroadcast(timeLeftInFormattedString)

                // Update the notification
                val notification = createNotification(timeLeftInFormattedString)
                startForeground(NOTIFICATION_ID, notification)

                isTimerRunning = false
                sendTimerRunningBroadcast(isTimerRunning)

                isTimeStarted = true
                sendTimerIsStartedBroadcast(isTimeStarted)
            }

            override fun onFinish() {
                if (pauseCount != 0) {
                    resetTimer()
                    pauseCount = 0
                    sendPauseIsStartedBroadcast(false)
                } else {
                    startPause()
                    pauseCount++
                    sendPauseIsStartedBroadcast(true)
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
        pauseCount = 0
        isTimerRunning = true
        isTimeStarted = false

        timeCountDown?.cancel()
        timeCountDown = null

        timeSelected = (0.5 * 60).toInt()
        val timeLeftInFormattedString = "50:00"
        sendTimerTickBroadcast(timeLeftInFormattedString)
        sendTimerRunningBroadcast(isTimerRunning)
        sendTimerIsStartedBroadcast(isTimeStarted)
        sendPauseIsStartedBroadcast(false)


    }

    private fun startPause() {

        timeSelected = (0.2 * 60).toInt()

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

                // Update the notification
                val notification = createNotification(timeLeftInFormattedString, "Pause is running")
                startForeground(NOTIFICATION_ID, notification)

            }

            override fun onFinish() {
                mediaPlayerEnd?.start()
                pauseCount = 0
                resetTimer()
                sendTimerFinishBroadcast()

                stopSelf()

                // navigate to MainActivity with intent action TIMER_TASK_FINISHED
                val intent = Intent(this@TimerService, MainActivity::class.java).apply {
                    action = "TIMER_TASK_FINISHED"
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("taskId", referencedTaskId)
                    putExtra("task", referencedTask)
                }
                startActivity(intent)
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

    private fun sendPauseIsStartedBroadcast(isPauseStarted: Boolean) {
        val intent = Intent(ACTION_PAUSE_STARTED)
        intent.putExtra(PAUSE_STARTED, isPauseStarted)
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
        const val ACTION_PAUSE_STARTED = "PAUSE_STARTED"
        const val TIME_RUNNING = "time_running"
        const val TIMER_STARTED = "timer_started"
        const val PAUSE_STARTED = "pause_started"
    }
}
