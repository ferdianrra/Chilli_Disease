package com.dicoding.deteksicabai.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dicoding.deteksicabai.db.DatabaseContract.HistoryColumns.Companion.TABLE_NAME

internal class DatabaseHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        private const val DATABASE_NAME = "dbhistoryapp"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_HISTORY = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.HistoryColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.HistoryColumns.DISEASE} TEXT NOT NULL," +
                " ${DatabaseContract.HistoryColumns.PHOTO} TEXT NOT NULL," +
                " ${DatabaseContract.HistoryColumns.DESCRIPTION} TEXT NOT NULL," +
                " ${DatabaseContract.HistoryColumns.DATE} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_HISTORY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}