package com.avidco.intellectcalculator.utils

import java.lang.NumberFormatException
import java.util.*
import kotlin.math.pow

object BaseConvert {
    private val hexLetters: HashMap<String?, Long?> = object : HashMap<String?, Long?>() {
        init {
            put("A", 10L)
            put("B", 11L)
            put("C", 12L)
            put("D", 13L)
            put("E", 14L)
            put("F", 15L)
        }
    }

    @JvmStatic
    fun fromChar(charArray: CharSequence): Conversion {

        var decimalString = ""
        for (char in charArray) {
            decimalString = decimalString + char.code.toLong().toString() + " "
        }
        return fromDecimal(decimalString)
    }

    @JvmStatic
    fun fromDecimal(decimalString: String): Conversion {
        var conversion = Conversion()

        var binaryString = ""
        var octalString = ""
        var hexString = ""
        var asciiString = ""

        for (digit in decimalString.split(" ".toRegex()).toTypedArray()) {
            if (digit.isNotEmpty()) {
                try {
                    val parseLong = digit.toLong()
                    binaryString = binaryString + java.lang.Long.toBinaryString(parseLong) + " "
                    try {
                        octalString = octalString + java.lang.Long.toOctalString(parseLong) + " "
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        conversion.error = true
                    }
                    try {
                        hexString = hexString + java.lang.Long.toHexString(parseLong).uppercase(Locale.getDefault()) + " "
                        try {
                            asciiString += parseLong.toInt().toChar().toString()
                        } catch (e2: NumberFormatException) {
                            e2.printStackTrace()
                            conversion.error = true
                        }
                        try {
                            conversion = Conversion(decimalString, binaryString, octalString, hexString, asciiString)
                            if (parseLong == Long.MAX_VALUE) {
                                try {
                                    conversion.error = true
                                } catch (e3: NumberFormatException) {
                                }
                            }
                        } catch (e4: NumberFormatException) {
                            e4.printStackTrace()
                            conversion.error = true
                        }
                    } catch (e5: NumberFormatException) {
                        e5.printStackTrace()
                        conversion.error = true
                    }
                } catch (e6: NumberFormatException) {
                    e6.printStackTrace()
                    conversion.error = true
                }
            }
        }
        return conversion
    }

    @JvmStatic
    fun fromRadix(numbersString: String, base: Int): Conversion {
        if (numbersString.isEmpty()) {
            return Conversion()
        }
        var decimalString = ""
        for (number in numbersString.uppercase(Locale.getDefault()).split(" ".toRegex()).toTypedArray()) {
            if (number.isNotEmpty()) {
                var remainder: Long = 0
                var i = 0
                var numb: Long
                for (digit in number.reversed()){
                    try {
                        numb = if (hexLetters.containsKey(digit.toString())) {
                            hexLetters[digit.toString()]!!.toLong()
                        } else {
                            digit.toString().toLong()
                        }

                        val remainderDouble = remainder.toDouble()
                        val numbDouble = numb.toDouble()

                        java.lang.Double.isNaN(numbDouble)
                        java.lang.Double.isNaN(remainderDouble)

                        remainder = (remainderDouble + numbDouble * base.toDouble().pow(i.toDouble())).toLong()

                        i++
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        return Conversion().apply { error = true }
                    }
                }
                decimalString = "$decimalString$remainder "
            }
        }
        return fromDecimal(decimalString)
    }
}