package com.dicoding.finego

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dicoding.finego.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id){
                R.id.budgetPlanningFragment -> {
                    supportActionBar?.apply {
                        title = getString(R.string.budgetPlanning)
                        setDisplayHomeAsUpEnabled(false)
                    }
                }
                R.id.transactionTrackingFragment -> {
                    supportActionBar?.apply {
                        title = getString(R.string.transactionTracking)
                        setDisplayHomeAsUpEnabled(false)
                    }
                }
                R.id.monthlyReportFragment -> {
                    supportActionBar?.apply {
                        title = getString(R.string.monthlyReport)
                        setDisplayHomeAsUpEnabled(false)
                    }
                }
            }
        }
        binding.bottomNavigation.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            R.id.profile_menu -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Apakah Anda yakin ingin logout?")
        alertDialog.setPositiveButton("Ya") { _, _ ->
            lifecycleScope.launch {
                val credentialManager = CredentialManager.create(this@MainActivity)
                auth.signOut()
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        alertDialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navHostFragment.navController
    }
}