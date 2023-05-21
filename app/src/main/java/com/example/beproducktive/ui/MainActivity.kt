package com.example.beproducktive.ui


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.ui.tasks.TasksFragmentDirections
import com.example.beproducktive.ui.timer.TimerService
import com.example.beproducktive.ui.timer.TimerSharedViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    private val sharedViewModel: TimerSharedViewModel by viewModels()

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent?.action == "TIMER_FRAGMENT") {
            val task = intent.getParcelableExtra<Task>("task")
//            val task = intent.getBundleExtra("task")
            if (task != null) {
                Log.d("TaskId ", "MainActivity: task: ${task.taskId}")
                navigateToTimerFragment(task)
            } else {
                Log.d("TaskId ", "MainActivity: Task object is null")
                // Handle the case when the "task" extra is null
            }
        }

//        Log.d("ViewModel", "MainActivity ViewModel: $sharedViewModel")



        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.blue_gray))

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_drawer, R.string.close_nav_drawer)
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

    private fun navigateToTimerFragment(task: Task) {
        val bundle = bundleOf("task" to task)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.timerFragment, bundle)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_daily_view -> findNavController(R.id.nav_host_fragment).navigate(R.id.dailyViewTasksFragment)
            R.id.nav_tasks_view -> findNavController(R.id.nav_host_fragment).navigate(R.id.tasksFragment)
            R.id.nav_projects_view -> findNavController(R.id.nav_host_fragment).navigate(R.id.projectsFragment)
            R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
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
            IntentFilter(TimerService.TIME_RUNNING))

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isTimerStartedReceiver,
            IntentFilter(TimerService.ACTION_TIMER_STARTED))
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
            IntentFilter(TimerService.TIME_RUNNING))

        LocalBroadcastManager.getInstance(this).registerReceiver(
            isTimerStartedReceiver,
            IntentFilter(TimerService.ACTION_TIMER_STARTED))
    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeUpdateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerRunningReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerStartedReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeUpdateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerRunningReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(isTimerStartedReceiver)
    }



}