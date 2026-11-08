package mx.tec.empleados.data.red

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// Avisa con un Flow cada vez que el teléfono gana o pierde la red.
class ConectividadObserver(context: Context) {

    private val cm = context.getSystemService(ConnectivityManager::class.java)

    val hayInternet: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(false) }
        }
        trySend(estaConectado())                       // valor inicial
        cm.registerDefaultNetworkCallback(callback)    // empieza a escuchar
        awaitClose { cm.unregisterNetworkCallback(callback) }  // deja de escuchar
    }.distinctUntilChanged()                           // solo avisa si cambió

    private fun estaConectado(): Boolean {
        val capacidades = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
