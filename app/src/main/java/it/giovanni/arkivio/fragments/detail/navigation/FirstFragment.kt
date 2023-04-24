package it.giovanni.arkivio.fragments.detail.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var layoutBinding: FragmentFirstBinding? = null
    private val binding get() = layoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        layoutBinding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding?.root

        // val view = inflater.inflate(R.layout.fragment_first, container, false)
        // return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.firstButton?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_secondFragment)
        }
    }
}

/**
 * La classe Model è dove definirai i dati con cui lavorerà la tua app. Ad esempio, se stai creando
 * un'app del meteo, il tuo model potrebbe avere campi come temperature, humidity e windSpeed.
 */
// data class Weather(val temperature: Double, val humidity: Double, val windSpeed: Double)

/**
 * La classe ViewModel è la classe dove definirai la logica che la tua app utilizzerà per interagire
 * con il tuo model. Ad esempio, se desideri recuperare i dati meteorologici da un'API, puoi definire
 * nel tuo ViewModel la funzione fetchWeatherData() che utilizza Retrofit e Coroutines.
 *
 * In questo esempio, weatherService è un servizio Retrofit che è stato definito per effettuare richieste
 * di rete (network request) a un'API che restituisce i dati meteorologici per una determinata città.
 *
 * Il metodo getWeatherData() è una funzione suspend che utilizza Coroutines per effettuare la request
 * e restituisce un oggetto Response<WeatherData>.
 *
 * Il metodo fetchWeatherData() è una funzione suspend che utilizza Coroutines per effettuare la request
 * e restituisce un oggetto Result<WeatherData>. La classe Result è una classe personalizzata che può
 * contenere un risultato di successo (Result.Success) o un risultato di errore (Result.Error).
 *
 * Nell'implementazione, per prima cosa effettuiamo la request utilizzando il metodo getWeatherData()
 * e controlliamo se la response ha avuto successo. In caso affermativo, restituiamo un oggetto
 * Result.Success con i dati meteorologici. Se invece, durante la request viene rilevata un'eccezione,
 * restituiamo un oggetto Result.Error con l'eccezione rilevata (con il messaggio di eccezione).
 */
/*
class WeatherViewModel : ViewModel() {

    private val weatherData = MutableLiveData<Weather>()

    suspend fun fetchWeatherData(city: String): Result<WeatherData> {
        // 1. Make API call to fetch weather data
        // 2. Update weatherData LiveData with the fetched data
        return try {
            val response = weatherService.getWeatherData(city)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
*/
/**
 * La classe View è dove definirai l'interfaccia utente che la tua app mostrerà all'utente.
 * Ad esempio, potresti avere un file di layout che include un TextView per visualizzare la
 * temperatura, un ProgressBar per visualizzare l'umidità e così via. Nella tua activity o
 * fragment, legherai poi questi elementi dell'interfaccia utente al ViewModel.
 *
 * Il ViewModel osserva un oggetto LiveData chiamato weatherData, che viene aggiornato quando viene
 * chiamato fetchWeatherData(). La view quindi osserva l'oggetto weatherData e aggiorna la UI quando
 * cambia. Ciò consente di separare il codice dell'interfaccia utente dalla business logic e
 * semplifica il test e la manutenzione del codice.
 */
/*
class WeatherActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        viewModel.weatherData.observe(this, { weather ->
        // TODO: Update UI with new weather data
        })

        buttonFetchWeather.setOnClickListener {
            viewModel.fetchWeatherData()
        }
    }
}
*/