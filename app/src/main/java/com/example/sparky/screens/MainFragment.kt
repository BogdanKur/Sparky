package com.example.sparky.screens

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.example.sparky.R
import com.example.sparky.databinding.FragmentMainBinding
import com.example.sparky.utils.doOnApplyWindowInsets
import android.os.Handler
import android.os.Looper

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        activity?.window?.statusBarColor = Color.parseColor("#444444")
        binding.root.doOnApplyWindowInsets { view, windowInsetsCompat, rect ->
            view.updatePadding(
                top = rect.top + windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
            )
            windowInsetsCompat
        }
        binding.settingLayout.doOnApplyWindowInsets { view, windowInsetsCompat, rect ->
            view.updatePadding(
                bottom = rect.bottom + windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).bottom,
            )
            windowInsetsCompat
        }

        binding.progressBar.progress = 75
        handler = Handler(Looper.getMainLooper())
        binding.ivSearch.setOnClickListener {
            binding.ivSearch.visibility = View.GONE
            binding.etSearch.visibility = View.VISIBLE
            binding.ivSearchIcon.visibility = View.VISIBLE
        }
        binding.ivBalls.setOnClickListener {
            binding.ivBalls.setBackgroundResource(R.drawable.bottom_switch_choose)
            binding.ivSetting.setBackgroundResource(R.drawable.bottom_end_switch)
            binding.ivChat.setBackgroundColor(Color.parseColor("#444444"))
            binding.ivVibe.setBackgroundColor(Color.parseColor("#444444"))
        }

        binding.ivSetting.setOnClickListener {
            binding.ivBalls.setBackgroundResource(R.drawable.bottom_switch)
            binding.ivSetting.setBackgroundResource(R.drawable.bottom_end_switch_choose)
            binding.ivChat.setBackgroundColor(Color.parseColor("#444444"))
            binding.ivVibe.setBackgroundColor(Color.parseColor("#444444"))
        }

        binding.ivChat.setOnClickListener {
            binding.ivBalls.setBackgroundResource(R.drawable.bottom_switch)
            binding.ivSetting.setBackgroundResource(R.drawable.bottom_end_switch)
            binding.ivChat.setBackgroundColor(Color.parseColor("#0077DD"))
            binding.ivVibe.setBackgroundColor(Color.parseColor("#444444"))
        }

        binding.ivVibe.setOnClickListener {
            binding.ivBalls.setBackgroundResource(R.drawable.bottom_switch)
            binding.ivSetting.setBackgroundResource(R.drawable.bottom_end_switch)
            binding.ivChat.setBackgroundColor(Color.parseColor("#444444"))
            binding.ivVibe.setBackgroundColor(Color.parseColor("#0077DD"))
        }
    }
}
