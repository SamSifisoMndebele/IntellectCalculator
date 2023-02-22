package com.avidco.intellectcalculator.ui.calculations

import com.avidco.intellectcalculator.utils.BaseConvert.fromDecimal
import com.avidco.intellectcalculator.utils.BaseConvert.fromRadix
import com.avidco.intellectcalculator.utils.BaseConvert.fromChar
import android.os.Bundle
import com.avidco.intellectcalculator.R
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.avidco.intellectcalculator.databinding.FragmentConvertBinding

class ConvertFragment : Fragment() {

    private lateinit var binding: FragmentConvertBinding
    private lateinit var prefs : SharedPreferences

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        binding = FragmentConvertBinding.inflate(layoutInflater, viewGroup, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.calc_menu, menu)

        binding.decimalEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.decimalEditText.text.toString().isNotEmpty()
        }
        binding.binaryEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.binaryEditText.text.toString().isNotEmpty()
        }
        binding.octalEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.octalEditText.text.toString().isNotEmpty()
        }
        binding.hexEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.hexEditText.text.toString().isNotEmpty()
        }
        binding.asciiEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.asciiEditText.text.toString().isNotEmpty()
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_clear -> {
                clear()
            }
            R.id.item_copy -> {
                copy()
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        prefs = context!!.getSharedPreferences("Bases", Context.MODE_PRIVATE)

        val decimal = prefs.getString("decimal_base_convert", null)
        if (decimal != null){
            val fromDecimal = fromDecimal(decimal)
            binding.decimalInput.error = fromDecimal.errorMessage
            binding.decimalInput.isErrorEnabled = fromDecimal.error

            binding.decimalEditText.setText(decimal)
            binding.binaryEditText.setText(fromDecimal.binary)
            binding.octalEditText.setText(fromDecimal.octal)
            binding.hexEditText.setText(fromDecimal.hex)
            binding.asciiEditText.setText(fromDecimal.ascii)
        }
        setupOnTextChanged()
    }

    private fun setupOnTextChanged() {
         binding.decimalEditText.doOnTextChanged { text,_,_,_ ->
             if (binding.decimalEditText.hasFocus()){
                 val fromDecimal = fromDecimal(text.toString())
                 
                 binding.decimalInput.error = fromDecimal.errorMessage
                 binding.decimalInput.isErrorEnabled = fromDecimal.error

                 prefs.edit().putString("decimal_base_convert", text.toString()).apply()
                 binding.binaryEditText.setText(fromDecimal.binary)
                 binding.octalEditText.setText(fromDecimal.octal)
                 binding.hexEditText.setText(fromDecimal.hex)
                 binding.asciiEditText.setText(fromDecimal.ascii)
             }
         }
        
         binding.binaryEditText.doOnTextChanged { text,_,_,_ ->
             if (binding.binaryEditText.hasFocus()){
                 val fromRadix = fromRadix(text.toString(), 2)

                 binding.binaryInput.error = fromRadix.errorMessage
                 binding.binaryInput.isErrorEnabled = fromRadix.error

                 prefs.edit().putString("decimal_base_convert", fromRadix.decimal).apply()
                 binding.decimalEditText.setText(fromRadix.decimal)
                 binding.octalEditText.setText(fromRadix.octal)
                 binding.hexEditText.setText(fromRadix.hex)
                 binding.asciiEditText.setText(fromRadix.ascii)
             }
         }
        
        binding.octalEditText.doOnTextChanged { text, _, _, _ ->
            if (binding.octalEditText.hasFocus()){
                val fromRadix = fromRadix(text.toString(), 8)

                prefs.edit().putString("decimal_base_convert", fromRadix.decimal).apply()
                binding.octalInput.error = fromRadix.errorMessage
                binding.octalInput.isErrorEnabled = fromRadix.error
                binding.decimalEditText.setText(fromRadix.decimal)
                binding.binaryEditText.setText(fromRadix.binary)
                binding.hexEditText.setText(fromRadix.hex)
                binding.asciiEditText.setText(fromRadix.ascii)
            }
        }
        
        binding.hexEditText.doOnTextChanged { text,_,_,_ ->
            if (binding.hexEditText.hasFocus()){
                val fromRadix = fromRadix(text.toString(), 16)
                
                binding.hexInput.error = fromRadix.errorMessage
                binding.hexInput.isErrorEnabled = fromRadix.error

                prefs.edit().putString("decimal_base_convert", fromRadix.decimal).apply()
                binding.decimalEditText.setText(fromRadix.decimal)
                binding.binaryEditText.setText(fromRadix.binary)
                binding.octalEditText.setText(fromRadix.octal)
                binding.asciiEditText.setText(fromRadix.ascii)
            }
        } 
        
        binding.asciiEditText.doOnTextChanged { text,_,_,_ ->
            if (binding.asciiEditText.hasFocus()){
                val fromChar = fromChar(text.toString())

                prefs.edit().putString("decimal_base_convert", fromChar.decimal).apply()
                binding.decimalEditText.setText(fromChar.decimal)
                binding.binaryEditText.setText(fromChar.binary)
                binding.octalEditText.setText(fromChar.octal)
                binding.hexEditText.setText(fromChar.hex)
            }
        }
    }

    private fun copy() {
        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clipData: ClipData? = when {
            binding.decimalEditText.hasFocus() -> {
                ClipData.newPlainText("decimal",  binding.binaryEditText.text.toString().trim())
            }
            binding.binaryEditText.hasFocus() -> {
                ClipData.newPlainText("binary",  binding.binaryEditText.text.toString().trim())
            }
            binding.octalEditText.hasFocus() -> {
                ClipData.newPlainText("octal", binding.octalEditText.text.toString().trim())
            }
            binding.hexEditText.hasFocus() -> {
                ClipData.newPlainText("hexadecimal", binding.hexEditText.text.toString().trim())
            }
            binding.asciiEditText.hasFocus() -> {
                ClipData.newPlainText("ascii", binding.asciiEditText.text.toString().trim())
            }
            else -> null
        }
        if (clipData != null) {
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(activity, getString(R.string.copied), Toast.LENGTH_LONG).show()
        }
    }
    private fun clear() {
        prefs.edit().putString("decimal_base_convert", null).apply()
        binding.decimalEditText.setText("")
        binding.binaryEditText.setText("")
        binding.octalEditText.setText("")
        binding.hexEditText.setText("")
        binding.asciiEditText.setText("")
    }
}