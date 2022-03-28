package com.example.aceleracin

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Toast
import com.example.aceleracin.databinding.ActivityMainBinding
import java.io.*

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

        binding.readFile.text=getString(R.string.texto_archivo, cargar())
    }
    private fun setUpSensorStuff()
    {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            binding.textoX.text= getString(R.string.fuerza_en_x, event.values[0].toString())
            binding.textoY.text= getString(R.string.fuerza_en_y, event.values[1].toString())
            binding.textoZ.text= getString(R.string.fuerza_en_z, event.values[2].toString())
            if(guardado==0 && borrado==0)
            {
                if (event.values[0] >= 4.0){
                    guardar(binding.textBox.text.toString())
                    binding.readFile.text=getString(R.string.texto_archivo, cargar())
                    guardado=1
                }
                if(event.values[0] <=  -4.0 ){
                    borrar()
                    binding.readFile.text=getString(R.string.texto_archivo, cargar())
                    borrado=1
                }
            }
            if(event.values[0] <= 1.0 && event.values[0] >=-1.0){
                borrado=0
                guardado=0
            }
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
    fun borrar()
    {
        try{
            val rutaSD = baseContext.getExternalFilesDir(null)?.absolutePath
            val miCarpeta= File(rutaSD, "MisDatos")
            if(!miCarpeta.exists()){
                miCarpeta.mkdir()
            }
            val ficheroFisico = File(miCarpeta, "Datos.txt")
            ficheroFisico.deleteRecursively()
        }catch(e: java.lang.Exception){
            Toast.makeText(this, "No se ha podido borrar", Toast.LENGTH_LONG).show()
        }
    }
}

