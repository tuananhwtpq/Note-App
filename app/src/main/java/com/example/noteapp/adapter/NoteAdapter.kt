package com.example.noteapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.databinding.NoteItemBinding
import com.example.noteapp.databinding.NoteItemListBinding
import com.example.noteapp.fragments.HomeFragmentDirections
import com.example.noteapp.model.Note
import kotlinx.coroutines.GlobalScope

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    companion object {
        private const val GRID_TYPE = 1
        private const val LIST_TYPE = 2
    }

    var isListView: Boolean = false

    inner class NoteViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == LIST_TYPE) {
            NoteItemListBinding.inflate(layoutInflater, parent, false)
        } else {
            NoteItemBinding.inflate(layoutInflater, parent, false)
        }

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {
        val currentNote = differ.currentList[position]

        when (holder.itemViewType) {
            GRID_TYPE -> {
                val binding = holder.binding as NoteItemBinding
                binding.tvTitle.text = currentNote.title
                binding.tvContent.text = currentNote.content
                binding.tvTime.text = currentNote.time
            }

            LIST_TYPE -> {
                val binding = holder.binding as NoteItemListBinding
                binding.tvTitle.text = currentNote.title
                binding.tvContent.text = currentNote.content
                binding.tvTime.text = currentNote.time
            }
        }

//        holder.bind(currentNote)

        holder.itemView.setOnClickListener {
            val directions =
                HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
            it.findNavController().navigate(directions)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (isListView) {
            LIST_TYPE
        } else {
            GRID_TYPE
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)
}