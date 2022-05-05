package com.example.biomecardapp2.ui.scan

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.example.biomecardapp2.databinding.FragmentScanBinding
import com.example.biomecardapp2.ui.collections.CollectionsFragment
import com.example.biomecardapp2.ui.collections.imageURLs
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


private const val CAMERA_REQUEST_CODE = 101

class ScanFragment : Fragment(){
    private lateinit var codeScanner: CodeScanner
    private var _binding: FragmentScanBinding? = null
    var scannedcode : String = ""
    public var displayStringList: MutableList<String> = mutableListOf()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

            ViewModelProvider(this).get(ScanViewModel::class.java)

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root


        setupPermissions()
        codeScanner()
        return root

    }


    private fun codeScanner() {
        codeScanner = CodeScanner(this.requireContext(), binding.scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false
            var searchJob: Job? = null
            decodeCallback = DecodeCallback {
                scannedcode = it.text


               searchJob = CoroutineScope(Dispatchers.IO).launch {
                   withContext(Dispatchers.Main) {
                       Toast.makeText(
                           context,
                           "Checking Card", Toast.LENGTH_SHORT
                       ).show()
                       codeScanner.stopPreview()
                       binding.scannerView.isVisible = false
                   }
                   Log.i("Work", it.text)


                   val url = URL("https://www.biomecard.at/wp-content/uploads/2022/05/cardID.json")
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
                       try {
                           imageURLs.addtoList(json.getString(it.text))
                           withContext(Dispatchers.Main) {
                               Toast.makeText(
                                   context,
                                   "Added Card", Toast.LENGTH_LONG
                               ).show()
                               codeScanner.startPreview()
                               binding.scannerView.isVisible = true
                           }
                       } catch (e:Exception) {
                           withContext(Dispatchers.Main) {
                               Toast.makeText(
                                   context,
                                   "Didnt find it", Toast.LENGTH_LONG
                               ).show()
                               codeScanner.startPreview()
                               binding.scannerView.isVisible = true
                           }
                       }

                   } catch (e:Exception){
                       Log.i("ERror", "error ${e.message}")
                   }
               }


            }

            errorCallback = ErrorCallback {
                    Log.e("Main", "Camera error ${it.message}")
            }
        }


    }

    override fun onResume(){
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {

        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupPermissions(){
        val permission : Int = ContextCompat.checkSelfPermission(this.requireContext(),
        android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this.requireContext(), "You need the camera permission to be able to use this app!",
                    Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this.requireContext(), "Successful",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}