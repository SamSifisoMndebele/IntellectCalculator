package com.avidco.intellectcalculator.ui.calculations

import android.os.Bundle
import com.avidco.intellectcalculator.R
import com.avidco.intellectcalculator.utils.ComplementConvert
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.avidco.intellectcalculator.databinding.FragmentComplementBinding

class ComplementFragment : Fragment() {

    private lateinit var binding: FragmentComplementBinding
    private lateinit var prefs : SharedPreferences

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        binding = FragmentComplementBinding.inflate(layoutInflater, viewGroup, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        prefs = context!!.getSharedPreferences("Bases", Context.MODE_PRIVATE)

        val decimal = prefs.getString("decimal_compliment", null)
        if (decimal != null){
            val fromDecimal = ComplementConvert.fromDecimal(decimal)

            binding.decimalSignedInput.error = fromDecimal.errorMessage
            binding.decimalSignedInput.isErrorEnabled = fromDecimal.error
            binding.decimalSignedEditText.setText(decimal)
            binding.complement2EditText.setText(fromDecimal.complement2)
            binding.complement1EditText.setText(fromDecimal.complement1)
        }

        setupWatchers()
    }

    private fun setupWatchers() {
        binding.decimalSignedEditText.doOnTextChanged { text, _, _, _ ->
            if (binding.decimalSignedEditText.hasFocus()) {
                val fromDecimal = ComplementConvert.fromDecimal(text.toString())

                prefs.edit().putString("decimal_compliment", text.toString()).apply()
                binding.decimalSignedInput.error = fromDecimal.errorMessage
                binding.decimalSignedInput.isErrorEnabled = fromDecimal.error
                binding.complement2EditText.setText(fromDecimal.complement2)
                binding.complement1EditText.setText(fromDecimal.complement1)
            }
        }

        binding.complement2EditText.doOnTextChanged { text, _, _, _ ->
            if (binding.complement2EditText.hasFocus()) {
                val fromComplement2 = ComplementConvert.fromComplement2(text.toString())

                prefs.edit().putString("decimal_compliment", fromComplement2.decimal).apply()
                binding.complement2Input.error = fromComplement2.errorMessage
                binding.complement2Input.isErrorEnabled = fromComplement2.error
                binding.decimalSignedEditText.setText(fromComplement2.decimal)
                binding.complement1EditText.setText(fromComplement2.complement1)
            }
        }

        binding.complement1EditText.doOnTextChanged { text, _, _, _ ->
            if (binding.complement1EditText.hasFocus()) {
                val fromComplement1 = ComplementConvert.fromComplement1(text.toString())

                prefs.edit().putString("decimal_compliment", fromComplement1.decimal).apply()
                binding.complement1Input.error = fromComplement1.errorMessage
                binding.complement1Input.isErrorEnabled = fromComplement1.error
                binding.decimalSignedEditText.setText(fromComplement1.decimal)
                binding.complement2EditText.setText(fromComplement1.complement2)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.calc_menu, menu)

        binding.decimalSignedEditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.decimalSignedEditText.text.toString().isNotEmpty()
        }
        binding.complement2EditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.complement2EditText.text.toString().isNotEmpty()
        }
        binding.complement1EditText.setOnFocusChangeListener { _, focused ->
            menu.findItem(R.id.item_copy).isVisible = focused &&
                    binding.complement1EditText.text.toString().isNotEmpty()
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
        } else if (binding.complement2EditText.hasFocus()) {
            ClipData.newPlainText("complement2", binding.complement2EditText.text.toString().trim())
        } else if (binding.complement1EditText.hasFocus()) {
            ClipData.newPlainText("complement1", binding.complement1EditText.text.toString().trim())
        } else null
        if (clipData != null) {
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(activity, getString(R.string.copied), Toast.LENGTH_LONG).show()
        }
    }

    private fun clear() {
        prefs.edit().putString("decimal_compliment", null).apply()
        binding.decimalSignedEditText.setText("")
        binding.complement2EditText.setText("")
        binding.complement1EditText.setText("")
    }
}