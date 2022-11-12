package com.hogl.eregister.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hogl.eregister.R
import com.hogl.eregister.data.entities.Group
import com.hogl.eregister.databinding.GroupRowLayoutBinding

class GroupsAdapter(private val onClick: (Group) -> Unit) :
    ListAdapter<Group, GroupsAdapter.GroupViewHolder>(GroupDiffCallback) {


    private var oldData = emptyList<Group>()

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class GroupViewHolder(val binding: GroupRowLayoutBinding, val onClick: (Group) -> Unit) :
        RecyclerView.ViewHolder(binding.root)

    {


        private val group_id :TextView = binding.groupId
        private val group_firstName: TextView = binding.groupName
        private val group_lastName: TextView = binding.groupLeader
        private var currentGroup: Group = Group(3,"robert","kagame",15, System.currentTimeMillis())

        init {
            itemView.setOnClickListener {
                currentGroup.let {
                    if (it != null) {
                        onClick(it)
                    }
                }
            }
        }


        fun bind(group: Group, position: Int) {
            currentGroup = group

            group_id.text = position.toString()
            group_firstName.text = currentGroup.grp_name
            group_lastName.text = currentGroup.grp_leader

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_row_layout, parent, false)
        return GroupViewHolder(
            GroupRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onClick
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group, position+1)

    }

    fun setData(newData: List<Group>) {
        oldData = newData
        notifyDataSetChanged()
    }
}

object GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.grp_id == newItem.grp_id
    }
}