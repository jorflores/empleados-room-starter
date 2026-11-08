package mx.tec.empleados.data.remote

import mx.tec.empleados.data.model.Empleado
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
    suspend fun listar(): List<Empleado>

    // El id que va en el body se ignora: el servidor asigna uno nuevo.
    @POST("empleados")
    suspend fun crear(@Body empleado: Empleado): Empleado

    @PUT("empleados/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body empleado: Empleado): Empleado

    // DELETE responde 204 sin body: por eso Response<Unit> y se revisa isSuccessful.
    @DELETE("empleados/{id}")
    suspend fun borrar(@Path("id") id: Int): Response<Unit>
}
