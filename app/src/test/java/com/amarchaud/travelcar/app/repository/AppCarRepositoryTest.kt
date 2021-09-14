package com.amarchaud.travelcar.app.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amarchaud.travelcar.app.CoroutineTestRule
import com.amarchaud.travelcar.app.domain.CarFactory.Companion.mockApiCar
import com.amarchaud.travelcar.app.domain.CarFactory.Companion.mockCar
import com.amarchaud.travelcar.data.db.AppDb
import com.amarchaud.travelcar.data.db.TravelCarDao
import com.amarchaud.travelcar.data.remote.TravelCarApi
import com.amarchaud.travelcar.data.repository.car.AppCarRepository
import com.amarchaud.travelcar.domain.db.car.EntityCar
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.domain.remote.car.ApiCar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import retrofit2.Call
import retrofit2.Response
import retrofit2.mock.Calls
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class) // Failed to instantiate test runner class androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner ??????
class AppCarRepositoryTest {

    private lateinit var mockRepo: AppCarRepository
    private lateinit var userDao: TravelCarDao
    private lateinit var db: AppDb

    @ExperimentalCoroutinesApi
    @get:Rule
    val testCoroutineRule = CoroutineTestRule()

    @Mock
    lateinit var carApiMock: TravelCarApi


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDb::class.java).allowMainThreadQueries().build()
        userDao = db.getCarDao()

        mockRepo = AppCarRepository(carApiMock, userDao, testCoroutineRule.dispatcher)
    }


    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun `getCarsFromDbFlow - ok`() = runBlocking {

        val response= Response.success((listOf(mockApiCar())))
        val mockCall = Calls.response(response)
        Mockito.`when`(carApiMock.getCars()).thenReturn(mockCall)

        userDao.addCar(
            EntityCar(
                make = "Ferrari",
                model = "Stradale",
                year = 2020,
                picture = null,
                equipments = listOf("GPS", "21 pouces")
            )
        )
        userDao.addCar(
            EntityCar(
                make = "Renault",
                model = "Megane",
                year = 2000,
                picture = null,
                equipments = listOf("GPS")
            )
        )

        val listCars = mockRepo.getCarsFlow().first()
        Assert.assertTrue(!listCars.isNullOrEmpty())
        Assert.assertTrue(listCars?.size == 2)
        Assert.assertTrue(listCars?.get(0)?.make == "Ferrari")
    }
}