package com.dicoding.deteksicabai.entity

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class history (
    var id: Int = 0,
    var disease: String = "",
    var descDisease: String = " ",
    var photoLeaf: String= "",
    var date: String = ""
): Parcelable