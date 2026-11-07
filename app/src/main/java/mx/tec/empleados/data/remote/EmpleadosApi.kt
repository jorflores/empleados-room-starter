package mx.tec.empleados.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints de api-empleados-crud (https://github.com/jorflores/api-empleados-crud).
interface EmpleadosApi {

    @GET("empleados")
    suspend fun listar(): List<EmpleadoDto>

    @POST("empleados")
    suspend fun crear(@Body datos: EmpleadoRequest): EmpleadoDto

    @PUT("empleados/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body datos: EmpleadoRequest): EmpleadoDto

    // DELETE responde 204 sin body: por eso Response<Unit> y se revisa isSuccessful.
    @DELETE("empleados/{id}")
    suspend fun borrar(@Path("id") id: Int): Response<Unit>
}
