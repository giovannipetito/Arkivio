package it.giovanni.arkivio.fragments.detail.puntonet.dagger

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UserCardBinding
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.Data

class UsersAdapter(onItemClicked: OnItemViewClicked) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private var onItemViewClicked : OnItemViewClicked? = onItemClicked
    private var users : List<Data>? = null

    fun setList(list: List<Data>?) {
        users = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val userCardBinding: UserCardBinding = UserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(userCardBinding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {

        val user : Data = users!![position]
        holder.firstName.text = user.firstName
        holder.lastName.text = user.lastName

        val imageUrl: String = user.avatar
        Glide.with(context)
            .load(imageUrl)
            .into(holder.avatar)

        holder.userCard.setOnClickListener {
            onItemViewClicked?.onItemInfoClicked(user)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.user_card
    }

    override fun getItemCount(): Int {
        return if (users == null) 0
        else users?.size!!
    }

    inner class UsersViewHolder(userCardBinding: UserCardBinding) : RecyclerView.ViewHolder(userCardBinding.root) {

        internal var userCard: CardView = userCardBinding.userCard
        internal var firstName: TextView = userCardBinding.getPostUserFirstName
        internal var lastName: TextView = userCardBinding.getPostUserLastName
        internal var avatar: ImageView = userCardBinding.userAvatar
    }

    interface OnItemViewClicked {
        fun onItemInfoClicked(user: Data)
    }
}