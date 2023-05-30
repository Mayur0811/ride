package com.bayride

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.bayride.databinding.ActivityMainBinding
import com.bayride.databinding.BookingHistoryItemBinding
import dagger.hilt.android.AndroidEntryPoint
import com.bayride.presentation.base.BaseActivity

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)


    }
}