package com.pegasone.AMSS.ui.clinical

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.pegasone.AMSS.AMSSApplication
import com.pegasone.AMSS.R
import com.pegasone.AMSS.databinding.FragmentClinicalBinding

class ClinicalFragment : Fragment() {

    private var _binding: FragmentClinicalBinding? = null
    private val binding get() = _binding!!

    private val PREFS_NAME = "ClinicalPrefs"
    private val KEY_MENTAL = "mental_index"
    private val KEY_ATAXIA = "ataxia_index"
    private val KEY_EDEMA = "edema_index"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClinicalBinding.inflate(inflater, container, false)
        
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
        updateClinicalScore()

        return binding.root
    }

    private fun setupRadioButtons() {
        val listener = RadioGroup.OnCheckedChangeListener { _, _ ->
            updateClinicalScore()
            savePreferences()
        }

        binding.rgMentalStatus.setOnCheckedChangeListener(listener)
        binding.rgAtaxia.setOnCheckedChangeListener(listener)
        binding.rgPeripheralEdema.setOnCheckedChangeListener(listener)
    }

    private fun resetLocalForm() {
        // Reset to first option (index 0) for all groups
        setRadioByIndex(binding.rgMentalStatus, 0)
        setRadioByIndex(binding.rgAtaxia, 0)
        setRadioByIndex(binding.rgPeripheralEdema, 0)
        savePreferences()
        updateClinicalScore()
    }

    private fun updateClinicalScore() {
        val score1 = getSelectedValue(binding.rgMentalStatus)
        val score2 = getSelectedValue(binding.rgAtaxia)
        val score3 = getSelectedValue(binding.rgPeripheralEdema)
        
        val totalScore = score1 + score2 + score3
        val maxScore = 10 // 4 + 4 + 2
        
        binding.tvClinicalScore.text = getString(R.string.clinical_score_format, totalScore, maxScore)
    }

    private fun getSelectedValue(radioGroup: RadioGroup): Int {
        val checkedId = radioGroup.checkedRadioButtonId
        if (checkedId == -1) return 0
        val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
        return radioGroup.indexOfChild(radioButton)
    }

    private fun savePreferences() {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(KEY_MENTAL, getSelectedValue(binding.rgMentalStatus))
            putInt(KEY_ATAXIA, getSelectedValue(binding.rgAtaxia))
            putInt(KEY_EDEMA, getSelectedValue(binding.rgPeripheralEdema))
            apply()
        }
    }

    private fun loadPreferences() {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val mIdx = sharedPref.getInt(KEY_MENTAL, 0)
        val aIdx = sharedPref.getInt(KEY_ATAXIA, 0)
        val eIdx = sharedPref.getInt(KEY_EDEMA, 0)

        setRadioByIndex(binding.rgMentalStatus, mIdx)
        setRadioByIndex(binding.rgAtaxia, aIdx)
        setRadioByIndex(binding.rgPeripheralEdema, eIdx)
    }

    private fun setRadioByIndex(radioGroup: RadioGroup, index: Int) {
        if (index >= 0 && index < radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(index) as? RadioButton
            radioButton?.let { radioGroup.check(it.id) }
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
