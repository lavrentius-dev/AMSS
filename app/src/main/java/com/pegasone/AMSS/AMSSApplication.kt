package com.pegasone.AMSS

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AMSSApplication : Application() {

    companion object {
        lateinit var gradColor: List<Int>
            private set
        
        private val _backgroundColor = MutableLiveData<Int>()
        var backgroundColor: Int
            get() = _backgroundColor.value ?: 0
            set(value) {
                _backgroundColor.value = value
            }
            
        val backgroundColorLiveData: LiveData<Int> = _backgroundColor

        private val _resetTrigger = MutableLiveData<Boolean>()
        val resetTrigger: LiveData<Boolean> = _resetTrigger

        /**
         * Triggers a global reset of all forms by clearing SharedPreferences
         * and notifying active fragments to refresh.
         */
        fun triggerReset(context: Context) {
            // Clear all preference files
            val prefs = listOf("SelfReportPrefs", "ClinicalPrefs", "FunctionalPrefs")
            prefs.forEach {
                context.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().apply()
            }
            
            // Signal active fragments to reload UI
            _resetTrigger.value = true
            _resetTrigger.value = false
        }
            
        fun getColorGradient(firstColor: Int, lastColor: Int, numColors: Int): List<Int> {
            if (numColors <= 0) return emptyList()
            if (numColors == 1) return listOf(firstColor)
            val gradient = mutableListOf<Int>()
            for (i in 0 until numColors) {
                val ratio = i.toFloat() / (numColors - 1)
                gradient.add(ColorUtils.blendARGB(firstColor, lastColor, ratio))
            }
            return gradient
        }
    }

    override fun onCreate() {
        super.onCreate()
        val firstColor = ContextCompat.getColor(this, R.color.dark_green)
        val lastColor = ContextCompat.getColor(this, R.color.red)
        gradColor = getColorGradient(firstColor, lastColor, 13)
        backgroundColor = gradColor[0]
    }
}
