package com.dicoding.asclepius.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.entity.SavemarkEntity
import com.dicoding.asclepius.databinding.ItemsBinding
import com.dicoding.asclepius.view.ResultActivity

class SavemarkAdapter : ListAdapter<SavemarkEntity, SavemarkAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val savemark = getItem(position)
        holder.bind(savemark)

    }

    class MyViewHolder(val binding: ItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(savemark: SavemarkEntity) {
            val labelScore = "${savemark.label} : ${savemark.confidenceScore}"
            binding.tvItemName.text = labelScore
            binding.tvItemDesc.text = savemark.descResult
            val imageUri = Uri.parse(savemark.imageResult)
            binding.ivPhoto.setImageURI(imageUri)

            itemView.setOnClickListener { view ->
                val context = itemView.context
                val intent = Intent(context, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, savemark.imageResult)
                intent.putExtra(ResultActivity.EXTRA_RESULT_LABEL, savemark.label)
                intent.putExtra(ResultActivity.EXTRA_RESULT_SCORE, savemark.confidenceScore)
                intent.putExtra(ResultActivity.EXTRA_RESULT_DESC, savemark.descResult)
                context.startActivity(intent)
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SavemarkEntity>() {
            override fun areItemsTheSame(
                oldItem: SavemarkEntity,
                newItem: SavemarkEntity
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SavemarkEntity,
                newItem: SavemarkEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}