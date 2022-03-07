package com.misit.abpenergy.Socket

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.misit.abpenergy.R
import kotlinx.android.synthetic.main.activity_socket.*

class SocketActivity : AppCompatActivity() {
    private var chat : ArrayList<String>?=null
    private var adapter:ChatAdapter?=null
    private var viewModel:ChatViewModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title="Pesan"
        chat = ArrayList()

        viewModel = ViewModelProvider(this@SocketActivity).get(ChatViewModel::class.java)
        viewModels()

        adapter = ChatAdapter(this@SocketActivity,chat!!)
        val linearLayoutManager = LinearLayoutManager(this@SocketActivity)
        rvChat?.layoutManager = linearLayoutManager
        rvChat.adapter = adapter

        btnSubmit.setOnClickListener {
            var pesan = inChat.text.toString()
            if(pesan!=null){
                viewModel?.sendChat(pesan)
            }
        }
    }
    private fun viewModels(){
        viewModel?.chatObserver()?.observe(this@SocketActivity,{ chatting->
            Log.d("chatting","$chatting")
            if(chatting!=null){
                chat?.addAll(chatting)
                adapter?.notifyDataSetChanged()
                inChat.text =null
            }
        })
        viewModel?.readChat()
    }
    override fun onDestroy() {
        viewModel?.destroyChat()
        super.onDestroy()
    }

    override fun onPause() {
        viewModel?.destroyChat()
        super.onPause()
    }

}