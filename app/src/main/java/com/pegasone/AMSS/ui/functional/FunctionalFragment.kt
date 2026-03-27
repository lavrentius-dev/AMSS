package com.pegasone.AMSS.ui.functional

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.pegasone.AMSS.AMSSApplication
import com.pegasone.AMSS.R
import com.pegasone.AMSS.databinding.FragmentFunctionalBinding

class FunctionalFragment : Fragment() {

    private var _binding: FragmentFunctionalBinding? = null
    private val binding get() = _binding!!

    private val PREFS_NAME = "FunctionalPrefs"
    private val KEY_ACTIVITY = "activity_index"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFunctionalBinding.inflate(inflater, container, false)
        
        // Observe backgroundColor and update background
        AMSSApplication.backgroundColorLiveData.observe(viewLifecycleOwner) { color ->
            binding.root.setBackgroundColor(color)
        }

        // Observe global reset trigger
        AMSSApplication.resetTrigger.observe(viewLifecycleOwner) { shouldReset ->
            if (shouldReset) {
                resetLocalForm()
            }
        }

        setupRadioButtons()
        loadPreferences()
        updateFunctionalScore()

        return binding.root
    }

    private fun setupRadioButtons() {
        binding.rgActivityReduction.setOnCheckedChangeListener { _, _ ->
            updateFunctionalScore()
            savePreferences()
        }
    }

    private fun resetLocalForm() {
        // Reset to first option (index 0)
        if (binding.rgActivityReduction.childCount > 0) {
            val radioButton = binding.rgActivityReduction.getChildAt(0) as? RadioButton
            radioButton?.let { binding.rgActivityReduction.check(it.id) }
        }
        savePreferences()
        updateFunctionalScore()
    }

    private fun updateFunctionalScore() {
        val radioGroup = binding.rgActivityReduction
        val scoreLabel = binding.tvFunctionalScore
        val maxScore = 3 // 0 to 3

        val checkedId = radioGroup.checkedRadioButtonId
        val index = if (checkedId != -1) {
            val checkedRadioButton = radioGroup.findViewById<RadioButton>(checkedId)
            radioGroup.indexOfChild(checkedRadioButton)
        } else {
            0
        }
        
        scoreLabel.text = getString(R.string.functional_score_format, index, maxScore)
    }

    private fun savePreferences() {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val checkedId = binding.rgActivityReduction.checkedRadioButtonId
        val index = if (checkedId != -1) {
            binding.rgActivityReduction.indexOfChild(binding.rgActivityReduction.findViewById(checkedId))
        } else {
            0
        }
        with(sharedPref.edit()) {
            putInt(KEY_ACTIVITY, index)
            apply()
        }
    }

    private fun loadPreferences() {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val index = sharedPref.getInt(KEY_ACTIVITY, 0)
        
        if (index >= 0 && index < binding.rgActivityReduction.childCount) {
            val radioButton = binding.rgActivityReduction.getChildAt(index) as? RadioButton
            radioButton?.let { binding.rgActivityReduction.check(it.id) }
        }
    }

    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
