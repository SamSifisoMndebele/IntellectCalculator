package com.avidco.intellectcalculator.ui.expressions

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.databinding.FragmentExpressionsBinding
import com.avidco.intellectcalculator.ui.calculations.history.DatabaseHelper
import com.avidco.intellectcalculator.utils.Constants
import com.avidco.intellectcalculator.utils.ExpressionConvert.evaluatePostfix
import com.avidco.intellectcalculator.utils.ExpressionConvert.evaluatePrefix
import com.avidco.intellectcalculator.utils.ExpressionConvert.infixToPostfix
import com.avidco.intellectcalculator.utils.ExpressionConvert.infixToPrefix
import com.avidco.intellectcalculator.utils.ExpressionConvert.postfixToInfix
import com.avidco.intellectcalculator.utils.ExpressionConvert.postfixToPrefix
import com.avidco.intellectcalculator.utils.ExpressionConvert.prefixToInfix
import com.avidco.intellectcalculator.utils.ExpressionConvert.prefixToPostfix
import com.avidco.intellectcalculator.utils.LoadingDialog
import java.util.*

class ExpressionsFragment : Fragment() {

    private var _binding: FragmentExpressionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : DatabaseHelper
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpressionsBinding.inflate(inflater, container, false)
        database = DatabaseHelper(context)
        loadingDialog = LoadingDialog(activity)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun updateUI(){
        try {
            val string = database.last
            if (string != null){
                binding.prefixString.text = string.prefix
                binding.infixString.text = string.infix
                binding.postfixString.text = string.postfix

                binding.prefixInput.editText?.setText(string.prefix)
                binding.infixInput.editText?.setText(string.infix)
                binding.postfixInput.editText?.setText(string.postfix)

                if (binding.evaluateCheckBox.isChecked){
                    binding.evaluateAnswerView.text = string.postfix.evaluatePostfix() + string.prefix.evaluatePrefix()
                }
            }
        } catch (e: Exception) {
            binding.infixInput.error = e.message
            binding.prefixInput.error = e.message
            binding.postfixInput.error = e.message

        } catch (e: EmptyStackException) {
            binding.infixInput.error = "Syntax Error"
            binding.prefixInput.error = "Syntax Error"
            binding.postfixInput.error = "Syntax Error"
        } finally {
            loadingDialog.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
        updateUI()

        binding.infixButton.setOnClickListener {
            val infixString = binding.infixInput.editText?.text.toString().replaceOperators()

            if (infixString == "error"){
                binding.infixInput.error = "You can compare only two expressions."
            } else if (infixString.isNotEmpty()){
                loadingDialog.show()

                try {
                    val task = database.addData(infixString,
                        DatabaseHelper.Infix,
                        infixString.infixToPrefix(),
                        infixString,
                        infixString.infixToPostfix())

                    if (!task) {
                        DatabaseHelper(activity).updateString(infixString)
                    }

                    updateUI()
                } catch (e: EmptyStackException) {
                    binding.infixInput.error = "Syntax Error"
                } catch (e: Exception) {
                    binding.infixInput.error = e.message
                } finally {
                    loadingDialog.dismiss()
                }
            } else clear()
        }

        binding.prefixButton.setOnClickListener {
            val prefixString = binding.prefixInput.editText?.text.toString().replaceOperators()

            if (prefixString == "error"){
                binding.prefixInput.error = "You can compare only two expressions."
            } else if (prefixString.isNotEmpty()){
                loadingDialog.show()
                try {
                    val task = database.addData(prefixString,
                        DatabaseHelper.Prefix,
                        prefixString,
                        prefixString.prefixToInfix(),
                        prefixString.prefixToPostfix())

                    if (!task) {
                        DatabaseHelper(activity).updateString(prefixString)
                    }

                    updateUI()
                } catch (e: EmptyStackException) {
                    binding.prefixInput.error = "Syntax Error"
                } catch (e: Exception) {
                    binding.prefixInput.error = e.message
                } finally {
                    loadingDialog.dismiss()
                }
            } else clear()
        }

        binding.postfixButton.setOnClickListener {
            val postfixString = binding.postfixInput.editText?.text.toString().replaceOperators()

            if (postfixString == "error"){
                binding.infixInput.error = "You can compare only two expressions."
            } else if (postfixString.isNotEmpty()){
                loadingDialog.show()
                try {
                    val task = database.addData(postfixString,
                        DatabaseHelper.Postfix,
                        postfixString.postfixToPrefix(),
                        postfixString.postfixToInfix(),
                        postfixString)

                    if (!task) {
                        DatabaseHelper(activity).updateString(postfixString)
                    }

                    updateUI()
                } catch (e: EmptyStackException) {
                    binding.postfixInput.error = "Syntax Error"
                } catch (e: Exception) {
                    binding.postfixInput.error = e.message
                } finally {
                    loadingDialog.dismiss()
                }
            } else clear()
        }
    }

    private fun setMenu(){
        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            binding.infixInput.isErrorEnabled = false
            binding.prefixInput.isErrorEnabled = false
            binding.postfixInput.isErrorEnabled = false

            when (binding.root.findViewById<RadioButton>(id).text) {
                Constants.CONVERT_INFIX -> {
                    binding.infixLayout.visibility = View.VISIBLE
                    binding.prefixLayout.visibility = View.GONE
                    binding.postfixLayout.visibility = View.GONE
                    binding.evaluateWarning.visibility = View.INVISIBLE
                }
                Constants.CONVERT_PREFIX -> {
                    binding.infixLayout.visibility = View.GONE
                    binding.prefixLayout.visibility = View.VISIBLE
                    binding.postfixLayout.visibility = View.GONE
                    if (binding.evaluateCheckBox.isChecked){
                        binding.evaluateWarning.visibility = View.VISIBLE
                        binding.evaluateAnswerLayout.visibility = View.VISIBLE
                        binding.infixButton.text = getString(R.string.evaluate)
                        binding.prefixButton.text = getString(R.string.evaluate)
                        binding.postfixButton.text = getString(R.string.evaluate)
                    } else {
                        binding.evaluateWarning.visibility = View.INVISIBLE
                        binding.infixButton.text = getString(R.string.convert)
                        binding.prefixButton.text = getString(R.string.convert)
                        binding.postfixButton.text = getString(R.string.convert)
                        binding.evaluateAnswerLayout.visibility = View.GONE
                    }
                }
                Constants.CONVERT_POSTFIX -> {
                    binding.infixLayout.visibility = View.GONE
                    binding.prefixLayout.visibility = View.GONE
                    binding.postfixLayout.visibility = View.VISIBLE
                    if (binding.evaluateCheckBox.isChecked){
                        binding.evaluateWarning.visibility = View.VISIBLE
                        binding.evaluateAnswerLayout.visibility = View.VISIBLE
                        binding.infixButton.text = getString(R.string.evaluate)
                        binding.prefixButton.text = getString(R.string.evaluate)
                        binding.postfixButton.text = getString(R.string.evaluate)
                    } else {
                        binding.evaluateWarning.visibility = View.INVISIBLE
                        binding.infixButton.text = getString(R.string.convert)
                        binding.prefixButton.text = getString(R.string.convert)
                        binding.postfixButton.text = getString(R.string.convert)
                        binding.evaluateAnswerLayout.visibility = View.GONE
                    }
                }
            }
        }

        binding.evaluateCheckBox.setOnCheckedChangeListener { _, checked ->
            if (checked && !binding.root.findViewById<RadioButton>(R.id.infix_radio_button).isChecked ){
                binding.evaluateWarning.visibility = View.VISIBLE
            }
            if(checked){
                binding.evaluateAnswerLayout.visibility = View.VISIBLE
                binding.infixButton.text = getString(R.string.evaluate)
                binding.prefixButton.text = getString(R.string.evaluate)
                binding.postfixButton.text = getString(R.string.evaluate)
            } else {
                binding.evaluateWarning.visibility = View.INVISIBLE
                binding.infixButton.text = getString(R.string.convert)
                binding.prefixButton.text = getString(R.string.convert)
                binding.postfixButton.text = getString(R.string.convert)
                binding.evaluateAnswerLayout.visibility = View.GONE

                binding.infixInput.isErrorEnabled = false
                binding.prefixInput.isErrorEnabled = false
                binding.postfixInput.isErrorEnabled = false
            }
        }

        binding.infixInput.editText?.doOnTextChanged { _, _, _, _ ->
            binding.infixInput.isErrorEnabled = false
            binding.prefixInput.isErrorEnabled = false
            binding.postfixInput.isErrorEnabled = false
        }
        binding.prefixInput.editText?.doOnTextChanged { _, _, _, _ ->
            binding.infixInput.isErrorEnabled = false
            binding.prefixInput.isErrorEnabled = false
            binding.postfixInput.isErrorEnabled = false
        }
        binding.postfixInput.editText?.doOnTextChanged { _, _, _, _ ->
            binding.infixInput.isErrorEnabled = false
            binding.prefixInput.isErrorEnabled = false
            binding.postfixInput.isErrorEnabled = false
        }
    }

    private fun clear() {
        binding.postfixInput.editText?.setText("")
        binding.prefixInput.editText?.setText("")
        binding.infixInput.editText?.setText("")
        binding.evaluateAnswerView.text = ""
        binding.postfixString.text = ""
        binding.infixString.text = ""
        binding.prefixString.text = ""
    }

    private fun String.replaceOperators() : String{
        var string = this
        while (string.contains("==")){
            string = string.replace("==", "=")
        }
        while (string.contains("<=")){
            string = string.replace("<=", "≤")
        }
        while (string.contains(">=")){
            string = string.replace(">=", "≥")
        }
        while (string.contains("!=")){
            string = string.replace("!=", "≠")
        }

        if (string.count { it == '=' || it == '≠' || it == '≤' || it == '<' || it == '≥' || it == '>' } > 1){
            return "error"
        }
        return string
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.expressions_menu, menu)

        val clearItem = menu.findItem(R.id.item_clear)
        clearItem.isVisible = binding.infixString.text.toString().isNotEmpty()
        binding.infixInput.editText?.doOnTextChanged { text, _, _, _ ->
            clearItem.isVisible = text.toString().isNotEmpty()
        }
        binding.prefixInput.editText?.doOnTextChanged { text, _, _, _ ->
            clearItem.isVisible = text.toString().isNotEmpty()
        }
        binding.postfixInput.editText?.doOnTextChanged { text, _, _, _ ->
            clearItem.isVisible = text.toString().isNotEmpty()
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_history -> {
                val intent = Intent(context, HistoryListActivity::class.java)
                startActivity(intent)
            }
            R.id.item_clear -> {
                clear()
                //menuItem.isVisible = binding.infixString.text.toString().isNotEmpty()
            }

        }
        return super.onOptionsItemSelected(menuItem)
    }
}
