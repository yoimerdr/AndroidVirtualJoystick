package com.yoimerdr.android.virtualjoystick

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yoimerdr.android.virtualjoystick.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val tvDirection: TextView get() = binding.tvDirection
    private val tvPosition: TextView get() = binding.tvPosition
    private val tvAngle: TextView get() = binding.tvAngle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

    }

}