package com.example.biomecardapp2

import android.app.SearchManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.biomecardapp2.databinding.ActivityBiomeMainBinding
import com.example.biomecardapp2.databinding.FragmentNotificationsBinding
import com.example.biomecardapp2.databinding.ObjectArtBinding
import com.google.zxing.integration.android.IntentIntegrator

class BiomeMain : AppCompatActivity() {

    private lateinit var binding: ActivityBiomeMainBinding
    //private lateinit var bindingScan: FragmentNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityBiomeMainBinding.inflate(layoutInflater)
        val bindingScan: FragmentNotificationsBinding = FragmentNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindingScan.qrButton.setOnClickListener {
            Toast.makeText(applicationContext, "test", Toast.LENGTH_LONG).show()
            bindingScan.textView.setText("TEsting")
        }
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_biome_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            AlertDialog.Builder(this)
                .setMessage("Would you like to go to ${result.contents}?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                    val intent = Intent(Intent.ACTION_WEB_SEARCH)
                    intent.putExtra(SearchManager.QUERY,result.contents)
                    startActivity(intent)
                })
                .setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->  })
                .create()
                .show()
        }
    }

    override fun onBackPressed() {

    }

}