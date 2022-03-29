package com.example.aceleracin

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.example.aceleracin.databinding.ActivityMainBinding
import java.io.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var guardado=0;
    private var borrado=0;
    private var editado=0
    private var editando=0
    private val accionar=5

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

            val x_centrado = event.values[0] <= 2.0 && event.values[0] >=-2.0
            val z_centrado = event.values[2] <= 2.0 && event.values[2] >= -2.0
            val vacio = (binding.textBox.text.toString() == " " || binding.textBox.text.toString() =="")
            if(guardado==0 && borrado==0 && editado ==0)
            {
                //GUARDAR
                if (event.values[0] >= accionar && z_centrado && !vacio){
                    if(editando==1){
                        borrar()
                        editando=0
                    }
                    guardar(binding.textBox.text.toString())
                    binding.textBox.text= Editable.Factory.getInstance().newEditable("")
                    binding.readFile.text=getString(R.string.texto_archivo, cargar())
                    guardado=1
                }
                //BORRAR
                if(event.values[0] <=  -accionar && z_centrado){
                    borrar()
                    binding.readFile.text=getString(R.string.texto_archivo, cargar())
                    borrado=1
                }
                //EDITAR
                if(event.values[2] >=accionar && x_centrado)
                {
                    binding.textBox.text= Editable.Factory.getInstance().newEditable(cargar())
                    editado=1
                    editando=1
                }
                //LIMPIAR TEXTBOX
                if(event.values[2] <= -accionar  && x_centrado)
                {
                    binding.textBox.text= Editable.Factory.getInstance().newEditable("")
                    editado=1
                }
            }
            if(x_centrado && z_centrado){
                borrado=0
                guardado=0
                editado=0
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

