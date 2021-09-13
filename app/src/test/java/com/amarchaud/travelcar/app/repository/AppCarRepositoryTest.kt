package com.amarchaud.travelcar.app.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amarchaud.travelcar.data.db.AppDb
import com.amarchaud.travelcar.data.db.TravelCarDao
import com.amarchaud.travelcar.data.remote.TravelCarApi
import com.amarchaud.travelcar.data.repository.car.AppCarRepository
import com.amarchaud.travelcar.domain.db.car.EntityCar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class) // Failed to instantiate test runner class androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner ??????
class AppCarRepositoryTest {

    private lateinit var mockRepo: AppCarRepository
    private lateinit var userDao: TravelCarDao
    private lateinit var db: AppDb
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var carApi: TravelCarApi

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDb::class.java).build()
        userDao = db.getCarDao()
    }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockRepo = AppCarRepository(carApi, userDao, testDispatcher)
    }


    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun `getCarsFromDbFlow - ok`() = runBlockingTest {

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
    }
}