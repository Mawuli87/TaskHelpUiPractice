package com.example.taskhelper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskhelper.data.entities.ContactEntities
import com.example.taskhelper.databinding.FriendsLayoutViewBinding


class FriendsAdapter(
    var context: Context,
    onItemClickListener: OnItemClickerListener
) :
    RecyclerView.Adapter<FriendsAdapter.FriendsViewModel>() {

    private var checkedPosition:Int = -1
    private var onItemClickListener: OnItemClickerListener? = null
    init {
        this.onItemClickListener = onItemClickListener
    }
    private val diffUtilCallback = object : DiffUtil.ItemCallback<ContactEntities>(){
        override fun areItemsTheSame(oldItem: ContactEntities, newItem: ContactEntities): Boolean {
            return oldItem.contact_Name == newItem.contact_Name
        }

        override fun areContentsTheSame(oldItem: ContactEntities, newItem: ContactEntities): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewModel {

        return FriendsViewModel(
            FriendsLayoutViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))

    }

    override fun onBindViewHolder(holder: FriendsViewModel, position: Int) {
        val friends = differ.currentList[position]
        holder.binding.friendName.text = friends.contact_Name
        Glide.with(context).asBitmap().load(friends.contact_Image).into(holder.binding.friendImage).clearOnDetach()
        holder.binding.root.setOnClickListener {
            checkedPosition = holder.adapterPosition
            onItemClickListener!!.onClicked(differ.currentList[position], position)
            onItemClickListener!!.onItemClickListener(position , holder.itemView)
        }
        if (checkedPosition == position){

            holder.binding.selectedFriend.visibility = View.VISIBLE
        }else{

            holder.binding.selectedFriend.visibility = View.GONE

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class FriendsViewModel(val binding: FriendsLayoutViewBinding) : RecyclerView.ViewHolder(binding.root)
    interface OnItemClickerListener{
        fun onClicked(contactEntities: ContactEntities, position: Int)
        fun onItemClickListener(position: Int, view: View?)
    }
}

