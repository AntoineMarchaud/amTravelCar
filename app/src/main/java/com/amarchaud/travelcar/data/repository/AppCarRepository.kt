package com.amarchaud.travelcar.data.repository

import com.amarchaud.travelcar.data.db.TravelCarDao
import com.amarchaud.travelcar.data.models.EntityCarOptionCrossRef
import com.amarchaud.travelcar.data.models.EntityOption
import com.amarchaud.travelcar.data.remote.TravelCarApi
import com.amarchaud.travelcar.data.remote.apiResult
import com.amarchaud.travelcar.di.DispatcherModule
import com.amarchaud.travelcar.domain.models.AppCar
import com.amarchaud.travelcar.domain.repository.CarRepository
import com.amarchaud.travelcar.utils.translator.CarTranslator.toAppCar
import com.amarchaud.travelcar.utils.translator.CarTranslator.toEntityCar
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppCarRepository @Inject constructor(
    private val travelCarApi: TravelCarApi,
    private val travelCarDao: TravelCarDao,
    @DispatcherModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
    }.flowOn(ioDispatcher)

    /**
     * first emit DB
     * then try to call endpoint, update db and emit it again
     */
    override fun getCarsFlow() = flow {
        emit(getCarsFromDb())
        if (update()) {
            emit(getCarsFromDb())
        }
    }.flowOn(ioDispatcher)

    /**
     * call api and refresh db
     */
    override suspend fun update(): Boolean = withContext(Dispatchers.IO) {

        var isUpdate = false

        val res = getCarsFromApi()
        if (res.isSuccess) {

            if (getCarsFromDb().isNullOrEmpty()) { // first time : add all
                //travelCarDao.addCars(res.value.map { it.toEntityCar() })

                res.getOrNull()?.forEach { oneCar ->
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
                res.getOrNull()?.forEach {
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

            isUpdate = true
        }

        isUpdate
    }

    /**
     * Get cars from DB only
     */
    private suspend fun getCarsFromDb(): List<AppCar>? = withContext(Dispatchers.IO) {
        travelCarDao.getCarsWithOptions()?.map { it.toAppCar() }
    }

    /**
     * Get cars from api only
     */
    private suspend fun getCarsFromApi(): Result<List<AppCar>> {
        return kotlin.runCatching {
            apiResult(travelCarApi.getCars()).getOrThrow()
        }.fold(
            onSuccess = { it ->
                Result.success(it.map { it.toAppCar() })
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}