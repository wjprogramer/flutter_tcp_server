package com.example.flutter_tcp_server

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.flutter.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class TcpServerService : Service() {
    private var serverSocket: ServerSocket? = null
    private val working = AtomicBoolean(true)
    private val runnable = Runnable {
        var socket: Socket? = null
        try {
            Log.i(TAG, "Run TCP Server Service")
            serverSocket = ServerSocket(PORT)
            while (working.get()) {
                if (serverSocket != null) {
                    socket = serverSocket!!.accept()
                    Log.i(TAG, "New client ____ : $socket")
                    val dataInputStream = DataInputStream(socket.getInputStream())
                    val dataOutputStream = DataOutputStream(socket.getOutputStream())

                    // Use threads for each client to communicate with them simultaneously
                    val t: Thread = TcpClientHandler(dataInputStream, dataOutputStream)
                    t.start()
                } else {
                    Log.e(TAG, "Couldn't create ServerSocket!")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                socket?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        startMeForeground()
        Thread(runnable).start()
    }

    override fun onDestroy() {
        working.set(false)
    }

    private fun startMeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannelId = packageName
            val channelName = "Tcp Server Background Service"
            val chan = NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.yurucamp)
                .setContentTitle("Tcp Server is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
        } else {
            startForeground(1, Notification())
        }
    }

    companion object {
        private val TAG = "[TCP] ${TcpServerService::class.java.simpleName}"
        private const val PORT = 9876
    }

//    val ANSI_BLACK_BACKGROUND = "\u001B[40m"
//    val ANSI_RED_BACKGROUND = "\u001B[41m"
//    val ANSI_GREEN_BACKGROUND = "\u001B[42m"
//    val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
//    val ANSI_BLUE_BACKGROUND = "\u001B[44m"
//    val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
//    val ANSI_CYAN_BACKGROUND = "\u001B[46m"
//    val ANSI_WHITE_BACKGROUND = "\u001B[47m"
//
//    val ANSI_RESET = "\u001B[0m"
//    val ANSI_BLACK = "\u001B[30m"
//    val ANSI_RED = "\u001B[31m"
//    val ANSI_GREEN = "\u001B[32m"
//    val ANSI_YELLOW = "\u001B[33m"
//    val ANSI_BLUE = "\u001B[34m"
//    val ANSI_PURPLE = "\u001B[35m"
//    val ANSI_CYAN = "\u001B[36m"
//    val ANSI_WHITE = "\u001B[37m"
}