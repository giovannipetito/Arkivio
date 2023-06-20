package it.giovanni.arkivio

import androidx.test.ext.junit.runners.AndroidJUnit4
import it.giovanni.arkivio.utils.Utils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegexTest {

    @Test
    fun checkEmailTest() {

        assertTrue(Utils.checkEmail("giovanni@gmail.com"))
        assertTrue(Utils.checkEmail("giovanni.petito@gmail.com"))
        assertTrue(Utils.checkEmail("giovanni_petito@gmail.com"))
        assertTrue(Utils.checkEmail("giovanni-petito@gmail.com"))

        assertFalse(Utils.checkEmail("giovanni.@gmail.com"))
        assertFalse(Utils.checkEmail("gióvanni_petito@wp.pl"))
        assertFalse(Utils.checkEmail("giovanni1988@gmail..com"))
        assertFalse(Utils.checkEmail("giovanni1988..@gmail.com"))
        assertFalse(Utils.checkEmail("giovànni.petito@gmail.com"))
        assertFalse(Utils.checkEmail("giovanni.petito.@tiss.edu"))
        assertFalse(Utils.checkEmail("giovånni@tronrudeiendom.no"))
        assertFalse(Utils.checkEmail("giovanni.petito.@gmail.com"))
        assertFalse(Utils.checkEmail("giovanni. petito@gmail.com"))
        assertFalse(Utils.checkEmail("giovanni.petito @gmail.com"))
        assertFalse(Utils.checkEmail(".giovanni.petito@gmail.com"))
        assertFalse(Utils.checkEmail("giovanni.petito.@gmail.com"))
        assertFalse(Utils.checkEmail("giovanńipetito88@optimum.pl."))
    }
}