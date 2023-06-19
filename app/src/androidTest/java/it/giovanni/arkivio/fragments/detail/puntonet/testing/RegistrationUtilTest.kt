package it.giovanni.arkivio.fragments.detail.puntonet.testing

import androidx.test.ext.junit.runners.AndroidJUnit4

import com.google.common.truth.Truth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

// import junit.framework.TestCase.assertFalse
// import junit.framework.TestCase.assertTrue

// import org.junit.jupiter.api.Assertions.assertFalse
// import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationUtilTest {

    @Test
    fun validUsernameAndPasswordsReturnTrue() {
        val result = RegistrationUtil.validateRegistrationInput(
            "Giovanni", "123", "123"
        )
        Truth.assertThat(result).isTrue()
        assertTrue(result)
    }

    @Test
    fun emptyUsernameReturnsFalse() {
        val result = RegistrationUtil.validateRegistrationInput(
            "", "123", "123"
        )
        Truth.assertThat(result).isFalse()
        assertFalse(result)
    }

    @Test
    fun emptyPasswordReturnsFalse() {
        val result = RegistrationUtil.validateRegistrationInput(
            "Giovanni", "", ""
        )
        Truth.assertThat(result).isFalse()
        assertFalse(result)
    }

    @Test
    fun usernameAlreadyExistsReturnsFalse() {
        val result = RegistrationUtil.validateRegistrationInput(
            "Luca", "123", "123"
        )
        Truth.assertThat(result).isFalse()
        assertFalse(result)
    }

    @Test
    fun wrongConfirmedPasswordReturnsFalse() {
        val result = RegistrationUtil.validateRegistrationInput(
            "Giovanni", "123", "1234"
        )
        Truth.assertThat(result).isFalse()
        assertFalse(result)
    }

    @Test
    fun wrongPasswordReturnsFalse() {
        val result = RegistrationUtil.validateRegistrationInput(
            "Giovanni", "12", "1234"
        )
        Truth.assertThat(result).isFalse()
        assertFalse(result)
    }
}