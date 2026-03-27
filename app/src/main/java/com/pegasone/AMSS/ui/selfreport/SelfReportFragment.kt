package com.pegasone.AMSS.ui.selfreport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.pegasone.AMSS.AMSSApplication
import com.pegasone.AMSS.R
import com.pegasone.AMSS.databinding.FragmentSelfReportBinding

class SelfReportFragment : Fragment() {

    private var _binding: FragmentSelfReportBinding? = null
    private val binding get() = _binding!!

    private val PREFS_NAME = "SelfReportPrefs"
    private val KEY_HEADACHE = "headache_index"
    private val KEY_GI = "gi_index"
    private val KEY_FATIGUE = "fatigue_index"
    private val KEY_DIZZINESS = "dizziness_index"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfReportBinding.inflate(inflater, container, false)
        
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
        updateSelfReportScore()

        binding.btnResetForms.setOnClickListener {
            showResetConfirmationDialog()
        }

        return binding.root
    }

    private fun setupRadioButtons() {
        val listener = RadioGroup.OnCheckedChangeListener { _, _ ->
            updateSelfReportScore()
            savePreferences()
        }

        binding.rgHeadache.setOnCheckedChangeListener(listener)
        binding.rgGiSymptoms.setOnCheckedChangeListener(listener)
        binding.rgFatigue.setOnCheckedChangeListener(listener)
        binding.rgDizziness.setOnCheckedChangeListener(listener)
    }

    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_reset_confirmation)
            .setPositiveButton(R.string.btn_reset) { _, _ ->
                // Pass applicationContext to avoid memory leaks
                AMSSApplication.triggerReset(requireContext().applicationContext)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun resetLocalForm() {
        // Since SharedPreferences are cleared globally, we just need to update the UI
        binding.rgHeadache.check(binding.rbHeadache0.id)
        binding.rgGiSymptoms.check(binding.rbGi0.id)
        binding.rgFatigue.check(binding.rbFatigue0.id)
        binding.rgDizziness.check(binding.rbDizziness0.id)
        updateSelfReportScore()
    }

    private fun updateSelfReportScore() {
        val score1 = getSelectedValue(binding.rgHeadache)
        val score2 = getSelectedValue(binding.rgGiSymptoms)
        val score3 = getSelectedValue(binding.rgFatigue)
        val score4 = getSelectedValue(binding.rgDizziness)
        
        val totalScore = score1 + score2 + score3 + score4
        
        val amsStatus = when {
            totalScore <= 2 -> getString(R.string.ams_none)
            totalScore <= 5 -> getString(R.string.ams_mild)
            totalScore <= 9 -> getString(R.string.ams_moderate)
            else -> getString(R.string.ams_severe)
        }
        
        binding.tvSelfReportScore.text = amsStatus
        
        // Update global background color based on the total score index
        if (totalScore >= 0 && totalScore < AMSSApplication.gradColor.size) {
            AMSSApplication.backgroundColor = AMSSApplication.gradColor[totalScore]
        }
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
            putInt(KEY_HEADACHE, getSelectedValue(binding.rgHeadache))
            putInt(KEY_GI, getSelectedValue(binding.rgGiSymptoms))
            putInt(KEY_FATIGUE, getSelectedValue(binding.rgFatigue))
            putInt(KEY_DIZZINESS, getSelectedValue(binding.rgDizziness))
            apply()
        }
    }

    private fun loadPreferences() {
        val sharedPref = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hIdx = sharedPref.getInt(KEY_HEADACHE, 0)
        val giIdx = sharedPref.getInt(KEY_GI, 0)
        val fIdx = sharedPref.getInt(KEY_FATIGUE, 0)
        val dIdx = sharedPref.getInt(KEY_DIZZINESS, 0)

        setRadioByIndex(binding.rgHeadache, hIdx)
        setRadioByIndex(binding.rgGiSymptoms, giIdx)
        setRadioByIndex(binding.rgFatigue, fIdx)
        setRadioByIndex(binding.rgDizziness, dIdx)
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
