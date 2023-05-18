package com.example.beproducktive.ui.timer

import android.app.Dialog
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.FragmentTimerBinding
import com.example.beproducktive.databinding.TimerAddDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerFragment : Fragment(R.layout.fragment_timer) {

    private var timeSelected: Int = (50 * 60).toInt()
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isTimerRunning = true
    private var pauseCount = 0

    private var mediaPlayerStart: MediaPlayer? = null
    private var mediaPlayerEnd: MediaPlayer? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the MediaPlayers in your onCreate or initialization method
        mediaPlayerStart = MediaPlayer.create(requireContext(), R.raw.tick_tick)
        mediaPlayerEnd = MediaPlayer.create(requireContext(), R.raw.ring)

        val binding = FragmentTimerBinding.bind(view)

        binding.apply {

            val minutes = timeSelected / 60
            val seconds = timeSelected % 60
            tvTimeLeft.text = String.format("%02d:%02d", minutes, seconds)

//            val addBtn: ImageButton = btnAdd
//            addBtn.setOnClickListener {
//                setTimeFunction(binding)
//            }

            val startBtn: Button = btnPlayPause
            startBtn.setOnClickListener {
                startTimerSetup(binding)
            }

            val resetBtn: ImageButton = btnReset
            resetBtn.setOnClickListener {
                resetTime(binding)
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


    private fun resetTime(binding: FragmentTimerBinding) {
        if (timeCountDown != null) {
            timeCountDown!!.cancel()
            timeProgress = 0
            timeSelected = 50 * 60
            pauseOffSet = 0
            timeCountDown = null

            binding.apply {
                val startBnt: Button = btnPlayPause
                startBnt.text = "Start"
                isTimerRunning = true
                val progressBar = pbTimer
                progressBar.progress = timeSelected
                val timeLeftTv = tvTimeLeft
                val minutes = timeSelected / 60
                val seconds = timeSelected % 60
                timeLeftTv.text = String.format("%02d:%02d", minutes, seconds)

            }
        }
    }


    private fun timePause() {
        if (timeCountDown != null) {
            timeCountDown!!.cancel()
        }
    }

    /*
    private fun setTimeFunction(binding: FragmentTimerBinding) {
        val timeDialog = Dialog(requireContext())
        timeDialog.setContentView(R.layout.timer_add_dialog)
        val timeSet = timeDialog.findViewById<EditText>(R.id.et_get_time)


        binding.apply {
            val timeLeftTv = tvTimeLeft
            val btnStart = btnPlayPause
            val progressBar = pbTimer

            timeDialog.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                if (timeSet.text.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter time duration",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    resetTime(binding)
                    btnStart.text = "Start"

                    try {
                        timeSelected = timeSet.text.toString().toInt() * 60
                        val timeSelectedInSeconds = timeSelected
                        val timeSelectedInMinutes = timeSelectedInSeconds / 60
                        val timeSelectedFormattedString = String.format("%02d:%02d", timeSelectedInMinutes, timeSelectedInSeconds % 60)
                        timeLeftTv.text = timeSelectedFormattedString
                        progressBar.max = timeSelected
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Please enter a valid number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                timeDialog.dismiss()
            }

        }
        timeDialog.show()
    }
*/


    private fun startTimerSetup(binding: FragmentTimerBinding) {

        binding.apply {

            val startBtn: Button = btnPlayPause
            if (timeSelected > timeProgress) {
                if (isTimerRunning) {
                    startBtn.text = "Pause"
                    startTimer(pauseOffSet, binding)
                    isTimerRunning = false
                } else {
                    startBtn.text = "Resume"
                    isTimerRunning = true
                    timePause()
                }
            } else {
                Toast.makeText(requireContext(), "Please set time", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun startTimer(pauseOffsetL: Long, binding: FragmentTimerBinding) {
        binding.apply {
            val progessBar = pbTimer
            progessBar.progress = timeProgress
            timeCountDown = object : CountDownTimer(
                timeSelected * 1000L - pauseOffsetL * 1000,
                1000
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    timeProgress++
                    pauseOffSet = timeSelected.toLong() - millisUntilFinished / 1000
                    progessBar.progress = timeSelected - timeProgress
                    val timeLeftInSeconds = (timeSelected - timeProgress)
                    val timeLeftInMinutes = timeLeftInSeconds / 60
                    val timeLeftInFormattedString =
                        String.format("%02d:%02d", timeLeftInMinutes, timeLeftInSeconds % 60)
                    tvTimeLeft.text = timeLeftInFormattedString
                }

                override fun onFinish() {

//                    resetTime(binding)
                    if (pauseCount != 0) {
                        resetTime(binding)
                        pauseCount = 0
                    } else {
                        startPause(binding)
                    }
                    Toast.makeText(requireContext(), "Times Up!", Toast.LENGTH_SHORT).show()
                }
            }.start()

            // Play the start sound
            mediaPlayerStart?.start()

        }
    }

    private fun startPause(binding: FragmentTimerBinding) {
        // When the timer finishes
        mediaPlayerEnd?.start()

        binding.apply {
            setTimer((10 * 60).toInt(), binding)
            startTimer(pauseOffSet, binding)
            pauseCount++
        }
    }

    private fun setTimer(time: Int, binding: FragmentTimerBinding) {
        binding.apply {
            timeSelected = time
            val timeLeftTv = tvTimeLeft
            val btnStart = btnPlayPause
            val progressBar = pbTimer

            btnStart.text = "Resume"

            val timeSelectedInSeconds = timeSelected
            val timeSelectedInMinutes = timeSelectedInSeconds / 60
            val timeSelectedFormattedString =
                String.format("%02d:%02d", timeSelectedInMinutes, timeSelectedInSeconds % 60)
            timeLeftTv.text = timeSelectedFormattedString
            progressBar.max = timeSelected
            timeProgress = 0
            pauseOffSet = 0
            timeCountDown = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (timeCountDown != null) {
            timeCountDown?.cancel()
            timeProgress = 0
        }

        mediaPlayerStart?.setOnCompletionListener { mp ->
            // Perform any necessary actions after the sound finishes playing
            mp.release()
        }

        mediaPlayerEnd?.setOnCompletionListener { mp ->
            // Perform any necessary actions after the sound finishes playing
            mp.release()
        }


    }

}

