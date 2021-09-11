package com.amarchaud.travelcar.data.repository.car

import arrow.core.Either
import com.amarchaud.travelcar.data.db.CarDao
import com.amarchaud.travelcar.data.remote.TravelCarApi
import com.amarchaud.travelcar.domain.db.car.EntityCarOptionCrossRef
import com.amarchaud.travelcar.domain.db.car.EntityOption
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.domain.local.user.AppUser
import com.amarchaud.travelcar.domain.remote.car.ApiCar
import com.amarchaud.travelcar.utils.translator.CarTranslator.toAppCar
import com.amarchaud.travelcar.utils.translator.CarTranslator.toEntityCar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppCarRepository @Inject constructor(
    private val travelCarApi: TravelCarApi,
    private val travelCarDao: CarDao
) : CarRepository {

    /**
     * Just emit db when there are some changes
     */
    override fun getCarsFromDbFlow() = flow {
        travelCarDao.getCarsWithOptionsFlow().collect {
            it?.let {
                emit(it.map { it.toAppCar() })
            }
        }
    }

    /**
     * first emit DB
     * then try to call endpoint, update db and emit it again
     */
    override fun getCarsFlow() = flow {
        emit(getCarsFromDb())
        if (update()) {
            emit(getCarsFromDb())
        }
    }


    /**
     * call api and refresh db
     */
    override suspend fun update(): Boolean {
        // then try to update
        val res = getCarsFromApi()
        if (res is Either.Right) {

            if (getCarsFromDb().isNullOrEmpty()) { // first time : add all
                //travelCarDao.addCars(res.value.map { it.toEntityCar() })

                res.value.forEach { oneCar ->
                    travelCarDao.addCar(oneCar.toEntityCar())
                    oneCar.equipments?.forEach {

                        if (travelCarDao.getOption(it) == null) {
                            travelCarDao.addOption(EntityOption(option = it))
                        }

                        val entityCar = travelCarDao.getCarByMarkAndModel(oneCar.make, oneCar.model)
                        val entityOption = travelCarDao.getOption(it)

                        if (entityCar != null && entityOption != null) {

                            travelCarDao.addRelation(
                                EntityCarOptionCrossRef(
                                    carId = entityCar.id,
                                    optionId = entityOption.id
                                )
                            )
                        }
                    }

                    // add relation
                }

            } else {
                // try to update / add
                res.value.forEach {
                    // warning : we suppose here make / model can not change
                    // normally the API need to send a remoteID
                    val exist = travelCarDao.getCarByMarkAndModel(it.make, it.model)
                    if (exist == null) {
                        travelCarDao.addCar(it.toEntityCar())
                    } else {
                        travelCarDao.updateCar(exist.apply {
                            this.year = it.year
                            this.picture = it.picture
                            equipments = it.equipments
                        })
                    }
                }
            }

            return true
        }

        return false
    }

    /**
     * Get cars from DB only
     */
    private suspend fun getCarsFromDb(): List<AppCar>? =
        travelCarDao.getCarsWithOptions()?.map { it.toAppCar() }

    /**
     * Get cars from api only
     */
    private suspend fun getCarsFromApi(): Either<String?, List<AppCar>> =

        suspendCoroutine { continuation ->

            travelCarApi.getCars().enqueue(object : Callback<List<ApiCar>> {
                override fun onResponse(
                    call: Call<List<ApiCar>>,
                    response: Response<List<ApiCar>>
                ) {
                    val responseBody = (response.body() as List<ApiCar>)
                    continuation.resume(Either.Right(responseBody.map { it.toAppCar() }))
                }

                override fun onFailure(call: Call<List<ApiCar>>, t: Throwable) {
                    continuation.resume(Either.Left(t.message))
                }
            })
        }
}