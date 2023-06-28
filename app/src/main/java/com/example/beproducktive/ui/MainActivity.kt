package com.example.beproducktive.ui


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.ui.tasks.TasksFragmentDirections
import com.example.beproducktive.ui.timer.TimerFragmentDirections
import com.example.beproducktive.ui.timer.TimerService
import com.example.beproducktive.ui.timer.TimerSharedViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout


    private val sharedViewModel: TimerSharedViewModel by viewModels()

    private lateinit var requestLauncher: ActivityResultLauncher<String>



    private val timeUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.ACTION_TIMER_TICK) {
                val timeLeft = intent.getStringExtra(TimerService.EXTRA_TIME_REMAINING)
                sharedViewModel.updateTimeLeft(timeLeft!!)
//                Log.d("Timer_cd ", "MainActivity: TimeLeft UPDATED: $timeLeft")
            }
        }
    }

    private val isTimerRunningReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.TIME_RUNNING) {
                val isTimerRunning = intent.getBooleanExtra(TimerService.TIME_RUNNING, true)
//                Log.d("Timer_cd ", "MainActivity: isTimerRunning UPDATED: $isTimerRunning")
                sharedViewModel.updateIsTimerRunning(isTimerRunning)
            }
        }
    }

    private var isTimerStartedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.ACTION_TIMER_STARTED) {
                val isTimeStarted = intent.getBooleanExtra(TimerService.TIMER_STARTED, false)
//                Log.d("Timer_cd ", "MainActivity: isTimeStarted UPDATED: $isTimeStarted")
                sharedViewModel.updateIsTimerStarted(isTimeStarted)
            }
        }
    }

    private var isPausedStartedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.ACTION_PAUSE_STARTED) {
                val isTimePaused = intent.getBooleanExtra(TimerService.PAUSE_STARTED, false)
                sharedViewModel.updateIsPauseStarted(isTimePaused)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAppNotificationSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // inizialize request launcher
        requestLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.d("MainActivity-2", "onCreate: Notification permission granted")
                } else {
                    Log.d("MainActivity-2", "onCreate: Notification permission not granted")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        showGrantNotificationDialog()
                    }
                }
            }

        // check if notification permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity-2", "onCreate: Notification permission granted")
        } else {
            Log.d("MainActivity-2", "onCreate: Notification permission not granted")
            // if androir 13
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showGrantNotificationDialog()
            }
        }


        if (intent?.action == "TIMER_FRAGMENT") {
            val task = intent.getParcelableExtra<Task>("task")
//            val task = intent.getBundleExtra("task")
            if (task != null) {
//                navigateToTimerFragment(task)
                sharedViewModel.updateTask(task)
                sharedViewModel.updateDestination("TIMER FRAGMENT")
            } else {
                Log.d("TaskId ", "MainActivity: Task object is null")
            }
        }


        // same as below but with TIMER_TASK_FINISHED
        if (intent?.action == "TIMER_TASK_FINISHED") {
            val task = intent.getParcelableExtra<Task>("task")
            if (task != null) {
                sharedViewModel.updateTaskCompleted(task)
            } else {
                Log.d("TaskId ", "MainActivity: Task object is null")
            }
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.blue_gray))

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav_drawer,
            R.string.close_nav_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Create an instance of OnBackPressedCallback and attach it to the activity's OnBackPressedDispatcher
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else if (findNavController(R.id.nav_host_fragment).popBackStack().not()) {
                    finish()
                }
            }
        }
        // Add the callback to the activity's OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showGrantNotificationDialog() {

        // create dialog
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Notification Policy Permission")
        dialogBuilder.setMessage("This app requires notification policy permission to function properly.")
        dialogBuilder.setPositiveButton("Grant Permission") { dialog, which ->
            openAppNotificationSettings()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            // Handle user cancellation
        }
        dialogBuilder.setCancelable(false)
        val dialog = dialogBuilder.create()
        dialog.show()

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_daily_view -> findNavController(R.id.nav_host_fragment).navigate(R.id.dailyViewTasksFragment)
            R.id.nav_tasks_view -> findNavController(R.id.nav_host_fragment).navigate(R.id.tasksFragment)
            R.id.nav_projects_view -> findNavController(R.id.nav_host_fragment).navigate(R.id.projectsFragment)
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
            }

            R.id.nav_statistics_view -> findNavController(R.id.nav_host_fragment).navigate(R.id.chartsFragment)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            timeUpdateReceiver,
            IntentFilter(TimerService.ACTION_TIMER_TICK).apply {
                addAction(TimerService.ACTION_TIMER_FINISH)
            })

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isTimerRunningReceiver,
            IntentFilter(TimerService.TIME_RUNNING)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isTimerStartedReceiver,
            IntentFilter(TimerService.ACTION_TIMER_STARTED)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isPausedStartedReceiver,
            IntentFilter(TimerService.ACTION_PAUSE_STARTED)
        )

    }

    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            timeUpdateReceiver,
            IntentFilter(TimerService.ACTION_TIMER_TICK).apply {
                addAction(TimerService.ACTION_TIMER_FINISH)
            })

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isTimerRunningReceiver,
            IntentFilter(TimerService.TIME_RUNNING)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isTimerStartedReceiver,
            IntentFilter(TimerService.ACTION_TIMER_STARTED)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isPausedStartedReceiver,
            IntentFilter(TimerService.ACTION_PAUSE_STARTED)
        )

    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeUpdateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerRunningReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerStartedReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isPausedStartedReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeUpdateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerRunningReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerStartedReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isPausedStartedReceiver)
    }

}

const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
