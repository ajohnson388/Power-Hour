package com.meaningless.powerhour.utils

import android.net.Uri
import com.google.gson.Gson
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream


object DataUtils {

    fun parseIntentData(uri: Uri): Map<String, String> {
        val data = hashMapOf<String, String>()
        val dataString = uri.toString().split("?").lastOrNull() ?: return data
        val entries = dataString.split("&")
        for (entry in entries) {
            val parts = entry.split("=")
            if (parts.size != 2) {
                throw Error("Intent data URI is malformed: $parts")
            }
            data[parts[0]] = parts[1]
        }
        return data
    }

    fun <T> makeModel(string: String, type: Class<T>): T =
        Gson().fromJson<T>(string, type)

    fun <T> makeModel(json: Map<String, String>, type: Class<T>): T =
        makeModel(json.toString(), type)

    fun <T> makeJsonString(obj: T): String =
        Gson().toJson(obj).toString()

    fun makeString(bitmap: Bitmap): String {
        val encodedImage: String
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.PNG, 100,
            outputStream
        )
        val byteArr = outputStream.toByteArray()
        encodedImage = Base64.encodeToString(byteArr, Base64.DEFAULT)
        return encodedImage
    }

    fun makeBitmap(string: String): Bitmap {
        val decodedString = Base64.decode(string, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}