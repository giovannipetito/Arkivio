package it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.giovanni.arkivio.databinding.PagingItemBinding
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersPagingDataAdapter.ImageViewHolder

/**
 * UsersPagingDataAdapter è un RecyclerView Adapter per la visualizzazione della lista
 * di users (oggetti Data) recuperati dall'API utilizzando la libreria di paging.
 *
 * class UsersPagingDataAdapter : PagingDataAdapter<Data, ImageViewHolder>(diffCallback):
 * questa riga definisce la classe dell'adapter ed eredita dalla classe PagingDataAdapter,
 * che è un adapter predefinito fornito dalla libreria Paging. Accetta due parametri di tipo:
 * il tipo di dati (Data) e il tipo di supporto della vista (ImageViewHolder) e accetta la
 * richiamata diff come parametro.
 *
 * ImageViewHolder: classe interna che definisce il titolare della vista per ogni elemento in
 * RecyclerView. Prende un oggetto PagingItemBinding come parametro, che viene generato
 * dalla libreria ViewBinding per il layout R.layout.paging_item.
 *
 * companion object: definisce il diffCallback per l'adapter. Confronta gli elementi nell'elenco
 * per determinare se sono uguali o meno. Viene utilizzato dall'adapter per aggiornare in modo
 * efficiente l'elenco quando vengono caricati nuovi dati.
 *
 * onCreateViewHolder: questa funzione viene chiamata quando RecyclerView deve creare un nuovo
 * ViewHolder. Gonfia il layout paging_item.xml utilizzando la libreria ViewBinding e restituisce
 * una nuova istanza della classe ImageViewHolder.
 *
 * onBindViewHolder: questa funzione viene chiamata quando RecyclerView deve associare i dati a
 * un ViewHolder. Recupera l'oggetto Data nella posizione specificata utilizzando il metodo
 * getItem fornito dalla libreria Paging e utilizza Glide per caricare l'immagine del character
 * in characterImageView ImageView.
 */
class UsersPagingDataAdapter : PagingDataAdapter<Data, ImageViewHolder>(diffCallback) {

    inner class ImageViewHolder(val binding: PagingItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {

        val diffCallback = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            PagingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currChar = getItem(position)

        holder.binding.apply {

            holder.itemView.apply {
                characterName.text = "${currChar?.firstName}"

                val imageUrl: String? = currChar?.avatar

                Glide.with(context)
                    .load(imageUrl)
                    .into(holder.binding.characterImageView)
            }
        }
    }
}