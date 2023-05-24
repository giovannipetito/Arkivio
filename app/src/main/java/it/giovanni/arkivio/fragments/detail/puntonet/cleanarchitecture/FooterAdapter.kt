package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.FooterItemBinding

class FooterAdapter(private val retry: () -> Unit) : LoadStateAdapter<FooterAdapter.FooterItemViewHolder>() {

    inner class FooterItemViewHolder(private val binding: FooterItemBinding, private val retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {

            binding.footerProgressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            binding.footerError.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            binding.footerError.text = if (loadState is LoadState.Error) loadState.error.localizedMessage else "Some Error Occurred"
            binding.footerRetry.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE

            binding.footerRetry.setOnClickListener {
                retry()
            }
        }
    }

    override fun onBindViewHolder(holder: FooterItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = FooterItemViewHolder(
        FooterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), retry)
}