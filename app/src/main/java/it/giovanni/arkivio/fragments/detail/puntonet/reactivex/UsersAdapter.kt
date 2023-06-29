package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

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
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.User

class UsersAdapter(onItemClicked: OnItemViewClicked) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    private var onItemViewClicked : OnItemViewClicked? = onItemClicked
    private var users : List<User?>? = null

    fun setList(list: List<User?>?) {
        users = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val userCardBinding: UserCardBinding = UserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(userCardBinding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {

        val user : User? = users?.get(position)
        holder.firstName.text = user?.firstName
        holder.lastName.text = user?.lastName

        val imageUrl: String = user?.avatar!!
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
        internal var firstName: TextView = userCardBinding.userFirstName
        internal var lastName: TextView = userCardBinding.userLastName
        internal var avatar: ImageView = userCardBinding.userAvatar
    }

    interface OnItemViewClicked {
        fun onItemInfoClicked(user: User)
    }
}