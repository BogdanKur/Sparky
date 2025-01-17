package com.example.sparky.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.example.sparky.databinding.FragmentMainBinding
import com.example.sparky.utils.doOnApplyWindowInsets

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
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
        binding.root.doOnApplyWindowInsets { view, windowInsetsCompat, rect ->
            view.updatePadding(
                top = rect.top + windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
            )
            windowInsetsCompat
        }
        binding.ivSettings.doOnApplyWindowInsets { view, windowInsetsCompat, rect ->
            view.updatePadding(
                bottom = rect.bottom + windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).bottom,
            )
            windowInsetsCompat
        }
        binding.progressBar.progress = 75
    }

}