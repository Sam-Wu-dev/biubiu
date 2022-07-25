package com.example.cameraxapp.NetWork

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.*
import java.net.Socket
import java.util.*

class Tcp () {
    private var mBufferOut: PrintWriter? = null
    private var mBufferIn: BufferedReader? = null
    var isRunning = false
    private var id = 0
    private var count = 0
    private val replyOnce: JsonObject
        get() {
            var reply = JsonObject()
            var s: String?
            while (true) {
                try {
                    s = mBufferIn!!.readLine()
                    if (s != null) {
                        try {
                            Log.d("test", "receive from server: $s")
                            reply = stringToJson(s)
                            Log.d("test", "json to string: $reply")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        break
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return reply
        }

    fun sendShoot(jpeg:ByteArray){
        Log.d("tcp", "sending frame " + count.toString() +" to server")
        count++
        val json = JsonObject()
        json.addProperty("type","S")
        json.addProperty("size",jpeg.size)
        json.addProperty("Base64_JPEG", Base64.encodeToString(jpeg, Base64.NO_WRAP))
        sendToServer(json)
    }
    private fun sendToServer(message: JsonObject?) {
        val s = message.toString()
        if (mBufferOut != null) {
            //Log.d("test", "Size of sending message: " + s.length)
            mBufferOut!!.println(s)
            mBufferOut!!.flush()
        }
    }

    init {
        Thread{
            val socket = Socket(SERVER_IP, SERVER_PORT)
            Log.d("test", "Connect to server: $SERVER_IP")
            mBufferOut = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
            mBufferIn = BufferedReader(InputStreamReader(socket.getInputStream()))
            val json = JsonObject()
            json.addProperty("type","EGR")
            json.addProperty("name","test player")
            sendToServer(json)
            id = replyOnce.get("id").asInt
            Log.d("tcp","Tcp is initialized")
            Log.d("tcp", "Id is $id")
            isRunning=true
        }.start()
    }

    private fun stringToJson(s: String?): JsonObject {
        val gson = Gson()
        return gson.fromJson(s, JsonObject::class.java)
    }

    companion object {
        var instance: Tcp? = null
            get() {
                if (field == null) {
                    field = Tcp()
                }
                return field
            }
            private set
        const val SERVER_IP = "192.168.0.107" //server IP address
        const val SERVER_PORT = 1337
    }
}