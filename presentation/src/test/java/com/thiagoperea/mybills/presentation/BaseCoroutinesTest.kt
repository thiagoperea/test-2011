package com.thiagoperea.mybills.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
abstract class BaseCoroutinesTest {

    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = CoroutineTestRule(dispatcher)
}

@ExperimentalCoroutinesApi
class CoroutineTestRule(
    private val dispatcher: TestCoroutineDispatcher,
) : TestWatcher() {


    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }
}