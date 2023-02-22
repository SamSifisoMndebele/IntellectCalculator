package com.avidco.intellectcalculator.utils

import com.avidco.intellectcalculator.utils.BinaryArithmetic.fillBits
import com.avidco.intellectcalculator.utils.BinaryArithmetic.toDecimal
import kotlin.Exception

object ExcessConvert {
    fun fromDecimal(decimalString: String, excessBits : String): Conversion {
        val excessIdentifier: Long = excessBits.bitsToIdentifier().toLong()
        return try {
            val excess = decimalString.decimalToExcess(excessIdentifier, excessBits.toInt())
            Conversion(decimalString, excess, excessIdentifier.toString(), excessBits.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
            Conversion("", "", excessIdentifier.toString(), excessBits.toInt()).also {
                it.error = (decimalString.isNotEmpty() && decimalString != "-") }
        }
    }

    fun fromExcess(excessString: String, excessBits : String): Conversion {
        val excessIdentifier: Long = excessBits.bitsToIdentifier().toLong()
        return try {
            if (excessString.isNotEmpty()){
                val decimal = excessString.toDecimal() - excessIdentifier
                fromDecimal(decimal.toString(), excessBits)
            } else {
                Conversion("", "", excessIdentifier.toString(), excessBits.toInt())
            }

        } catch (unused: Exception) {
            Conversion("", "", excessIdentifier.toString(), excessBits.toInt()).also {
                it.error = excessString.isNotEmpty() }
        }
    }

    //////
    fun String.identifierToBits(): String {
        return try {
            val excessIdentifier = this.toLong()
            val excessBits = java.lang.Long.toBinaryString(excessIdentifier)
            excessBits.length.toString()
        } catch (e: Exception) {
            ""
        }
    }

    fun String.bitsToIdentifier(): String {
        return try {
            val excessBits = this.toInt()
            val excessIdentifier = "1".padEnd(excessBits, '0')
            excessIdentifier.toDecimal().toString()
        } catch (e: Exception) {
            ""
        }
    }

    fun Long.isValidIdentifier() : Boolean {
        var b = true
        var i = this
        while (i > 0){
            if ((i % 2) != 0.toLong()){
                b = false
                break
            }
            i/=2
        }
        return b
    }

    private fun String.decimalToExcess(excessIdentifier: Long, excessBits : Int): String {
        val excessLong = this.toLong() + excessIdentifier
        val binary = java.lang.Long.toBinaryString(excessLong).fillBits(true)

        if (excessLong < 0 || excessIdentifier*2-1 < binary.toDecimal())
            throw Exception()

        val excessBinary = binary.takeLast(excessBits-1)
        return if (this.toLong() < 0.toLong())
            "0$excessBinary"
        else
            "1$excessBinary"
    }
}