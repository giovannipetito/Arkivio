package it.giovanni.arkivio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("it.giovanni.arkivio", appContext.packageName)
    }

    @Test
    fun useAppContext2() {
        // Context of the app under test.
        val context = ApplicationProvider.getApplicationContext<Context>()
        Truth.assertThat("it.giovanni.arkivio" == context.packageName).isTrue()
    }
}