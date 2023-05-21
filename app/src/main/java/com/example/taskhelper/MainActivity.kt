package com.example.taskhelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.taskhelper.databinding.ActivityMainBinding
import com.example.taskhelper.ui.home.HomeScreen
import com.example.taskhelper.ui.onBoarding.OnBoardingScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val isFirstRun = getSharedPreferences("SharedPreference", Context.MODE_PRIVATE).getBoolean(
            "isFirstRun",
            true
        )

        if (isFirstRun) {

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@MainActivity,OnBoardingScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("ToastMessage", 101)
                startActivity(intent)
                finish()
            }, 2000)

        } else {

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@MainActivity,HomeScreen::class.java)
                intent.putExtra("ToastMessage", 101)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("ToastMessage", 101)
                startActivity(intent)
                finish()
            }, 2000)



        }

        getSharedPreferences("SharedPreference", Context.MODE_PRIVATE).edit()
            .putBoolean("isFirstRun", false).apply()

    }





}