package it.giovanni.arkivio.puntonet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import it.giovanni.arkivio.getOrAwaitValue
import it.giovanni.arkivio.puntonet.room.dao.UserCoroutinesDao
import it.giovanni.arkivio.puntonet.room.database.CoreDatabase
import it.giovanni.arkivio.puntonet.room.entity.User
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest // Unit Test
// @MediumTest // Instrumented Test
// @LargeTest // UI Test
class UserCoroutinesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: CoreDatabase
    private lateinit var dao: UserCoroutinesDao

    @Before
    fun setup() {
        // inMemoryDatabaseBuilder: The data are not saved in the persistence storage,
        // but in the volatile memory (RAM).
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CoreDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.userCoroutinesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUserTest() = runTest {
        val user = User(1, "Giovanni", "Petito", "35")
        dao.insertUser(user)

        // 1° test:
        val liveDataUsers: LiveData<List<User>> = dao.getUsers1()
        val users1 = liveDataUsers.getOrAwaitValue() // getOrAwaitValue: extension function.
        assertThat(users1).contains(user)

        // 2° test:
        val users2 = dao.getUsers2()
        assertThat(users2).contains(user)
    }

    @Test
    fun deleteUserTest() = runTest {
        val user = User(1, "Giovanni", "Petito", "35")
        dao.insertUser(user)
        dao.deleteUser(user)

        val liveDataUsers: LiveData<List<User>> = dao.getUsers1()
        val users: List<User> = liveDataUsers.getOrAwaitValue()
        assertThat(users).doesNotContain(user)
    }

    @Test
    fun checkUsersAgeTest() = runTest {
        val user1 = User(1, "Giovanni", "Petito", "35")
        val user2 = User(2, "Mariano", "Pinto", "35")
        val user3 = User(3, "Daniele", "Musacchia", "36")
        dao.insertUser(user1)
        dao.insertUser(user2)
        dao.insertUser(user3)

        val users: List<User> = dao.getUsers1().getOrAwaitValue()

        var totalAge = 0
        for (user in users) {
            totalAge += user.age.toInt()
        }

        val averageAge = totalAge/users.size

        assertThat(averageAge > 18).isTrue()
    }
}