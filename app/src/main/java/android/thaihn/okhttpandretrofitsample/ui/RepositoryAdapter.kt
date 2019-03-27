package android.thaihn.okhttpandretrofitsample.ui

import android.support.v7.widget.RecyclerView
import android.thaihn.okhttpandretrofitsample.databinding.ItemRepositoryBinding
import android.thaihn.okhttpandretrofitsample.entity.Repository
import android.view.LayoutInflater
import android.view.ViewGroup

class RepositoryAdapter(
        private val items: ArrayList<Repository>
) : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    class ViewHolder(
            private val binding: ItemRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Repository) {
            binding.repository = item

            binding.root.setOnClickListener {

            }
            binding.executePendingBindings()
        }
    }

    fun updateAllData(newList: List<Repository>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}