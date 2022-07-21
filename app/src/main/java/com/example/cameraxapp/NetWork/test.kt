package com.example.cameraxapp.NetWork

import android.util.Log

class test {
    fun run() {
        Thread { Log.d("test", "test") }.start()
    }
}