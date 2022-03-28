package com.example.aceleracin

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import com.example.aceleracin.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //SI AGREGAMOS UN BOTON
        //binding.calculateButton.setOnClickListener { boton() }
        setUpSensorStuff()
        guardar("Holi <3")
    }
    private fun setUpSensorStuff()
    {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        val stringX = binding.textoX.text.toString()
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            binding.textoX.text= getString(R.string.fuerza_en_x, event.values[0].toString())
            binding.textoY.text= getString(R.string.fuerza_en_y, event.values[1].toString())
            binding.textoZ.text= getString(R.string.fuerza_en_z, event.values[2].toString())
        }
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
    fun guardar(texto: String){
        val rutaSD = baseContext.getExternalFilesDir(null)?.absolutePath
        val miCarpeta= File(rutaSD, "MisDatos")
        if(!miCarpeta.exists()){
            miCarpeta.mkdir()
        }
        val ficheroFisico = File(miCarpeta, "Datos.txt")
        ficheroFisico.appendText("$texto\n")

    }
}

