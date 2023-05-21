package com.example.taskhelper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskhelper.databinding.OnboardingItemContainerBinding
import com.example.taskhelper.ui.onBoarding.OnBoardingItem


class OnboardItemAdapter(private var context:Context, private val listOnBoardingItem: ArrayList<OnBoardingItem>) :
    RecyclerView.Adapter<OnboardItemAdapter.OnboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardViewHolder {

        return OnboardViewHolder( OnboardingItemContainerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: OnboardViewHolder, position: Int) {
         val onboardItem = listOnBoardingItem[position]
        holder.binding.viewPagerImage.setImageResource(onboardItem.onBoardingImage)
        holder.binding.viewPagerText.text = onboardItem.titleText
        holder.binding.viewPagerDescText.text = onboardItem.descriptionText

    }

    inner class OnboardViewHolder(val binding: OnboardingItemContainerBinding) : RecyclerView.ViewHolder(binding.root)
    override fun getItemCount(): Int {
        return listOnBoardingItem.size
    }
}