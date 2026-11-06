package com.example.brujuladigital

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.roundToInt

/** Ejemplo didáctico de brújula: teléfono horizontal, pantalla hacia arriba. */
class MainActivity : Activity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private var magnetometro: Sensor? = null

    // Últimas lecturas recibidas de cada sensor (X, Y, Z).
    private val gravedad = FloatArray(3)
    private val campoMagnetico = FloatArray(3)
    private var tieneGravedad = false
    private var tieneMagnetismo = false

    private lateinit var txtGrados: TextView
    private lateinit var txtDireccion: TextView
    private lateinit var txtSensor: TextView
    private lateinit var imgBrujula: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtGrados = findViewById(R.id.txtGrados)
        txtDireccion = findViewById(R.id.txtDireccion)
        txtSensor = findViewById(R.id.txtSensor)
        imgBrujula = findViewById(R.id.imgBrujula)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (acelerometro == null || magnetometro == null) {
            txtSensor.text = "Falta acelerómetro o magnetómetro"
            txtDireccion.text = "Brújula no disponible"
        } else {
            txtSensor.text = "Sensores disponibles. Gira el teléfono."
        }
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {

                tieneGravedad = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {

                tieneMagnetismo = true
            }
        }
        if (tieneGravedad && tieneMagnetismo) calcularOrientacion()
    }





}
