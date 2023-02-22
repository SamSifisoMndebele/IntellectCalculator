package com.avidco.intellectcalculator.ui.expressions.tracetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avidco.intellectcalculator.R
import java.util.*

class TableRowAdapter(private var scanArrayList: ArrayList<TableRowData>) :
    RecyclerView.Adapter<TableRowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.trace_table_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.characterScanned.text = scanArrayList[i].operand.toString()
        viewHolder.stack.text = scanArrayList[i].stack
        viewHolder.postfixExpression.text = scanArrayList[i].postfixString
    }

    override fun getItemCount(): Int {
        return scanArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val characterScanned: TextView = itemView.findViewById(R.id.character_scanned)
        val stack: TextView = itemView.findViewById(R.id.stack)
        val postfixExpression: TextView = itemView.findViewById(R.id.postfix_expression)


    }
}

