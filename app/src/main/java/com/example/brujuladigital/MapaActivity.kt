package com.example.brujuladigital

// Permite acceder a las constantes de los permisos de Android.
import android.Manifest

// Permite comprobar si un permiso está concedido.
import android.content.pm.PackageManager

// Permite recuperar el estado anterior de una actividad.
import android.os.Bundle

// Componentes gráficos de la interfaz.
import android.widget.Button
import android.widget.TextView

// Permite solicitar permisos y recibir sus resultados.
import androidx.activity.result.contract.ActivityResultContracts

// Proporciona funciones compatibles con diferentes versiones de Android.
import androidx.core.content.ContextCompat

// Actividad que permite trabajar con fragmentos de AndroidX.
import androidx.fragment.app.FragmentActivity

// Clases relacionadas con los servicios de ubicación.
import com.google.android.gms.location.*

// Clases necesarias para trabajar con Google Maps.
import com.google.android.gms.maps.*

// Clases para representar coordenadas, marcadores y otros elementos.
import com.google.android.gms.maps.model.*

// Permite cancelar una solicitud de ubicación.
import com.google.android.gms.tasks.CancellationTokenSource


/**
 * Pantalla encargada de mostrar un mapa y obtener
 * la ubicación puntual del dispositivo.
 *
 * FragmentActivity:
 * Permite incorporar fragmentos dentro de la actividad,
 * como el fragmento que contiene Google Maps.
 *
 * OnMapReadyCallback:
 * Interfaz que permite recibir una notificación
 * cuando Google Maps está preparado para utilizarse.
 */
class MapaActivity : FragmentActivity(), OnMapReadyCallback {

    // =====================================================
    // 1. DECLARACIÓN DE PROPIEDADES
    // =====================================================

    // Componente gráfico donde mostraremos las coordenadas,
    // la precisión y los mensajes relacionados con la ubicación.
    //
    // lateinit permite inicializarlo posteriormente en onCreate().
    private lateinit var txtUbicacion: TextView


    // Cliente de Google Play Services encargado de solicitar
    // la ubicación del dispositivo.
    //
    // Puede utilizar diferentes fuentes de información:
    // GPS, redes Wi-Fi y redes móviles.
    private lateinit var cliente: FusedLocationProviderClient


    // Referencia al objeto que permite controlar Google Maps.
    //
    // Inicialmente es null porque el mapa necesita un tiempo
    // para inicializarse y estar disponible.
    private var googleMap: GoogleMap? = null


    // Almacena las últimas coordenadas obtenidas del dispositivo.
    //
    // LatLng es una clase que contiene dos valores:
    // - Latitud.
    // - Longitud.
    //
    // Inicialmente es null porque todavía no tenemos ubicación.
    private var ultimaUbicacion: LatLng? = null


    // Objeto que permitirá cancelar una solicitud de ubicación.
    //
    // Será útil para evitar solicitudes simultáneas o cancelar
    // una búsqueda cuando abandonemos la actividad.
    private var cancelacion: CancellationTokenSource? = null


    // =====================================================
    // 2. SOLICITUD DE PERMISOS
    // =====================================================

    /*
     * Android necesita autorización del usuario para acceder
     * a la ubicación del dispositivo.
     *
     * registerForActivityResult() permite registrar una operación
     * y recibir posteriormente su resultado.
     *
     * RequestMultiplePermissions() permite solicitar varios
     * permisos en una misma operación.
     *
     * El resultado se recibe de forma asíncrona, es decir,
     * cuando el usuario responde al diálogo de permisos.
     */
    private val solicitarPermisos = registerForActivityResult(

        ActivityResultContracts.RequestMultiplePermissions()

    ) { permisos ->

        /*
         * La variable permisos contiene un mapa:
         *
         * Clave: nombre del permiso.
         * Valor: true si se ha concedido o false si se ha denegado.
         *
         * Comprobamos si el usuario ha concedido alguno
         * de los siguientes permisos:
         *
         * ACCESS_FINE_LOCATION:
         * Permite acceder a una ubicación precisa.
         *
         * ACCESS_COARSE_LOCATION:
         * Permite acceder a una ubicación aproximada.
         *
         * El operador || representa un OR lógico.
         * Es suficiente con que uno de los permisos sea true.
         */

        if (
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {

            // Si tenemos autorización, solicitamos la ubicación.
            obtenerUbicacion()

        } else {

            // Si no tenemos autorización, informamos al usuario.
            txtUbicacion.text =
                "Permiso denegado. Puedes volver a solicitarlo o revisar Ajustes."
        }
    }


    // =====================================================
    // 3. INICIALIZACIÓN DE LA ACTIVIDAD
    // =====================================================

    /*
     * onCreate() forma parte del ciclo de vida de Android.
     *
     * Se ejecuta cuando se crea una instancia de la actividad.
     *
     * En este método cargamos la interfaz gráfica,
     * localizamos sus componentes e inicializamos los servicios
     * que necesitaremos posteriormente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // Ejecutamos el método onCreate() de la clase padre.
        super.onCreate(savedInstanceState)


        // Asociamos esta actividad con su archivo de diseño XML.
        // El archivo se encuentra en res/layout/activity_mapa.xml.
        setContentView(R.layout.activity_mapa)


        // Buscamos el TextView mediante su identificador.
        // Guardamos su referencia para poder modificar su texto.
        txtUbicacion = findViewById(R.id.txtUbicacion)


        /*
         * Inicializamos el cliente de ubicación.
         *
         * LocationServices proporciona acceso a los servicios
         * de posicionamiento de Google Play Services.
         *
         * getFusedLocationProviderClient() devuelve un objeto
         * que permite realizar solicitudes de ubicación.
         *
         * this representa la actividad actual y proporciona
         * el contexto necesario para acceder al servicio.
         *
         * Esta instrucción no obtiene todavía las coordenadas.
         * Únicamente prepara el cliente que utilizaremos
         * posteriormente para solicitarlas.
         */
        cliente = LocationServices.getFusedLocationProviderClient(this)

    }

}