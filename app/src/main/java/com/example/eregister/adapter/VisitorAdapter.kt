package com.example.eregister.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eregister.R
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.databinding.VisitorRowLayoutBinding

class VisitorAdapter(private val onClick: (Visitor) -> Unit) :
    ListAdapter<Visitor, VisitorAdapter.VisitorViewHolder>(VisitorDiffCallback) {


    private var oldData = emptyList<Visitor>()

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class VisitorViewHolder(val binding: VisitorRowLayoutBinding, val onClick: (Visitor) -> Unit) :
        RecyclerView.ViewHolder(binding.root)

    {


        private val visitor_firstName: TextView = binding.firstNameTextView
        private val visitor_lastName: TextView = binding.lastNameTextView
        private var currentVisitor: Visitor = Visitor(3,"robert","kagame",15,"og",54)

        init {
            itemView.setOnClickListener {
                currentVisitor.let {
                    if (it != null) {
                        onClick(it)
                    }
                }
            }
        }


        fun bind(visitor: Visitor) {
            currentVisitor = visitor

            visitor_firstName.text = currentVisitor!!.vis_first_name
            visitor_lastName.text = currentVisitor!!.vis_last_name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.visitor_row_layout, parent, false)
        return VisitorViewHolder(
            VisitorRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onClick
        )
    }

    override fun onBindViewHolder(holder: VisitorViewHolder, position: Int) {
        val visitor = getItem(position)
        holder.bind(visitor)

    }

    fun setData(newData: List<Visitor>) {
        oldData = newData
        notifyDataSetChanged()
    }
}

object VisitorDiffCallback : DiffUtil.ItemCallback<Visitor>() {
    override fun areItemsTheSame(oldItem: Visitor, newItem: Visitor): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Visitor, newItem: Visitor): Boolean {
        return oldItem.vis_id == newItem.vis_id
    }
}