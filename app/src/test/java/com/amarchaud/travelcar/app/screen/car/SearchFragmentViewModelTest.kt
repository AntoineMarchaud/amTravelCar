package com.amarchaud.travelcar.app.screen.car

import android.app.Application
import android.net.Uri
import androidx.annotation.CallSuper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.amarchaud.travelcar.app.CoroutineTestRule
import com.amarchaud.travelcar.app.domain.CarFactory.Companion.mockCar
import com.amarchaud.travelcar.data.repository.car.CarRepository
import com.amarchaud.travelcar.domain.local.car.AppCar
import com.amarchaud.travelcar.ui.car.search.SearchFragmentViewModel
import com.amarchaud.travelcar.ui.car.search.model.CarListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class SearchFragmentViewModelTest {

    @get:Rule
    val rule = CoroutineTestRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val carsSimulateUpdateFlow = flow {
        emit(
            listOf(mockCar(), mockCar(), mockCar(), mockCar(), mockCar())
        )
        kotlinx.coroutines.delay(300)
        emit(
            listOf(mockCar(), mockCar()) // simulate update by the endpoint
        )
    }

    private val carsFlow = flow {
        emit(
            listOf(
                AppCar(
                    "Peugeot",
                    "508 PSE",
                    2021,
                    Uri.parse("https://image1"),
                    listOf("GPS", "Caméra de recul", "Affichage tête haute")
                ), AppCar(
                    "Bugatti",
                    "Chiron",
                    2016,
                    Uri.parse("https://image2"),
                    listOf("DSG7", "Cuir", "Ligne inox")
                ), AppCar(
                    "BMW",
                    "M240i",
                    2015,
                    Uri.parse("https://image2"),
                    listOf("Auto-parking", "ConnectedDrive", "Pack M")
                )
            )
        )
    }

    private lateinit var viewModel: SearchFragmentViewModel

    @Mock
    lateinit var applicationMock: Application

    @Mock
    lateinit var carRepositoryMock: CarRepository

    @Before
    @CallSuper
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `Int flow Ok`() = rule.dispatcher.runBlockingTest {

        Mockito.`when`(carRepositoryMock.getCarsFlow()).thenReturn(carsSimulateUpdateFlow)
        viewModel = SearchFragmentViewModel(applicationMock, carRepositoryMock)

        val result = arrayListOf<List<CarListItem>>()
        val job = launch { viewModel.cars.toList(result) }

        Assert.assertTrue(result.size == 1)

        // first emit
        with(result[0]) {
            Assert.assertTrue(size == 5)
            this.forEach {
                Assert.assertTrue(it is CarListItem.Car)
            }
        }

        advanceTimeBy(300)

        with(result[1]) {
            Assert.assertTrue(size == 2)
            this.forEach {
                Assert.assertTrue(it is CarListItem.Car)
            }
        }

        job.cancel()
    }

    @Test
    fun `filterNoSuggestion Ok`() = rule.dispatcher.runBlockingTest {

        Mockito.`when`(carRepositoryMock.getCarsFlow()).thenReturn(carsFlow)
        viewModel = SearchFragmentViewModel(applicationMock, carRepositoryMock)

        viewModel.filterWithSuggestions("Peu")

        var job: Job?
        val result = arrayListOf<List<CarListItem>>()

        result.clear()
        job = launch { viewModel.cars.toList(result) }
        Assert.assertTrue(result.size == 1)
        result[0].let {
            Assert.assertTrue(it.size == 1)
            it[0].let {
                (it as CarListItem.Car).let {
                    Assert.assertTrue(it.appCar.make == "Peugeot")
                }
            }
        }
        job.cancel()


        result.clear()
        viewModel.filterWithSuggestions("atti")
        job = launch { viewModel.cars.toList(result) }
        Assert.assertTrue(result.size == 1)
        result[0].let {
            Assert.assertTrue(it.size == 1)
            it[0].let {
                (it as CarListItem.Car).let {
                    Assert.assertTrue(it.appCar.make == "Bugatti")
                }
            }
        }
        job.cancel()

        result.clear()
        viewModel.filterWithSuggestions("20")
        job = launch { viewModel.cars.toList(result) }
        Assert.assertTrue(result.size == 1)
        result[0].let {
            Assert.assertTrue(it.size == 3)
        }
        job.cancel()
    }

    @Test
    fun `filterWithSuggestion Ok`() = rule.dispatcher.runBlockingTest {

        Mockito.`when`(carRepositoryMock.getCarsFlow()).thenReturn(carsFlow)
        viewModel = SearchFragmentViewModel(applicationMock, carRepositoryMock)

        val job: Job?
        val result = arrayListOf<List<CarListItem>>()

        viewModel.filterWithSuggestions("Peugoet", listOf("Peugeot", "Peujeot", "Peugeoc"))

        result.clear()
        job = launch { viewModel.cars.toList(result) }
        Assert.assertTrue(result.size == 1)
        result[0].let {
            Assert.assertTrue(it.size == 1)
            it[0].let {
                (it as CarListItem.Car).let {
                    Assert.assertTrue(it.appCar.make == "Peugeot")
                }
            }
        }
        job.cancel()

    }

    @Test
    fun `filterWithSuggestion Ko`() = rule.dispatcher.runBlockingTest {

        Mockito.`when`(carRepositoryMock.getCarsFlow()).thenReturn(carsFlow)
        viewModel = SearchFragmentViewModel(applicationMock, carRepositoryMock)

        val job: Job?
        val result = arrayListOf<List<CarListItem>>()

        viewModel.filterWithSuggestions("sdoufhuisdlofhioulmdzsfoidlmhs")

        result.clear()
        job = launch { viewModel.cars.toList(result) }
        Assert.assertTrue(result.size == 1)
        result[0].let {
            Assert.assertTrue(it.size == 1)
            Assert.assertTrue(it[0] is CarListItem.Nothing)
        }
        job.cancel()

    }
}
