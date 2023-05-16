package fr.isen.bonnefond.jarvisapp

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.bonnefond.jarvisapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1
    }

    private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            BLUETOOTH_CONNECT,
            BLUETOOTH_SCAN,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION
        )
    }
    private lateinit var binding: ActivityMainBinding
    private var scanning = false
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val handler = Handler()
    private val SCAN_PERIOD: Long = 10000

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner

        checkPermissions()
        startScan(bluetoothLeScanner)
        onStop()
    }
    override fun onStop() {
        super.onStop()
        if(bluetoothAdapter?.isEnabled == true){
            scanning = false
        }
    }

    private fun checkPermissions() {
        if (REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS,
                PERMISSION_REQUEST_LOCATION
            )
        }
    }
    private fun startScan(bluetoothLeScanner: BluetoothLeScanner) {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non disponible", Toast.LENGTH_SHORT).show()
            return
        }
        if (bluetoothAdapter?.isEnabled == false) {
            Toast.makeText(this, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
            return
        }
        if (bluetoothAdapter?.isEnabled == true) {
            binding.playButton.setOnClickListener() {
                toggleAction(bluetoothLeScanner)
                initList()
            }
        }
    }
    private fun toggleAction(bluetoothLeScanner: BluetoothLeScanner) {
        if (!scanning) {
            binding.titleScan.text = "Scan en cours..."
            binding.playButton.setImageResource(R.drawable.icon_pause)
            binding.progressBar.isIndeterminate = true
            scanLeDevice(bluetoothLeScanner)
            scanning = true
        } else {
            binding.titleScan.text = "Lancer le scan"
            binding.playButton.setImageResource(R.drawable.icon_play)
            binding.progressBar.isIndeterminate = false
            scanLeDevice(bluetoothLeScanner)
            scanning = false
        }
    }
    private fun initList() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = DeviceAdapter(leDeviceListAdapter) { deviceName, MAC ->
            val intent = Intent(this@MainActivity, DeviceActivity::class.java)
            intent.putExtra("DEVICE_NAME", deviceName)
            intent.putExtra("DEVICE_MAC", MAC)
            startActivity(intent)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "L'autorisation de BLUETOOTH est requise pour scanner les appareils Bluetooth.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun scanLeDevice(bluetoothLeScanner : BluetoothLeScanner) {
        if (!scanning) {
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }
    private val leDeviceListAdapter = Devices()
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDeviceListAdapter.addDevice(result.device, result.rssi)
            binding.recyclerView.adapter = DeviceAdapter(leDeviceListAdapter) { deviceName, MAC ->
                val intent = Intent(this@MainActivity, DeviceActivity::class.java)
                intent.putExtra("DEVICE_NAME", deviceName)
                intent.putExtra("DEVICE_MAC", MAC)
                startActivity(intent)
            }
        }
    }

    class Devices {
        var device_name: ArrayList<String> = ArrayList()
        var MAC: ArrayList<String> = ArrayList()
        var distance : ArrayList<Int> = ArrayList()
        var size = 0
        @SuppressLint("MissingPermission")
        fun addDevice(device: BluetoothDevice, rssi: Int) {

            if (!device.name.isNullOrBlank()) {

                if(!MAC.contains(device.address)) {
                    device_name.add(device.name)
                    MAC.add(device.address)
                    distance.add(rssi)
                    size++
                    Log.d("Device", "${device.name} + $MAC")
                }
            }
        }
    }
}