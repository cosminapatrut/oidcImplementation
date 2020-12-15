package com.orange.volunteers.userdomains

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.orange.domain.model.DomainModel
import com.orange.domain.model.DomainRecyclerItem
import com.orange.volunteers.R
import kotlinx.android.synthetic.main.domains_recycler_view_item.view.*

class DomainsAdapter (
private val context: Context,
private val items: List<DomainModel>
) : RecyclerView.Adapter<DomainsViewHolder>() {
    var onDomainSelectedCallback: ((DomainModel) -> Unit)? = null
    var onDomainDeselectedCallback: ((DomainModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DomainsViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val domainsItemView = layoutInflater.inflate(
            R.layout.domains_recycler_view_item,
            parent,
            false
        )
        return DomainsViewHolder(
            domainsItemView
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DomainsViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.domains_text_tv.text = item.name
        holder.itemView.domains_item_checkbox.isChecked = item.isInterested
        holder.itemView.domains_item_checkbox.setOnClickListener {
            item.isInterested = holder.itemView.domains_item_checkbox.isChecked
            if(item.isInterested) {
                onDomainSelectedCallback?.invoke(item)
                notifyDataSetChanged()
            }
            else {
                onDomainDeselectedCallback?.invoke(item)
                notifyDataSetChanged()
            }
            if(holder.itemView.domains_item_checkbox.isChecked)
                    holder.itemView.domains_text_tv.setTextColor(ContextCompat.getColor(context, R.color.black))
            else
                holder.itemView.domains_text_tv.setTextColor(ContextCompat.getColor(context, R.color.inactive_dark_grey_text))

        }
    }
}

class DomainsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)