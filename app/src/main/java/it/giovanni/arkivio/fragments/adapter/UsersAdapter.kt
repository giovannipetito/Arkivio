package it.giovanni.arkivio.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.App.Companion.context
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.user.User
import it.giovanni.arkivio.fragments.viewholder.UsersViewHolder
import it.giovanni.arkivio.utils.ColorGenerator.Companion.generate
import it.giovanni.arkivio.utils.Utils.Companion.turnArrayListToString

class UsersAdapter(onItemClicked: OnItemViewClicked) : RecyclerView.Adapter<UsersViewHolder>() {

    private var onItemViewClicked : OnItemViewClicked? = onItemClicked
    private var users : ArrayList<User>? = null

    fun setList(list: ArrayList<User>?) {
        users = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {

        val color = generate()
        holder.userRound.setColorFilter(ResourcesCompat.getColor(context.resources, color, null))

        val user : User = users!![position]
        val name = user.nome + " " + user.cognome
        holder.nome.text = name
        holder.email.text = turnArrayListToString(user.emails!!)

        holder.userInfoContainer.setOnClickListener {
            onItemViewClicked?.onItemInfoClicked(user, users)
        }
        holder.addUserContainer.setOnClickListener {
            onItemViewClicked?.onItemClicked(user, color)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.rubrica_item
    }

    override fun getItemCount(): Int {
        return if (users == null) 0
        else users?.size!!
    }

    interface OnItemViewClicked {
        fun onItemInfoClicked(user: User, list: ArrayList<User>?)
        fun onItemClicked(user: User, color: Int?)
    }
}