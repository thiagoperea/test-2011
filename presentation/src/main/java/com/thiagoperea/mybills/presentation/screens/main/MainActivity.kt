package com.thiagoperea.mybills.presentation.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thiagoperea.mybills.presentation.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // get host fragment and setup as base navigator
        val hostFragment = supportFragmentManager.findFragmentById(R.id.mainHostFragment) as NavHostFragment
        findViewById<BottomNavigationView>(R.id.mainBottomBar).setupWithNavController(hostFragment.navController)
    }
}