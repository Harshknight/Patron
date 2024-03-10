package com.example.patron.Post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.patron.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {
    private lateinit var binding :ActivityPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }
    }
}