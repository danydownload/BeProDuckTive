package com.example.beproducktive.ui.timer

import android.content.*
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerFragment : Fragment(R.layout.fragment_timer) {

    private var timeLeft: String? = "50:00"
    private var binding: FragmentTimerBinding? = null
    private var progressBar: ProgressBar? = null


    private var isTimerRunning = true
    private var isTimerStarted = false

    private var taskId = -1
    private var mTask : Task? = null

    private val sharedViewModel: TimerSharedViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTimerBinding.bind(view)

//        Log.d("ViewModel", "Fragment ViewModel: $sharedViewModel")


        binding?.apply {
            tvTimeLeft.text = timeLeft

            progressBar = binding?.pbTimer


            sharedViewModel.timeLeft.observe(viewLifecycleOwner) { it_timeLeft ->

                val totalTimeInSeconds: Int = if (sharedViewModel.isPauseStarted.value == true) {
                    18
                } else {
                    30
                }

                // Split the time left into minutes and seconds
                val timeLeftSplit = it_timeLeft.split(":")
                val minutes = timeLeftSplit[0].toInt()
                val seconds = timeLeftSplit[1].toInt()

                // Calculate the remaining time in seconds
                val remainingTimeInSeconds = minutes * 60 + seconds

                // Update the progress bar with the remaining time in seconds
                progressBar?.max = totalTimeInSeconds
                progressBar?.progress = remainingTimeInSeconds

                tvTimeLeft.text = it_timeLeft
            }


            sharedViewModel.isTimerStarted.observe(viewLifecycleOwner) { it_isTimerStarted ->
                isTimerStarted = it_isTimerStarted
                if (!isTimerStarted) {
                    btnPlayPause.text = "Start"
                }
            }

            sharedViewModel.isTimerRunning.observe(viewLifecycleOwner) { it_isTimerRunning ->
                isTimerRunning = it_isTimerRunning

                if (isTimerRunning) {
                    btnPlayPause.text = "Resume"
                } else {
                    btnPlayPause.text = "Pause"
                }
            }


            val startBtn: Button = btnPlayPause
            startBtn.setOnClickListener {
//                Log.d("TaskId", "TimerFragment: TaskId: $taskId")

                // Do the opposite of what isTimerRunning is, beacuse the button is showing the next action
                if (isTimerRunning) {
                    startBtn.text = "Pause"
                    startTimerIntent()
                } else {
                    startBtn.text = "Resume"
                    pauseTimerIntent()
                }
            }


            val resetBtn: ImageButton = btnReset
            resetBtn.setOnClickListener {
                resetTimerIntent()
            }

            val bundle = arguments
            if (bundle != null) {
                val args = TimerFragmentArgs.fromBundle(bundle)
                args.task.let { task ->
                    taskId = task.taskId
                    mTask = task

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

                        checkboxCompleted.isClickable = false

                    }
                }
            }
        }
    }

    private fun resetTimerIntent() {
        val serviceIntent = Intent(requireContext(), TimerService::class.java).apply {
            action = "RESET_TIMER"
        }
//        Log.d("Timer_cd", "resetTimerIntent called")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }

    private fun startTimerIntent() {

        val serviceIntent = Intent(requireContext(), TimerService::class.java).apply {
            action = "START_TIMER"
            putExtra("task", mTask)
        }
//        Log.d("Timer_cd", "startTimerIntent called")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }
    }

    private fun pauseTimerIntent() {
        val serviceIntent = Intent(requireContext(), TimerService::class.java).apply {
            action = "PAUSE_TIMER"
        }
//        Log.d("Timer_cd", "pauseTimerIntent called")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(serviceIntent)
        } else {
            requireContext().startService(serviceIntent)
        }

    }


    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onPause() {
        super.onPause()
    }


}


