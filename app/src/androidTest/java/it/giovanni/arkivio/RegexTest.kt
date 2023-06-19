package it.giovanni.arkivio

import androidx.test.ext.junit.runners.AndroidJUnit4
import it.giovanni.arkivio.utils.Patterns
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegexTest {

    @Test
    fun checkEmailRegex() {

        val email1 = "mario.rossi@gmail.com"
        val email2 = "mario_rossi@gmail.com"
        val email3 = "mario-rossi@gmail.com"
        val email4 = "mario@gmail.com"

        val email5 = "mario.@gmail.com"
        val email6 = ".mario.rossi@gmail.com"
        val email7 = "màrio.rossi@gmail.com"
        val email8 = "mario.rossi.@gmail.com"
        val email9 = "dghardas2009..@gmail.com"
        val email10 = "apaoa119@gmail..com"
        val email11 = "sadków_sp@wp.pl"
        val email12 = "prabhakar.koli.@tiss.edu"
        val email13 = "kovicitycenter.@gmail.com"
        val email14 = "smoleńskiego25@ioptimum.pl."
        val email15 = "ståle@tronrudeiendom.no"
        val email16 = "vj.radhakrishan.@gmail.com"

        Assert.assertTrue(Patterns.emailPattern.matcher(email1).matches())
        Assert.assertTrue(Patterns.emailPattern.matcher(email2).matches())
        Assert.assertTrue(Patterns.emailPattern.matcher(email3).matches())
        Assert.assertTrue(Patterns.emailPattern.matcher(email4).matches())

        Assert.assertFalse(Patterns.emailPattern.matcher(email5).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email6).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email7).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email8).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email9).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email10).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email11).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email12).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email13).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email14).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email15).matches())
        Assert.assertFalse(Patterns.emailPattern.matcher(email16).matches())
    }
}