package fr.isen.bonnefond.jarvisapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.isen.bonnefond.jarvisapp.databinding.DeviceCellBinding

class DeviceAdapter(private val devices: MainActivity.Devices, private val onItemClick: (String, String) -> Unit) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DeviceCellBinding.inflate(inflater,parent,false)
        return DeviceViewHolder(binding)
    }
    class DeviceViewHolder(binding: DeviceCellBinding):RecyclerView.ViewHolder(binding.root){
        val deviceName = binding.deviceName
        val macAddress = binding.macAddress
        val distanceNumber = binding.distanceNumber
        val image = binding.rond
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.deviceName.text = devices.device_name[position]
        holder.macAddress.text = devices.MAC[position]
        if(devices.distance[position] >= -65) {
            holder.image.setImageResource(R.drawable.circle_light_purple)
        } else {
            holder.image.setImageResource(R.drawable.circle_dark_purple)
        }
        holder.distanceNumber.text = devices.distance[position].toString()

        holder.itemView.setOnClickListener {
            onItemClick(devices.device_name[position], devices.MAC[position])
        }
    }
}