package com.barracudas.savenotes

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.scottyab.aescrypt.AESCrypt
import java.lang.Exception

open class MyAdepter (private val userList : ArrayList<User>) : Adapter<MyAdepter.MyViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_for_main_activity, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]

        var decryptedTitle = "Data Stolen"
        var decryptedNotes = "Data Stolen, Please Contact to App Developer Immediately"

        try {
            decryptedTitle= AESCrypt.decrypt(currentItem.UserID, currentItem.title)
            decryptedNotes = AESCrypt.decrypt(currentItem.UserID, currentItem.Notes)
        } catch (e : Exception) {
            holder.title.setTextColor(Color.RED)
            holder.notes.setTextColor(Color.RED)
        }

        holder.title.text = decryptedTitle
        holder.notes.text = decryptedNotes

        holder.itemView.setOnClickListener {

            var intent = Intent(holder.itemView.context, NewNotesPage::class.java)
            intent.putExtra("title", decryptedTitle)
            intent.putExtra("notes", decryptedNotes)
            intent.putExtra("UserID", currentItem.UserID)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder (itemView) {

        val title : TextView = itemView.findViewById(R.id.recycleViewTitle)
        val notes : TextView = itemView.findViewById(R.id.recycleViewNotes)
    }
}