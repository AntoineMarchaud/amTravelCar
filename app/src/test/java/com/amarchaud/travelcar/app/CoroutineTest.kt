package com.amarchaud.travelcar.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

open class CoroutineTest {


    @ExperimentalCoroutinesApi
    fun launch(block: suspend TestCoroutineScope.() -> Unit) {
        testcoroutineRule.runBlockingTest(block)
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    val testcoroutineRule = CoroutineTestRule()


    @ExperimentalCoroutinesApi
    inner class CoroutineTestRule constructor(private val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) : TestWatcher(),
        TestCoroutineScope by TestCoroutineScope(testDispatcher) {

        override fun starting(description: Description?) {
            super.starting(description)
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description?) {
            super.finished(description)
            Dispatchers.resetMain()
            cleanupTestCoroutines()
        }
    }
}