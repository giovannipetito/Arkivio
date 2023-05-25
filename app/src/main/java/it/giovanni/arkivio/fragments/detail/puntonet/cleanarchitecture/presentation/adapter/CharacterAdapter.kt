package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import it.giovanni.arkivio.App
import it.giovanni.arkivio.databinding.RickMortyItemBinding
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.model.RickMorty
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.presentation.viewholder.CharacterViewHolder

/**
 * CharacterAdapter è un RecyclerView Adapter per la visualizzazione della lista di characters
 * (oggetti RickMorty) recuperati dall'API utilizzando la libreria di paging.
 *
 * class CharacterAdapter : PagingDataAdapter<RickMorty, CharacterViewHolder>(diffCallback):
 * questa riga definisce la classe dell'adapter ed eredita dalla classe PagingDataAdapter, che è
 * un adapter predefinito fornito dalla libreria Paging. Accetta due parametri di tipo: il tipo
 * di dati (RickMorty) e il tipo di supporto della vista (CharacterViewHolder) e accetta la
 * richiamata diff come parametro.
 *
 * CharacterViewHolder: classe interna che definisce il titolare della vista per ogni elemento
 * in RecyclerView. Prende un oggetto RickMortyItemBinding come parametro, che viene generato
 * dalla libreria ViewBinding per il layout R.layout.paging_item.
 *
 * companion object: definisce il diffCallback per l'adapter. Confronta gli elementi nell'elenco
 * per determinare se sono uguali o meno. Viene utilizzato dall'adapter per aggiornare in modo
 * efficiente l'elenco quando vengono caricati nuovi dati.
 *
 * onCreateViewHolder: questa funzione viene chiamata quando RecyclerView deve creare un nuovo
 * ViewHolder. Gonfia il layout paging_item.xml utilizzando la libreria ViewBinding e restituisce
 * una nuova istanza della classe CharacterViewHolder.
 *
 * onBindViewHolder: questa funzione viene chiamata quando RecyclerView deve associare i dati a
 * un ViewHolder. Recupera l'oggetto RickMorty nella posizione specificata utilizzando il metodo
 * getItem fornito dalla libreria Paging e utilizza Glide per caricare l'immagine del character
 * in characterImageView ImageView.
 */
class CharacterAdapter(private val clicked: (RickMorty) -> Unit) :
    PagingDataAdapter<RickMorty, CharacterViewHolder>(PlayersDiffCallback()) { // Oppure: diffCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding: RickMortyItemBinding = RickMortyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val currentCharacter: RickMorty? = getItem(position)
        holder.binding.let {
            holder.itemView.apply {
                currentCharacter.let { rickMorty ->
                    it.characterName.text = rickMorty?.name

                    val imageUrl: String? = rickMorty?.image
                    Glide.with(App.context)
                        .load(imageUrl)
                        .into(it.characterImage)

                    it.root.setOnClickListener {
                        if (rickMorty != null)
                            clicked.invoke(rickMorty)
                    }
                }
            }
        }
    }

    /**
     *
     * Se dichiaro CharacterViewHolder all'interno di CharacterAdapter, allora onBindViewHolder si
     * riduce alla forma seguente:
     *
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val currentCharacter: RickMorty? = getItem(position)
        holder.bind(currentCharacter)
    }
    */

    private class PlayersDiffCallback : DiffUtil.ItemCallback<RickMorty>() {
        override fun areItemsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<RickMorty>() {
            override fun areItemsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
                return oldItem == newItem
            }
        }
    }
}