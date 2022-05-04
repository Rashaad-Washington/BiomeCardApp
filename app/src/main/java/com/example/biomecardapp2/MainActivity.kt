package com.example.biomecardapp2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.biomecardapp2.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("STATUS_TEXT", "Path = test\n")
        binding.buttonsearch.setOnClickListener(DownloadListener())

    }

    private fun isNetworkAvailable(): Boolean {
        var available = false

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        cm?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    ) {
                        available = true
                    }
                }
            } else {
                cm.getActiveNetworkInfo()?.run {
                    if (type == ConnectivityManager.TYPE_MOBILE
                        || type == ConnectivityManager.TYPE_WIFI
                        || type == ConnectivityManager.TYPE_VPN
                    ) {
                        available = true
                    }
                }
            }
        }
        return available
    }

    var searchJob: Job? = null

    inner class DownloadListener : View.OnClickListener {
        override fun onClick(view: View?) {
            if (isNetworkAvailable()) {
                Log.i("STATUS_TEXT", "Path = test2\n")
                binding.progressBar.isVisible = true
                binding.status.isVisible = false
                binding.editTextUsername.isVisible = false
                binding.editTextPassword.isVisible = false
                binding.status.setText("")
                startDownload()
            } else {
                Log.i("STATUS_TEXT", "Path = test3\n")
            }

        }

        private fun startDownload() {
            searchJob = CoroutineScope(Dispatchers.IO).launch {
                //**************************************************************************************
                val builder = Uri.Builder()
                    .scheme("http")
                    .authority("biomecard.at")
                    .path("/api/auth/generate_auth_cookie/?username=")

                //.appendQueryParameter("&password=", binding.editTextPassword.getText().toString())
                //var path: String = builder.build().toString()
                var username = binding.editTextUsername.getText().toString()
                var password = binding.editTextPassword.getText().toString()
                var path: String = "https://biomecard.at/api/auth/generate_auth_cookie/?username=" +
                        "${username}&password=${password}"
                Log.i("STATUS_TEXT", "Path = ${path}\n")
                val url = URL(path)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

                var jsonStr = ""
                try {
                    jsonStr = connection.getInputStream()
                        .bufferedReader().use(BufferedReader::readText)
                } finally {
                    connection.disconnect()

                }

                try {
                    val json = JSONObject(jsonStr) //same thing as jsonString but as type JSON
                    Log.i("STATUS_TEXT", "JSON = ${jsonStr}\n")
                    if (json.getString("status").equals("error")){
                        withContext(Dispatchers.Main) {
                            binding.status.isVisible = true
                            binding.editTextUsername.isVisible = true
                            binding.editTextPassword.isVisible = true
                            binding.status.setText("Invalid username and/or password")

                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            binding.status.isVisible = true
                            //binding.status.setText("Successfully logged in")
                            buTestUpdateText2(binding.buttonsearch)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.status.isVisible = true
                        binding.editTextUsername.isVisible = true
                        binding.editTextPassword.isVisible = true
                        binding.status.setText("Sorry, app is under maintenance.")

                    }

                }
                withContext(Dispatchers.Main) {
                    binding.progressBar.isVisible = false

                }

            }
        }
    }
    fun buTestUpdateText2 (view: View) {
        val changePage : Intent = Intent(this, BiomeMain::class.java)
        startActivity(changePage)
    }
    }