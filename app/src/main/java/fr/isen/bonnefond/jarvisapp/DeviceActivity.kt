package fr.isen.bonnefond.jarvisapp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import fr.isen.bonnefond.jarvisapp.databinding.ActivityDeviceBinding
import java.util.*
import android.content.BroadcastReceiver

class DeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceBinding

    private var notifications = false

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private var bluetoothGatt: BluetoothGatt? = null


    private val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
    private val characteristicButtonUUID = UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")
    private val configNotifications = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private var demoBroadcastReceiver: BroadcastReceiver? = null

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val deviceName = intent.getStringExtra("DEVICE_NAME")
        val deviceAddress = intent.getStringExtra("DEVICE_MAC")
        binding.deviceName.text = deviceName

        val device = bluetoothAdapter!!.getRemoteDevice(deviceAddress)
        bluetoothGatt = device.connectGatt(this, false, gattCallback)

        binding.group.visibility = View.GONE

        binding.buttonDemarrage.setOnClickListener() {
            val intent = Intent(this@DeviceActivity, DemoActivity::class.java)
            startActivity(intent)
        }

        binding.checkBox2.setOnClickListener() {
            binding.buttonDemarrage.isEnabled = true;
        }
    }

    private fun show() {
        runOnUiThread {
            binding.group.visibility = View.VISIBLE
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission", "SetTextI18n")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    show()
                    bluetoothGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    runOnUiThread {
                        binding.group.visibility = View.GONE

                        val color = Color.parseColor("#F80D1B")
                        binding.connectedTextView.setTextColor(color)
                        binding.connectedTextView.text = "Disconnected"
                        binding.connectedImageView.setImageResource(R.drawable.disconnected_icon)
                    }
                    bluetoothGatt?.close()
                }
                else -> {
                    Log.d("STATUS", "Connection state changed: $newState")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d("STATUS", "Services discovered successfully.")
                    val service = gatt?.getService(serviceUUID)
                    val characteristicButton = service?.getCharacteristic(characteristicButtonUUID)
                    binding.checkBox.setOnClickListener {
                        if (!notifications) {
                            characteristicButton?.let { enableNotifications(it) }
                        } else {
                            characteristicButton?.let { disableNotifications(it) }
                        }
                    }
                }
                else -> {
                    Log.d("STATUS", "Service discovery failed: $status")
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            if (characteristic?.uuid == characteristicButtonUUID) {
                val value = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                runOnUiThread {
                    if (value == 1) {
                        val intent = Intent(this@DeviceActivity, DemoActivity::class.java)
                        intent.putExtra("ROTATE_DIRECTION", "RIGHT")
                        startActivity(intent)
                    }
                }
                Log.d("Bluetooth", "Received value: $value")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
        val descriptor = characteristic.getDescriptor(configNotifications)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        bluetoothGatt?.writeDescriptor(descriptor)
        bluetoothGatt?.setCharacteristicNotification(characteristic, true)
        notifications = true
    }

    @SuppressLint("MissingPermission")
    fun disableNotifications(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.setCharacteristicNotification(characteristic, false)
        notifications = false
    }
}