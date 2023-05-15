package it.giovanni.arkivio.fragments.detail.puntonet.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.databinding.RickMortyItemBinding

/**
 * CharacterAdapter è un RecyclerView Adapter per la visualizzazione della lista di characters
 * (oggetti RickMorty) recuperati dall'API utilizzando la libreria di paging.
 *
 * class CharacterAdapter : PagingDataAdapter<RickMorty, CharacterItemViewHolder>(diffCallback):
 * questa riga definisce la classe dell'adapter ed eredita dalla classe PagingDataAdapter, che è
 * un adapter predefinito fornito dalla libreria Paging. Accetta due parametri di tipo: il tipo
 * di dati (RickMorty) e il tipo di supporto della vista (CharacterItemViewHolder) e accetta la
 * richiamata diff come parametro.
 *
 * CharacterItemViewHolder: classe interna che definisce il titolare della vista per ogni elemento
 * in RecyclerView. Prende un oggetto RickMortyItemBinding come parametro, che viene generato
 * dalla libreria ViewBinding per il layout R.layout.paging_item.
 *
 * companion object: definisce il diffCallback per l'adapter. Confronta gli elementi nell'elenco
 * per determinare se sono uguali o meno. Viene utilizzato dall'adapter per aggiornare in modo
 * efficiente l'elenco quando vengono caricati nuovi dati.
 *
 * onCreateViewHolder: questa funzione viene chiamata quando RecyclerView deve creare un nuovo
 * ViewHolder. Gonfia il layout paging_item.xml utilizzando la libreria ViewBinding e restituisce
 * una nuova istanza della classe CharacterItemViewHolder.
 *
 * onBindViewHolder: questa funzione viene chiamata quando RecyclerView deve associare i dati a
 * un ViewHolder. Recupera l'oggetto RickMorty nella posizione specificata utilizzando il metodo
 * getItem fornito dalla libreria Paging e utilizza Glide per caricare l'immagine del character
 * in characterImageView ImageView.
 */
class CharacterAdapter(private val clicked: (RickMorty) -> Unit) : PagingDataAdapter<RickMorty, CharacterAdapter.CharacterItemViewHolder>(PlayersDiffCallback()) {

    inner class CharacterItemViewHolder(private val binding: RickMortyItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RickMorty?) {
            binding.let {
                item.let { rickMorty ->
                    it.characterName.text = rickMorty?.name

                    val imageUrl: String? = rickMorty?.image
                    Glide.with(context)
                        .load(imageUrl)
                        .into(it.characterImageView)

                    it.root.setOnClickListener {
                        if (rickMorty != null)
                            clicked.invoke(rickMorty)
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterItemViewHolder {
        val binding: RickMortyItemBinding = RickMortyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterItemViewHolder, position: Int) {

        val currentCharacter: RickMorty? = getItem(position)
        holder.bind(currentCharacter)
    }

    private class PlayersDiffCallback : DiffUtil.ItemCallback<RickMorty>() {
        override fun areItemsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
            return oldItem == newItem
        }
    }
}