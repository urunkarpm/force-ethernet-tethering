package com.example.forcethernet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.forcethernet.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
