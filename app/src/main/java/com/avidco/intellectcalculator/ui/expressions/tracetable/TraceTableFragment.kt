package com.avidco.intellectcalculator.ui.expressions.tracetable

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.databinding.FragmentTraceTableBinding
import com.avidco.intellectcalculator.ui.calculations.history.DatabaseHelper
import com.avidco.intellectcalculator.ui.expressions.HistoryListActivity
import com.avidco.intellectcalculator.utils.ExpressionConvert.precedence
import java.util.*
import kotlin.collections.ArrayList

class TraceTableFragment : Fragment() {

    private var _binding: FragmentTraceTableBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : DatabaseHelper

    private var postfixTableDataList = ArrayList<TableRowData>()
    private var prefixTableDataList = ArrayList<TableRowData>()
    private lateinit var postfixTableAdapter: TableRowAdapter
    private lateinit var prefixTableAdapter: TableRowAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTraceTableBinding.inflate(inflater, container, false)
        database = DatabaseHelper(context)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        postfixTableAdapter = TableRowAdapter(postfixTableDataList)
        binding.postfixTableRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postfixTableRecyclerView.adapter = postfixTableAdapter

        prefixTableAdapter = TableRowAdapter(prefixTableDataList)
        binding.prefixTableRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.prefixTableRecyclerView.adapter = prefixTableAdapter
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

    private fun updateUI(){
        try {
            val string = database.last
            if (string != null){
                drawPostfixTable(string.infix)
                drawPrefixTable(string.infix)

                postfixTableAdapter.notifyDataSetChanged()
                prefixTableAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: EmptyStackException) {

        }
    }

    private fun drawPostfixTable(infix: String){
        postfixTableDataList.clear()
        val postfix: Queue<String> = LinkedList()
        val stack: Stack<Char> = Stack()

        val num = StringBuilder()
        var prevChar = ' '

        for (char in infix){
            if (char.isWhitespace()) continue

            if (char.isLetterOrDigit() || char == '.') {
                if (prevChar == ')') {
                    stack.push('×')
                    val stackWithChar = stack.joinToString("")
                    stack.pop()
                    while (stack.isNotEmpty() && '×'.precedence() <= stack.peek().precedence()) {
                        val element = stack.pop()
                        postfix.add(element.toString())
                    }
                    stack.push('×')
                    postfixTableDataList.add(TableRowData('×', stackWithChar ,  postfix.joinToString(" ")))
                }
                num.append(char)
                postfixTableDataList.add(TableRowData(char, stack.joinToString("") ,  postfix.joinToString(" ") + " $num"))
                prevChar = char
                continue
            }

            if (num.isNotEmpty()){
                postfix.add("$num")
                num.clear()
            }

            if (char == '(' && ((prevChar.isLetterOrDigit() || prevChar == '.') || prevChar == ')')) {
                stack.push('×')
                val stackWithChar = stack.joinToString("")
                stack.pop()
                while (stack.isNotEmpty() && '×'.precedence() <= stack.peek().precedence()) {
                    val element = stack.pop()
                    postfix.add(element.toString())
                }
                stack.push('×')
                postfixTableDataList.add(TableRowData('×', stackWithChar ,  postfix.joinToString(" ")))
            }

            if (stack.isEmpty() || char == '(') {
                stack.push(char)
                postfixTableDataList.add(TableRowData(char, stack.joinToString("") ,  postfix.joinToString(" ")))
                prevChar = char
                continue
            }

            if (char == ')') {
                stack.push(char)
                val stackWithBr = stack.joinToString("")
                stack.pop()
                // Need to remove stack element until the close bracket
                while (stack.isNotEmpty() && stack.peek() != '(')
                {
                    // Get top element
                    val element = stack.pop()
                    postfix.add(element.toString())
                }
                if (stack.isNotEmpty() && stack.peek() == '(')
                {
                    // Remove stack element
                    stack.pop()
                }
                postfixTableDataList.add(TableRowData(char, stackWithBr ,  postfix.joinToString(" ")))

                prevChar = char
                continue
            }
            prevChar = char

            // Remove stack element until precedence of
            // top is greater than current infix operator
            stack.push(char)
            val stackWithChar = stack.joinToString("")
            stack.pop()
            while (stack.isNotEmpty() && char.precedence() <= stack.peek().precedence()) {
                // Get top element
                val element = stack.pop()
                postfix.add(element.toString())
            }
            // Add new operator
            stack.push(char)
            postfixTableDataList.add(TableRowData(char, stackWithChar,  postfix.joinToString(" ")))
        }

        if (num.isNotEmpty()){
            postfix.add("$num")
            num.clear()
        }

        while (stack.isNotEmpty()) {
            val e = stack.pop()
            if (e == '(' || e == ')') continue
            postfix.add("$e")
        }
        postfixTableDataList.add(TableRowData(' ' , "", postfix.joinToString(" ")))
    }

    private fun drawPrefixTable(infix: String){
        prefixTableDataList.clear()
        val infixRev = infix.reversed()
        val prefix: Queue<String> = LinkedList()
        val stack: Stack<Char> = Stack()

        val num = StringBuilder()
        var prevChar = ' '

        for (char in infixRev){
            if (char.isWhitespace()) continue

            if (char.isLetterOrDigit() || char == '.') {
                if (prevChar == '(') {
                    stack.push('×')
                    val stackWithChar = stack.joinToString("")
                    stack.pop()
                    while (stack.isNotEmpty() && '×'.precedence() < stack.peek().precedence()) {
                        val element = stack.pop()
                        prefix.add(element.toString())
                    }
                    stack.push('×')
                    prefixTableDataList.add(TableRowData('×', stackWithChar ,
                        prefix.joinToString(" ").reversed()))
                }
                num.append(char)
                prefixTableDataList.add(TableRowData(char, stack.joinToString("") ,
                    "$num " +prefix.joinToString(" ").reversed()))
                prevChar = char
                continue
            }

            if (num.isNotEmpty()){
                prefix.add("$num")
                num.clear()
            }

            if (char == ')' && ((prevChar.isLetterOrDigit() || prevChar == '.') || prevChar == '(')) {
                stack.push('×')
                val stackWithChar = stack.joinToString("")
                stack.pop()
                while (stack.isNotEmpty() && '×'.precedence() < stack.peek().precedence()) {
                    val element = stack.pop()
                    prefix.add(element.toString())
                }
                stack.push('×')
                prefixTableDataList.add(TableRowData('×', stackWithChar ,  prefix.joinToString(" ")))
            }

            if ( stack.isEmpty() || char == ')') {
                stack.push(char)
                prefixTableDataList.add(TableRowData(char, stack.joinToString("") ,
                    prefix.joinToString(" ").reversed()))
                prevChar = char
                continue
            }

            if (char == '(') {
                stack.push(char)
                val stackWithBr = stack.joinToString("")
                stack.pop()
                // Need to remove stack element until the close bracket
                while (stack.isNotEmpty() && stack.peek() != ')')
                {
                    // Get top element
                    val element = stack.pop()
                    prefix.add(element.toString())
                }
                if (stack.peek() == ')')
                {
                    // Remove stack element
                    stack.pop()
                }
                prefixTableDataList.add(
                    TableRowData(char, stackWithBr ,
                    prefix.joinToString(" ").reversed())
                )
                prevChar = char
                continue
            }
            prevChar = char

            // Remove stack element until precedence of
            // top is greater than current infix operator
            stack.push(char)
            val stackWithBr = stack.joinToString("")
            stack.pop()
            while (stack.isNotEmpty() && char.precedence() < stack.peek().precedence()) {
                // Get top element
                val element = stack.pop()
                prefix.add(element.toString())
            }
            // Add new operator
            stack.push(char)
            prefixTableDataList.add(
                TableRowData(char, stackWithBr ,
                prefix.joinToString(" ").reversed())
            )
        }

        if (num.isNotEmpty()){
            prefix.add("$num")
            num.clear()
        }

        while (stack.isNotEmpty()) {
            val e = stack.pop()
            if (e == '(' || e == ')') continue
            prefix.add("$e")
        }
        prefixTableDataList.add(
            TableRowData(' ', stack.joinToString(" ") ,
            prefix.joinToString(" ").reversed())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}