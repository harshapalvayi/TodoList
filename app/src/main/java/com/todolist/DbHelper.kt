package com.todolist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper: SQLiteOpenHelper {
    constructor(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : super(context, "UserData", null, 1)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create Table UserList(id Integer  primary key AUTOINCREMENT NOT NULL, text TEXT, isChecked Boolean)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
       db?.execSQL("drop Table if exists UserList")
    }

    fun insertData(text: String, isChecked: Boolean): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues().apply {
            put("text", text)
            put("isChecked", isChecked)
        }
        val result: Long = db.insert("UserList", null, values)
        return result != -1L
    }

    fun updateData(text: String, isChecked: Boolean): Boolean {
        var result: Int = 0
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues().apply {
            put("text", text)
            put("isChecked", isChecked)
        }
        result = db.update("UserList", ContentValues().apply {
            put("isChecked", isChecked)
        }, "text = ?", arrayOf(text))
        return result != -1
    }

    fun deleteData(text: String) {
        val db: SQLiteDatabase = this.writableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM UserList WHERE text=? AND isChecked = 1", arrayOf(text))
        if (cursor.count > 0) {
            db.delete("UserList", "text=?", arrayOf(text))
        }
    }

    fun checkIfExists(text: String): Boolean {
      val db: SQLiteDatabase = this.writableDatabase
      val cursor: Cursor = db.rawQuery("SELECT * FROM UserList WHERE text=?", arrayOf(text))
        return cursor.count > 0
    }

    fun getData(): Cursor {
        val db: SQLiteDatabase = this.writableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM UserList", null)
        return cursor
    }
}