package mx.tec.empleados.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {

    // URL de la API en Docker (siempre con "/" al final).
    // - Desde el EMULADOR: "http://10.0.2.2:3000/" (10.0.2.2 es tu computadora vista desde el emulador)
    // - Desde un teléfono físico en la misma red Wi-Fi: "http://<IP de tu compu>:3000/"
    // - Con el túnel HTTPS de docker compose: "https://<palabras>.trycloudflare.com/"
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val json = Json {
        ignoreUnknownKeys = true  // si la API agrega campos nuevos, la app no truena
        encodeDefaults = true     // manda TODOS los campos, aunque tengan su valor por defecto
                                  // (sin esto, activo = true no se enviaría al editar)
    }

    // Imprime en Logcat cada petición y respuesta. Filtro: tag~:okhttp
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: EmpleadosApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(EmpleadosApi::class.java)
    }
}
