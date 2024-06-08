package com.dicoding.deteksicabai.adapter

import Chi.R
import Chi.databinding.HistoryItemBinding
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.deteksicabai.entity.history
import java.io.File

class HistoryAdapter(private val activity: Activity) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var listHistory = ArrayList<history>()
        set(listNotes) {
            field.clear()
            field.addAll(listNotes)
            notifyDataSetChanged()  // Notify adapter of data change
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int = listHistory.size

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = HistoryItemBinding.bind(itemView)

        fun bind(historyDisease: history) {
            binding.tvDiseaseName.text = historyDisease.disease
            binding.tvItemDate.text = historyDisease.date
            binding.tvDiseaseDescription.text = historyDisease.descDisease
            binding.diseasePhoto.setImageURI(historyDisease.photoLeaf.toUri())
        }
    }


}
