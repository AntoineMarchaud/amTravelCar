package com.amarchaud.travelcar.ui

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.fragment.app.Fragment
import com.amarchaud.travelcar.R
import com.amarchaud.travelcar.databinding.ActivityMainBinding
import com.amarchaud.travelcar.ui.account.main.AccountFragment
import com.amarchaud.travelcar.ui.car.search.SearchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val SAVED_TAB = "saved_tab"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SAVED_TAB, binding.navigation.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.light_blue_400)

        with(binding) {

            navigation.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.navigation_search -> {
                        supportFragmentManager.findFragmentByTag(SearchFragment.TAG)?.let {
                            displayFragment(it, SearchFragment.TAG)
                        } ?: displayFragment(SearchFragment.newInstance(), SearchFragment.TAG)
                    }

                    R.id.navigation_account -> {
                        supportFragmentManager.findFragmentByTag(AccountFragment.TAG)?.let {
                            displayFragment(it, AccountFragment.TAG)
                        } ?: displayFragment(AccountFragment.newInstance(), AccountFragment.TAG)
                    }
                }

                true
            }

            if (savedInstanceState != null) {
                navigation.selectedItemId = savedInstanceState.getInt(SAVED_TAB)
            } else {
                navigation.selectedItemId = R.id.navigation_search
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun displayFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainTabContainer, fragment, tag)
        transaction.commit()
    }
}