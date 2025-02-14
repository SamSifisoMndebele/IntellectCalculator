package com.avidco.intellectcalculator.ui.expressions.treediagram.graphview

import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.models.Graph
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.models.Node

abstract class AbstractGraphAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    var graph: Graph? = null
    override fun getItemCount(): Int = graph?.nodeCount ?: 0

    open fun getNode(position: Int): Node? = graph?.getNodeAtPosition(position)
    open fun getNodeOperand(position: Int): String? = graph?.getNodeAtPosition(position)?.operand
    open fun getNodePostfix(position: Int): String? = graph?.getNodeAtPosition(position)?.postfix
    open fun getNodePrefix(position: Int): String? = graph?.getNodeAtPosition(position)?.prefix
    /**
     * Submits a new graph to be displayed.
     *
     *
     * If a graph is already being displayed, you need to dispatch Adapter.notifyItem.
     *
     * @param graph The new graph to be displayed.
     */
    open fun submitGraph(@Nullable graph: Graph?) {
        this.graph = graph
    }
}