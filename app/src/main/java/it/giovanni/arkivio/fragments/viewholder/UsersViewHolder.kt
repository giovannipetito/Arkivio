package it.giovanni.arkivio.fragments.viewholder

import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.databinding.RubricaItemBinding

class UsersViewHolder(rubricaItemBinding: RubricaItemBinding) : RecyclerView.ViewHolder(rubricaItemBinding.root) {

    internal var userInfoContainer: RelativeLayout = rubricaItemBinding.userInfoContainer
    internal var userRound: ImageView = rubricaItemBinding.userRound
    internal var addUserContainer: RelativeLayout = rubricaItemBinding.addUserContainer
    internal var nome: TextView = rubricaItemBinding.userName
    internal var email: TextView = rubricaItemBinding.userEmail
}