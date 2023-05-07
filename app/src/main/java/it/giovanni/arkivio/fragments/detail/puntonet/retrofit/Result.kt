package it.giovanni.arkivio.fragments.detail.puntonet.retrofit

/**
 * La classe sealed Result è uno pattern comune in Kotlin che viene utilizzato per rappresentare il
 * risultato di un'operazione che può avere esito positivo o negativo.
 *
 * La classe ha due sottoclassi: Success ed Error. La sottoclasse Success contiene il risultato
 * di un'operazione successful e la sottoclasse Error contiene un messaggio di errore e uno status
 * code HTTP facoltativo.
 *
 * La sottoclasse Success ha un dato di parametro di tipo T che è un parametro di tipo generico di
 * proiezione esterna (out-projection). Il modificatore out indica che il parametro di tipo può
 * apparire solo in posizione esterna (out-position), ovvero come tipo restituito o tipo di argomento
 * di una funzione). La property data contiene il risultato positivo dell'operazione.
 *
 * La sottoclasse Error ha due properties, message e statusCode. La property message è una stringa
 * che contiene un messaggio di errore e la property statusCode è un numero intero facoltativo che
 * contiene uno status code HTTP in caso di errore HTTP.
 *
 * Usando questa classe sealed, è facile gestire il risultato di un'operazione in modo indipendente
 * dai tipi. Ad esempio, se si dispone di una funzione che restituisce un risultato, è possibile
 * utilizzare un'espressione when per gestire sia i casi di Success che quelli di Error in modo
 * conciso e indipendente dai tipi.
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val message: String?, val statusCode: Int? = null) : Result<Nothing>()
}