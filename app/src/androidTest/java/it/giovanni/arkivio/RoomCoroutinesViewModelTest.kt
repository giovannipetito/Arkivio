package it.giovanni.arkivio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import it.giovanni.arkivio.fragments.detail.puntonet.room.entity.User
import it.giovanni.arkivio.fragments.detail.puntonet.room.repository.RoomCoroutinesRepository
import it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodel.RoomCoroutinesViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
@SmallTest
class RoomCoroutinesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepository: RoomCoroutinesRepository

    private lateinit var viewModel: RoomCoroutinesViewModel

    @Before
    fun setup() {
        // Initialize Mockito
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getUsersTest() {
        // Create a list of users for testing
        val users: List<User> = listOf(
            User(1, "Giovanni", "Petito", "35"),
            User(2, "Mariano", "Pinto", "35"),
            User(3, "Daniele", "Musacchia", "36")
        )

        val liveDataUsers: MutableLiveData<List<User>> = MutableLiveData<List<User>>().apply {
            value = users
        }

        // Mock the repository's behavior
        `when`(mockRepository.getUsers1()).thenReturn(liveDataUsers)

        // Create an instance of the ViewModel with the mocked repository
        viewModel = RoomCoroutinesViewModel(ApplicationProvider.getApplicationContext())

        // Call the method to fetch users
        viewModel.getUsers()

        // Verify that the repository's getUsers method was called
        verify(mockRepository).getUsers1()

        // Verify that the ViewModel's users contains the expected data
        assert(viewModel.users.value == users)
    }
}