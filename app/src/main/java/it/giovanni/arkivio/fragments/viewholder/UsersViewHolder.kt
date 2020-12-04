package it.giovanni.arkivio.fragments.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var userInfoContainer: RelativeLayout = itemView.findViewById(R.id.user_info_container)
    internal var userRound: ImageView = itemView.findViewById(R.id.user_round)
    internal var addUserContainer: RelativeLayout = itemView.findViewById(R.id.add_user_container)
    internal var nome: TextView = itemView.findViewById(R.id.user_name)
    internal var email: TextView = itemView.findViewById(R.id.user_email)
}