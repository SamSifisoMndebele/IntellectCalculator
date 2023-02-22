package com.avidco.intellectcalculator.ui.calculations.history

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(val context: Context?) : SQLiteOpenHelper(context, TABLE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                                                       $COL1 TEXT NOT NULL UNIQUE, 
                                                       $COL2 TINYINT NOT NULL,
                                                       $PREFIX TEXT NOT NULL,
                                                       $INFIX TEXT NOT NULL,
                                                       $POSTFIX TEXT NOT NULL)"""
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        db.execSQL("""DROP TABLE IF EXISTS $TABLE_NAME""")
        onCreate(db)
    }

    fun addData(string: String, type : Int, prefix: String,infix: String,postfix: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL1, string)
            put(COL2, type)
            put(PREFIX, prefix)
            put(INFIX, infix)
            put(POSTFIX, postfix)
        }

        val result = db.insert(TABLE_NAME, null, contentValues)
        println( "addData: Adding $string to $TABLE_NAME")
        //if inserted incorrectly it will return -1
        return result != -1L
    }

    val dataList: ArrayList<StringModel> get() {
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        val listData = ArrayList<StringModel>()
        while (cursor.moveToNext()) {
            listData.add(StringModel(cursor.getString(1),cursor.getString(2).toInt(),cursor.getString(3),cursor.getString(4),cursor.getString(5)))
            println( "Data: cursor: ${cursor.getString(1)}")
        }

        return listData
    }

    val last : StringModel? get() {
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $ID DESC LIMIT 1"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToNext()) {
            //println( "Data2: cursor: ${cursor.getString(1)}, ${cursor.getString(2)}")
            return StringModel(cursor.getString(1),cursor.getString(2).toInt(),cursor.getString(3),cursor.getString(4),cursor.getString(5))
        }

        return null
        //context?.getString(R.string.default_prefix)
        //context?.getString(R.string.default_infix)
        //context?.getString(R.string.default_postfix)
    }

    private fun getItemID(name: String): Int {
        val db = this.writableDatabase
        val query = """SELECT $ID FROM $TABLE_NAME WHERE $COL1 = '$name'"""
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToNext()) {
            return cursor.getString(0).toInt()
        }
        return 0
    }

    fun updateString(string: String) : StringModel? {

        val db = this.writableDatabase
        val newPos = if (last != null) getItemID(last!!.string) + 1 else 1

        db.execSQL("""UPDATE $TABLE_NAME SET $ID = $newPos WHERE $COL1 = '$string'""".trimMargin())

        val query = "SELECT * FROM $TABLE_NAME WHERE $COL1 = '$string'"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToNext()) {
           // println( "Data2: cursor: ${cursor.getString(1)}, ${cursor.getString(0)}")
            return StringModel(cursor.getString(1),cursor.getString(2).toInt(),cursor.getString(3),cursor.getString(4),cursor.getString(5))
        }

        return null
    }
    
    fun deleteString(string: String) {
        val db = this.writableDatabase
        val query = ("""DELETE FROM $TABLE_NAME WHERE $COL1 = '$string'""")
        println("deleteName: query: $query, \nDeleting $string from database.")
        db.execSQL(query)
    }

    fun clearStrings() {
        val db = this.writableDatabase
        val query = ("""DELETE FROM $TABLE_NAME""")
        db.execSQL(query)
    }

    companion object {
        private const val TABLE_NAME = "StringTable6"
        private const val ID = "Id"
        private const val COL1 = "String"
        private const val COL2 = "Type"
        private const val PREFIX = "Prefix"
        private const val INFIX = "Infix"
        private const val POSTFIX = "Postfix"

        const val Prefix = 1
        const val Infix = 2
        const val Postfix = 3
    }
}