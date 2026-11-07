package mx.tec.empleados.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URL de la API (siempre con "/" al final). Elige una:
    // - API en Docker de tu compu, desde el EMULADOR: "http://10.0.2.2:3000/"
    //   (10.0.2.2 es tu computadora vista desde el emulador; requiere usesCleartextTraffic en el manifest)
    // - Túnel HTTPS o Render: "https://<tu-url>/"
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Si la API agrega campos nuevos, la app no truena.
    private val json = Json { ignoreUnknownKeys = true }

    // Imprime en Logcat cada petición y respuesta. Filtro: tag~:okhttp
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Render (plan gratuito) tarda cerca de un minuto en despertar: timeouts largos.
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
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
