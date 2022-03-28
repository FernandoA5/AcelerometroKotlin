package com.example.aceleracin

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Toast
import com.example.aceleracin.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var guardado=0;
    private var borrado=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //SI AGREGAMOS UN BOTON
        //binding.calculateButton.setOnClickListener { boton() }
        setUpSensorStuff()

    }
    private fun setUpSensorStuff()
    {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        //val stringX = binding.textoX.text.toString()
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            binding.textoX.text= getString(R.string.fuerza_en_x, event.values[0].toString())
            binding.textoY.text= getString(R.string.fuerza_en_y, event.values[1].toString())
            binding.textoZ.text= getString(R.string.fuerza_en_z, event.values[2].toString())

            if(event.values[0] >= 4.0 && guardado==0)
            {
                guardar(binding.textBox.text.toString())
                binding.readFile.text = getString(R.string.texto_archivo, cargar())

                guardado=1
            }
            if(event.values[1]>= 4.0 && borrado==0)
            {
                borrar();
            }
            if(event.values[0] <= 1.0){ guardado=0 }
            if(event.values[1] <= 1.0){ borrado=0 }
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
        try{
            val rutaSD = baseContext.getExternalFilesDir(null)?.absolutePath
            val miCarpeta= File(rutaSD, "MisDatos")
            if(!miCarpeta.exists()){
                miCarpeta.mkdir()
            }
            val ficheroFisico = File(miCarpeta, "Datos.txt")
            ficheroFisico.appendText("$texto\n")
        }catch(e : Exception){
            Toast.makeText(this, "No se ha podido guardar", Toast.LENGTH_LONG).show()
        }
    }
    fun cargar():String{
        var texto=" "
        try{
            val rutaSD = baseContext.getExternalFilesDir(null)?.absolutePath
            val miCarpeta = File(rutaSD, "MisDatos")
            val ficheroFisico = File(miCarpeta, "Datos.txt")
            val fichero = BufferedReader(InputStreamReader(FileInputStream(ficheroFisico)))
            texto = fichero.use(BufferedReader::readText)
        }catch(e: java.lang.Exception){}
        return texto
    }
    fun borrar(){
        val rutaSD = baseContext.getExternalFilesDir(null)?.absolutePath
        val miCarpeta= File(rutaSD, "MisDatos")
        if(!miCarpeta.exists()){
            miCarpeta.mkdir()
        }
        val ficheroFisico = File(miCarpeta, "Datos.txt")
    }
}

