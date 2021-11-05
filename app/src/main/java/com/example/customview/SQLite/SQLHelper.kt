package com.example.appmusicmvvm.SQLite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "Password.db"
        const val DB_TABLE = "PasswordTable"
        const val DB_VERSION = 1
        const val DB_KEY = "key"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val queryCreateTable = "CREATE TABLE $DB_TABLE (" +
                "$DB_KEY string not null primary key"+
                ")"
        db.execSQL(queryCreateTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion != oldVersion) {
            db.execSQL("DROP TABLE IF exists $DB_TABLE")
            onCreate(db)
        }
    }
    fun addKey(string: String) {
        val sqLiteDatabase = writableDatabase
        var contentValues = ContentValues()
        contentValues.put("$DB_KEY", string)
        sqLiteDatabase.insert(DB_TABLE, null, contentValues)
    }

    fun removeAll(){
        val sqLiteDatabase = writableDatabase
        sqLiteDatabase.delete(DB_TABLE,null,null)
    }

    @SuppressLint("Range")
    fun getAll(): MutableList<String>{
        val sqlList: MutableList<String> = mutableListOf()
        val sqLiteDatabase = readableDatabase
        val cursor = sqLiteDatabase.query(
            false,
            DB_TABLE,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val key = cursor.getString(cursor.getColumnIndex("$DB_KEY"))
            sqlList.add(key)
        }
        return sqlList
    }
}