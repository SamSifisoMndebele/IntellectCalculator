package com.avidco.intellectcalculator.ui.calculations

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
import com.avidco.intellectcalculator.databinding.FragmentExcessBinding
import com.avidco.intellectcalculator.utils.BinaryArithmetic.toDecimal
import com.avidco.intellectcalculator.utils.ExcessConvert
import com.avidco.intellectcalculator.utils.ExcessConvert.bitsToIdentifier
import com.avidco.intellectcalculator.utils.ExcessConvert.identifierToBits

class ExcessFragment : Fragment() {

    private lateinit var binding: FragmentExcessBinding
    private lateinit var prefs : SharedPreferences

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        binding = FragmentExcessBinding.inflate(layoutInflater, viewGroup, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.calc_menu, menu)

        binding.decimalSignedEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.decimalSignedEditText.text.toString().isNotEmpty()
        }
        binding.excessBitsEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.excessBitsEditText.text.toString().isNotEmpty()
        }
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        prefs = context!!.getSharedPreferences("Bases", Context.MODE_PRIVATE)

        val decimal = prefs.getString("decimal_excess", null)
        val bits = prefs.getString("bits_excess", null)

        if (decimal != null && bits != null){
            binding.excessBitsEditText.setText(bits)
            val excessIdentifier = bits.bitsToIdentifier()
            binding.excessIdentifierEditText.setText(excessIdentifier)

            val fromDecimal = ExcessConvert.fromDecimal(decimal, bits)
            binding.decimalSignedInput.error = fromDecimal.errorMessage
            binding.decimalSignedInput.isErrorEnabled = fromDecimal.error

            binding.decimalSignedEditText.setText(decimal)
            binding.excessEditText.setText(fromDecimal.excess)
            binding.excessIdentifierEditText.setText(fromDecimal.excessIdentifier)
            binding.excessBitsEditText.setText(fromDecimal.excessBits.toString())
        }

        setupWatchers()
    }

    private fun setupWatchers() {
        binding.decimalSignedEditText.doOnTextChanged { text, _, _, _ ->
            if (binding.decimalSignedEditText.hasFocus()) {
                var excessBits = binding.excessBitsEditText.text.toString()
                if (excessBits.isEmpty()){
                    excessBits = "8"
                }

                prefs.edit().putString("decimal_excess", text.toString()).apply()
                val fromDecimal = ExcessConvert.fromDecimal(text.toString(), excessBits)

                binding.decimalSignedInput.error = fromDecimal.errorMessage
                binding.decimalSignedInput.isErrorEnabled = fromDecimal.error

                binding.excessEditText.setText(fromDecimal.excess)
                binding.excessIdentifierEditText.setText(fromDecimal.excessIdentifier)
                binding.excessBitsEditText.setText(fromDecimal.excessBits.toString())
            }
        }

        binding.excessEditText.doOnTextChanged { text, _, _, _ ->
            if (binding.excessEditText.hasFocus()) {
                var excessBits = binding.excessBitsEditText.text.toString()
                if (excessBits.isEmpty()){
                    excessBits = "8"
                }

                val fromExcess = ExcessConvert.fromExcess(text.toString(), excessBits)

                binding.excessInput.error = fromExcess.errorMessage
                binding.excessInput.isErrorEnabled = fromExcess.error

                prefs.edit().putString("decimal_excess", fromExcess.decimal).apply()
                binding.decimalSignedEditText.setText(fromExcess.decimal)
                binding.excessIdentifierEditText.setText(fromExcess.excessIdentifier)
                binding.excessBitsEditText.setText(fromExcess.excessBits.toString())
            }
        }


        binding.excessIdentifierEditText.doOnTextChanged { text, _, _, _ ->
            binding.excessIdentifierInput.isErrorEnabled = false
            if (binding.excessIdentifierEditText.hasFocus()) {

                try {
                    if (text.toString().isNotEmpty()){
                        text.toString().toLong()
                    }
                    binding.excessIdentifierInput.isErrorEnabled = false
                } catch (e:NumberFormatException){
                    binding.excessIdentifierInput.error = "Size Error"
                }

                val excessBits = text.toString().identifierToBits()
                prefs.edit().putString("bits_excess", excessBits).apply()
                binding.excessBitsEditText.setText(excessBits)
            }
        }

        binding.excessBitsEditText.doOnTextChanged { text, _, _, _ ->
            binding.excessBitsInput.isErrorEnabled = false
            if (binding.excessBitsEditText.hasFocus()) {

                if (text.toString().isNotEmpty() && text.toString().toInt() >= 64){
                    binding.excessBitsInput.error = "Size Error"
                } else {
                    binding.excessBitsInput.isErrorEnabled = false
                }
                prefs.edit().putString("bits_excess", text.toString()).apply()

                val excessIdentifier = try {
                    val excessIdentifier = "1".padEnd(text.toString().toInt(), '0')
                    excessIdentifier.toDecimal().toString()
                } catch (e: Exception) {
                    ""
                }
                binding.excessIdentifierEditText.setText(excessIdentifier)
            }
        }
        binding.excessIdentifierEditText.setOnFocusChangeListener { _, focused ->
            val excessBitsString = binding.excessBitsEditText.text.toString()
            if (!focused && excessBitsString.isNotEmpty()){
                val excessIdentifier = excessBitsString.bitsToIdentifier()
                binding.excessIdentifierEditText.setText(excessIdentifier)
            }
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        if (itemId == R.id.item_clear) {
            clear()
        } else if (itemId == R.id.item_copy) {
            copy()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun copy() {
        val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clipData: ClipData? = if (binding.decimalSignedEditText.hasFocus()) {
            ClipData.newPlainText("decimal", binding.decimalSignedEditText.text.toString().trim())
        }  else if (binding.excessEditText.hasFocus()) {
            ClipData.newPlainText("excess", binding.excessEditText.text.toString().trim())
        } else null
        if (clipData != null) {
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(activity, getString(R.string.copied), Toast.LENGTH_LONG).show()
        }
    }

    private fun clear() {
        prefs.edit().putString("decimal_excess", null).apply()
        prefs.edit().putString("bits_excess", null).apply()

        binding.decimalSignedEditText.setText("")
        binding.excessEditText.setText("")
    }
}