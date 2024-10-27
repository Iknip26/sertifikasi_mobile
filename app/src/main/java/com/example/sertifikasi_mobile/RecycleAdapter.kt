package com.example.sertifikasi_mobile
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sertifikasi_mobile.SQLite.DataPemilih
import com.example.sertifikasi_mobile.SQLite.DataPemilihDBHelper
interface OnDataChangedListener {
    fun onDataChanged(newCount: Int)
}

class RecycleAdapter(private val itemList: MutableList<DataPemilih>, private val listener: OnDataChangedListener
) : RecyclerView.Adapter<RecycleAdapter.ItemViewHolder>() {
    private lateinit var db : DataPemilihDBHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        if(position%2==0){
            holder.mainContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.grey2))
        }else{
            holder.mainContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.grey3))
        }
        holder.numberTextView.text = (position+1).toString()
        holder.nameTextView.text = item.name
        holder.dateTextView.text = "Diupload pada : ${item.date}"

        // Handle button click to go to DetailActivity
        holder.detailBtn.setOnClickListener {
            val context = holder.itemView.context
            val intentToDetail = Intent(context, DetailActivity::class.java)
            intentToDetail.putExtra("data_id", item.id)
            context.startActivity(intentToDetail)
        }

        db = DataPemilihDBHelper(holder.itemView.context)

        holder.deleteBtn.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).apply {
                setTitle("Hapus Data")
                setMessage("Apakah Anda yakin ingin menghapus data ini?")
                setPositiveButton("Ya") { _, _ ->
                        db.deleteData(item.id)
                        itemList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemList.size)
                }
                setNegativeButton("Tidak", null)
            }.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainContainer: LinearLayout = itemView.findViewById(R.id.main_container)
        val numberTextView: TextView = itemView.findViewById(R.id.number_txt)
        val nameTextView: TextView = itemView.findViewById(R.id.name_Rlayout_txt)
        val dateTextView: TextView = itemView.findViewById(R.id.date_RLayout_txt)
        val detailBtn: ImageButton = itemView.findViewById(R.id.detail_btn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.delete_btn)
    }
}
