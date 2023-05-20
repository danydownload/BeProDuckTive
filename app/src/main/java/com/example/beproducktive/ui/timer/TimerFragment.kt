package com.example.beproducktive.ui.timer

import android.content.*
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerFragment : Fragment(R.layout.fragment_timer) {

    private var timeLeft: String? = null
    private var binding: FragmentTimerBinding? = null
    private var isTimerRunning = true


    private val timeUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.ACTION_TIMER_TICK) {
                timeLeft = intent.getStringExtra(TimerService.EXTRA_TIME_REMAINING)
                // Find the TimerFragment and update the UI with the received time
                binding?.apply{
                    tvTimeLeft.text = timeLeft
                    btnPlayPause.text = "Pause"
                }
            }
        }
    }

    private val isTimerRunningReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.TIME_RUNNING) {
                isTimerRunning = intent.getBooleanExtra(TimerService.TIME_RUNNING, true)
//                Log.d("Timer_cd ", "IsTimerRunning UPDATED: $isTimerRunning")
                // Find the TimerFragment and update the UI with the received time
                binding?.apply{
                    tvTimeLeft.text = timeLeft
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTimerBinding.bind(view)

        binding?.apply {

            tvTimeLeft.text = timeLeft
            btnPlayPause.text = "Start"

            val startBtn: Button = btnPlayPause
            startBtn.setOnClickListener {
    //                startTimerSetup(binding)

                Log.d("Timer_cd","is timer running: $isTimerRunning")

                if (isTimerRunning) {
                    Log.d("Timer_cd", "CALLED IS TIMER RUNNING")
                    startBtn.text = "Pause"
                    startTimerIntent()
                    isTimerRunning = false
                } else {
                    Log.d("Timer_cd", "CALLED IS TIMER NOT RUNNING")
                    startBtn.text = "Resume"
                    pauseTimerIntent()
                    isTimerRunning = true
                }

            }

            val resetBtn: ImageButton = btnReset
            resetBtn.setOnClickListener {
    //                resetTime(binding)
            }

            val bundle = arguments
            if (bundle != null) {
                val args = TimerFragmentArgs.fromBundle(bundle)
                args.task.let { task ->
                    taskItem.apply {
                        imageViewTimer.isVisible = false
                        textviewTaskTitle.text = task.taskTitle
                        textViewDescription.text = task.description
                        deadline.text = task.deadlineFormatted
                        checkboxCompleted.isChecked = task.completed

                        priority.text = requireContext().resources.getString(
                            R.string.priority_value,
                            task.priority.name
                        )
                        priority.setTypeface(null, Typeface.BOLD)

                        val colorArray = requireContext().resources.getIntArray(R.array.colors)

                        val textColor = when (task.priority) {
                            TaskPriority.HIGH -> R.color.red
                            TaskPriority.MEDIUM -> R.color.green
                            TaskPriority.LOW -> R.color.resolution_blue
                        }
                        val intColor = ContextCompat.getColor(requireContext(), textColor)
                        priority.setTextColor(intColor)
                        deadline.setTextColor(intColor)

                        val cardColor = when (task.priority) {
                            TaskPriority.HIGH -> R.color.pale_pink
                            TaskPriority.MEDIUM -> R.color.light_goldenrod_yellow
                            TaskPriority.LOW -> R.color.water
                        }

                        val viewColor = when (task.priority) {
                            TaskPriority.HIGH -> R.color.misty_rose
                            TaskPriority.MEDIUM -> R.color.fawn
                            TaskPriority.LOW -> R.color.baby_blue
                        }

                        viewLine.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                viewColor
                            )
                        )

                        cardView.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                cardColor
                            )
                        )
                    }
                }
            }
        }
    }

    private fun startTimerIntent()
    {
        val serviceIntent = Intent(requireContext(), TimerService::class.java).apply {
            action = "START_TIMER"
        }
        Log.d("Timer_cd", "startTimerIntent called")
        requireContext().startService(serviceIntent)
    }

    private fun pauseTimerIntent() {
        val serviceIntent = Intent(requireContext(), TimerService::class.java).apply {
            action = "PAUSE_TIMER"
        }
        Log.d("Timer_cd", "pauseTimerIntent called")
        requireContext().startService(serviceIntent)
    }



    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            timeUpdateReceiver,
            IntentFilter(TimerService.ACTION_TIMER_TICK).apply {
                addAction(TimerService.ACTION_TIMER_FINISH)
            })

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            isTimerRunningReceiver,
            IntentFilter(TimerService.TIME_RUNNING))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(timeUpdateReceiver)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(isTimerRunningReceiver)
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            timeUpdateReceiver,
            IntentFilter(TimerService.ACTION_TIMER_TICK).apply {
                addAction(TimerService.ACTION_TIMER_FINISH)
            })

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            isTimerRunningReceiver,
            IntentFilter(TimerService.TIME_RUNNING))

        Log.d("Timer_cd", "onResume called")
        Log.d("Timer_cd", "onResume timeLeft: $timeLeft")

        binding?.apply {
            tvTimeLeft.text = timeLeft ?: "50:00"
        }

    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(timeUpdateReceiver)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(isTimerRunningReceiver)
    }





}


