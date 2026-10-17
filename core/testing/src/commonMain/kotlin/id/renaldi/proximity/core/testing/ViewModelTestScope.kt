package id.renaldi.proximity.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun runViewModelTest(body: suspend TestScope.() -> Unit): TestResult {
    val scheduler = TestCoroutineScheduler()
    Dispatchers.setMain(StandardTestDispatcher(scheduler))
    return try {
        runTest(context = scheduler) { body() }
    } finally {
        Dispatchers.resetMain()
    }
}
