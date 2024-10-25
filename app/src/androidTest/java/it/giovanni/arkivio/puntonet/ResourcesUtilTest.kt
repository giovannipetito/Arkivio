package it.giovanni.arkivio.puntonet

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import it.giovanni.arkivio.R
import it.giovanni.arkivio.puntonet.testing.ResourcesUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourcesUtilTest {

    private lateinit var resourcesUtil: ResourcesUtil

    @Before
    fun setup() {
        resourcesUtil = ResourcesUtil()
    }

    @After
    fun tearDown() {
        // This case is handled by garbage collector, but in general use tearDown to destroy
        // resources and avoid memory leak.
    }

    @Test
    fun isEqualTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result1 = resourcesUtil.isEqual(context, R.string.app_name, "Arkivio")
        val result2 = resourcesUtil.isEqual(context, R.string.app_name, "Archivio")
        Truth.assertThat(result1).isTrue()
        Truth.assertThat(result2).isFalse()
    }
}