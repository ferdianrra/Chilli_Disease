package com.dicoding.deteksicabai.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class HistoryColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "history"
            const val _ID = "_id"
            const val DISEASE = "disease"
            const val PHOTO = "photo"
            const val DESCRIPTION = "description"
            const val DATE = "date"
        }
    }
}