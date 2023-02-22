package com.avidco.intellectcalculator.ui.calculations

import android.os.Bundle
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.utils.FloatingBinaryConvert
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.avidco.intellectcalculator.databinding.FragmentFloatingPointBinding
import com.avidco.intellectcalculator.utils.ExcessConvert
import com.avidco.intellectcalculator.utils.ExcessConvert.bitsToIdentifier
import java.util.*

class FloatingPointFragment : Fragment() {
    
    private lateinit var binding: FragmentFloatingPointBinding
    private lateinit var prefs : SharedPreferences

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        binding = FragmentFloatingPointBinding.inflate(layoutInflater, viewGroup, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.calc_menu, menu)

        binding.doubleIeee754EditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.doubleIeee754EditText.text.toString().isNotEmpty()
        }
        binding.ieee754EditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.ieee754EditText.text.toString().isNotEmpty()
        }
        binding.decimalFloatingEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.decimalFloatingEditText.text.toString().isNotEmpty()
        }
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        prefs = context!!.getSharedPreferences("FloatingPoint", Context.MODE_PRIVATE)

        val floatingPoint = prefs.getString("floating_decimal", null)

        if (floatingPoint != null){
            val fromDecimal = FloatingBinaryConvert.fromDecimal(floatingPoint)
            binding.decimalFloatingInput.error = fromDecimal.errorMessage
            binding.decimalFloatingInput.isErrorEnabled = fromDecimal.error

            binding.decimalFloatingEditText.setText(floatingPoint)
            binding.ieee754EditText.setText(fromDecimal.iEEE754)
            binding.doubleIeee754EditText.setText(fromDecimal.doubleIEEE754)
        }
        setupWatchers()
    }

    private fun setupWatchers() {
        binding.decimalFloatingEditText.doOnTextChanged { text, _, _, _ ->
            if (binding.decimalFloatingEditText.hasFocus()) {
                val fromDecimal = FloatingBinaryConvert.fromDecimal(text.toString().trim())
                binding.decimalFloatingInput.error = fromDecimal.errorMessage
                binding.decimalFloatingInput.isErrorEnabled = fromDecimal.error

                prefs.edit().putString("floating_decimal", text.toString().trim()).apply()
                binding.ieee754EditText.setText(fromDecimal.iEEE754)
                binding.doubleIeee754EditText.setText(fromDecimal.doubleIEEE754)
            }
        }

        binding.ieee754EditText.doOnTextChanged { text, _, _, _ ->
            if (binding.ieee754EditText.hasFocus()) {
                val fromIEEE754 = FloatingBinaryConvert.fromIEEE754(text.toString())
                binding.ieee754Input.error = fromIEEE754.errorMessage
                binding.ieee754Input.isErrorEnabled = fromIEEE754.error

                prefs.edit().putString("floating_decimal", fromIEEE754.decimal).apply()
                binding.decimalFloatingEditText.setText(fromIEEE754.decimal)
                binding.doubleIeee754EditText.setText(fromIEEE754.doubleIEEE754)
            }
        }

        binding.doubleIeee754EditText.doOnTextChanged { text, _, _, _ ->
            if (binding.doubleIeee754EditText.hasFocus()) {
                val fromDoubleIEEE754 = FloatingBinaryConvert.fromDoubleIEEE754(text.toString())

                binding.decimalFloatingInput.error = fromDoubleIEEE754.errorMessage
                binding.decimalFloatingInput.isErrorEnabled = fromDoubleIEEE754.error

                prefs.edit().putString("floating_decimal", fromDoubleIEEE754.decimal).apply()
                binding.decimalFloatingEditText.setText(fromDoubleIEEE754.decimal)
                binding.ieee754EditText.setText(fromDoubleIEEE754.iEEE754)
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

        val clipData: ClipData? = if (binding.decimalFloatingEditText.hasFocus()) {
            ClipData.newPlainText(
                "decimal", Objects.requireNonNull(
                    binding.decimalFloatingEditText.text
                ).toString().trim()
            )
        } else if (binding.ieee754EditText.hasFocus()) {
            ClipData.newPlainText(
                "single_ieee754", Objects.requireNonNull(
                    binding.ieee754EditText.text
                ).toString().trim()
            )
        } else {
            if (binding.doubleIeee754EditText.hasFocus()) ClipData.newPlainText(
                "double_ieee754", Objects.requireNonNull(
                    binding.doubleIeee754EditText.text
                ).toString().trim()
            ) else null
        }
        if (clipData != null) {
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(activity, getString(R.string.copied), Toast.LENGTH_LONG).show()
        }
    }

    private fun clear() {
        binding.decimalFloatingEditText.setText("")
        binding.ieee754EditText.setText("")
        binding.doubleIeee754EditText.setText("")
    }
}