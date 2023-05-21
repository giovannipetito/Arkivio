package it.giovanni.arkivio.fragments.detail.puntonet.dependencyinjection

import android.util.Log

/**
 * DEPENDENCY INJECTION
 *
 * Dependency Injection (DI) is a design pattern used in software development for improving the
 * quality and the maintainability of your code. DI is a way of structuring your code, so that is
 * easier to maintain, test and modify it over time. Its main purpose is to reduce the coupling
 * between different components in your code. That means that instead of creating and managing
 * objects directly within your code, you delegate that responsibility to a separate component
 * called Dependency Injection Container (DIP). This container takes care of creating and managing
 * all your different objects that your code needs allowing you to focus on writing the code that
 * does the work. So, a class does require references to other classes to work.

 * With the Standard class implementation, the classes PC and Motherboard are tightly coupled and
 * no subclasses or alternative implementations can easily be used, so that's where the Dependency
 * Injection comes in. With DI we can change the code by removing the instance of the Motherboard
 * class from the PC class itself and moving that class inside the constructor instead. That way,
 * when we are constructing the PC, we would have an option to customize what kind of Motherboard
 * we want to construct with a PC, rather then using only a single one. That would make our code a
 * lot more testable and maintainable. Also we can replace the real Motherboard with a fake one just
 * to test our PC class.
 *
 * There are different ways to implement DI in your code:
 * - Constructor Injection (CI): the dependencies of a component are passed in a parameter of the
 *   actual class.
 * - Field Injection (FI): the activities and the fragments are instantiated by the system itself,
 *   so the CI is not possible. And that's where FI comes into play.
 */

object ConstructorInjection {

    fun main(): String {
        val asusMotherboard = AsusMotherboard()
        val fakeMotherboard = FakeMotherboard()

        val pc = PC(motherBoard = asusMotherboard)
        return pc.start()
    }

    class PC(private val motherBoard: Motherboard? = null) {
        fun start(): String {
            return motherBoard?.powerOn()!!
        }
    }

    class AsusMotherboard: Motherboard {
        override fun powerOn(): String {
            val output = "Asus Motherboard turning on the computer..."
            Log.i("[DI]", output)
            return output
        }
    }

    class FakeMotherboard: Motherboard {
        override fun powerOn(): String {
            val output = "Fake Motherboard turning on the computer..."
            Log.i("[DI]", output)
            return output
        }
    }

    interface Motherboard {
        fun powerOn(): String
    }
}