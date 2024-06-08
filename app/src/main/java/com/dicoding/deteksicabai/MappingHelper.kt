package com.dicoding.deteksicabai

import android.database.Cursor
import com.dicoding.deteksicabai.db.DatabaseContract
import com.dicoding.deteksicabai.entity.history

object MappingHelper {
    // Mengonversi dari Cursor ke Arraylist
    fun mapCursorToArrayList(historyCursor: Cursor?): ArrayList<history> {
        val historyList = ArrayList<history>()
        historyCursor?.apply {
            while (moveToNext()) {
                //  Di sini kita ambil datanya satu per satu menggunakan getColumnIndexOrThrow
                //  dan dimasukkan ke dalam ArrayList.
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.HistoryColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.HistoryColumns.DISEASE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.HistoryColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.HistoryColumns.DATE))
                val picture = getString(getColumnIndexOrThrow(DatabaseContract.HistoryColumns.PHOTO))
                historyList.add(history(id, title, description, picture, date))
            }
        }
        return historyList
    }
}