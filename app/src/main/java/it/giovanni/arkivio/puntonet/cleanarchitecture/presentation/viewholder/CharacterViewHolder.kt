package it.giovanni.arkivio.puntonet.cleanarchitecture.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.RickMortyItemBinding

class CharacterViewHolder(val binding: RickMortyItemBinding) : RecyclerView.ViewHolder(binding.root)

/**
 *
 * Posso dichiarare CharacterViewHolder all'interno di CharacterAdapter come inner class e scriverlo
 * nel modo seguente:
 *
inner class CharacterViewHolder(private val binding: RickMortyItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RickMorty?) {
        binding.let {
            item.let { rickMorty ->
                it.characterName.text = rickMorty?.name

                val imageUrl: String? = rickMorty?.image
                Glide.with(App.context)
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
*/