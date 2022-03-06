package com.misit.abpenergy.Socket

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.misit.abpenergy.R
import es.dmoral.toasty.Toasty
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import kotlinx.android.synthetic.main.activity_socket.*
import java.net.URI
import java.net.URISyntaxException

class SocketActivity : AppCompatActivity() {
    private var mSocket: Socket?=null
    private var opts:IO.Options?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()
        mSocket?.emit("newMessage");
        mSocket?.on("newMessage",{
            if(it!=null){
                var pesan = it[0] as Int
                runOnUiThread {
                    Toasty.success(this,"${it.toString()}").show()
                    tvChat.text = "$pesan"
                }
            }

        })
        btnSubmit.setOnClickListener {
            mSocket?.emit("newMessage")
        }
    }

    override fun onStop() {
        SocketHandler.closeConnection()
        super.onStop()
    }

}