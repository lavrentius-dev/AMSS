package com.pegasone.AMSS

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pegasone.AMSS.databinding.ActivityMainBinding
import com.pegasone.AMSS.ui.clinical.ClinicalFragment
import com.pegasone.AMSS.ui.functional.FunctionalFragment
import com.pegasone.AMSS.ui.selfreport.SelfReportFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for Android 15 compatibility
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ensure status bar foreground (icons/text) is light (antique_white/white)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        // Observe backgroundColor to update status bar and app bar background
        AMSSApplication.backgroundColorLiveData.observe(this) { color ->
            // window.statusBarColor is deprecated in Android 15; edge-to-edge handles it.
            binding.appBar.setBackgroundColor(color)
            binding.tabLayout.setBackgroundColor(color)
        }

        // Adjust the AppBarLayout padding to avoid overlapping with the status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val pagerAdapter = MainPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        
        // Disable switching between tabs by swiping
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_self_report)
                1 -> getString(R.string.tab_clinical)
                2 -> getString(R.string.tab_functional)
                else -> null
            }
        }.attach()
    }

    private inner class MainPagerAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SelfReportFragment()
                1 -> ClinicalFragment()
                2 -> FunctionalFragment()
                else -> SelfReportFragment()
            }
        }
    }
}
