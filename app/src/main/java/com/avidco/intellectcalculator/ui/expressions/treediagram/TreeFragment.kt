package com.avidco.intellectcalculator.ui.expressions.treediagram

import android.content.Intent
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.databinding.FragmentTreeDiagramBinding
import com.avidco.intellectcalculator.ui.calculations.history.DatabaseHelper
import com.avidco.intellectcalculator.ui.expressions.HistoryListActivity
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.models.Edge
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.models.Graph
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.models.Node
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.layouts.StraightEdgeDecoration
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.layouts.TreeConfiguration
import com.avidco.intellectcalculator.ui.expressions.treediagram.graphview.layouts.TreeLayoutManager
import java.util.*

class TreeFragment : Fragment() {

    private var _binding: FragmentTreeDiagramBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : DatabaseHelper
    private lateinit var edgeDecoration : StraightEdgeDecoration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreeDiagramBinding.inflate(inflater, container, false)
        database = DatabaseHelper(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val configuration = TreeConfiguration.Builder()
            .setSiblingSeparation(100)
            .setLevelSeparation(100)
            .setSubtreeSeparation(100)
            .setOrientation(TreeConfiguration.ORIENTATION_TOP_BOTTOM)
            .build()

        binding.treeRecyclerView.layoutManager = TreeLayoutManager(requireContext(),configuration)
        edgeDecoration = StraightEdgeDecoration(Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 8f
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            pathEffect = CornerPathEffect(10f)
        })
    }

    private fun updateUI(){
        try {
            val string = database.last
            if (string != null){
                drawTree(string.postfix)
            }
        } catch (e: EmptyStackException) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.expressions_menu, menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_history -> {
                val intent = Intent(context, HistoryListActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun drawTree(postfix : String) {
        val nodesStack: Stack<Node> = Stack()
        val edgeStack: Stack<Edge> = Stack()
        val operand = StringBuilder()

        var i = 0
        for (char in postfix){
            if (char.isLetterOrDigit() || char == '.'){
                operand.append(char)
                continue
            }
            if (operand.isNotEmpty()){
                nodesStack.push(Node(i,operand.toString()) )
                operand.clear()
                i++
            }
            if (char.isWhitespace()) continue


            val rightNode = nodesStack.pop()
            val leftNode = nodesStack.pop()

            val rightPostfix = rightNode.operand + rightNode.postfix
            val leftPostfix  = leftNode.operand + leftNode.postfix

            val rightPrefix = rightNode.operand + rightNode.prefix
            val leftPrefix = leftNode.operand + leftNode.prefix

            val parentNode = Node(i,
                "${leftPostfix.trim()} ${rightPostfix.trim()} $char",
                "$char ${leftPrefix.trim()} ${rightPrefix.trim()}")

            edgeStack.push(Edge(parentNode, rightNode ))
            edgeStack.push(Edge(parentNode, Node(i++,char.toString()) ))
            edgeStack.push(Edge(parentNode,leftNode ))

            nodesStack.push(parentNode)
            i++
        }

        val graph = Graph()
        while (edgeStack.isNotEmpty()) {
            graph.addEdge(edgeStack.pop())
        }

        binding.treeRecyclerView.addItemDecoration(edgeDecoration)
        TreeAdapter().apply {
            this.submitGraph(graph)
            binding.treeRecyclerView.adapter = this
        }
    }
}
