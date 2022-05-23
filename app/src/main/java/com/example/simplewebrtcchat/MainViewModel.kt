package com.example.simplewebrtcchat


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.skyway.Peer.ConnectOption
import io.skyway.Peer.DataConnection
import io.skyway.Peer.Peer
import io.skyway.Peer.PeerOption

class MainViewModel : ViewModel() {
    var localPeerId = MutableLiveData("")
    var remotePeerId = MutableLiveData("")
    var msg = MutableLiveData("")
    private lateinit var peer: Peer
    private lateinit var dataConnection: DataConnection

    fun setup(activity: MainActivity) {
        startPeer(activity)
    }

    fun connectPeer(peerId: String) {
        remotePeerId.value = peerId
        val option = ConnectOption()
        option.label = "chat"
        dataConnection = peer.connect(remotePeerId.value, option)
        if (this::dataConnection.isInitialized) {
            setupDataCallback()
        }
    }

    private fun startPeer(activity: MainActivity) {
        val option = PeerOption()
        option.key = BuildConfig.SKYWAY_APIKEY
        option.domain = BuildConfig.SKYWAY_DOMAIN
        this.peer = Peer(activity, option)
        if (this::peer.isInitialized) {
            setupPeerCallback()
        }
    }

    fun sendData(msg: String) {
        dataConnection.send(msg)
    }

    private fun setupPeerCallback() {
        this.peer.on(Peer.PeerEventEnum.OPEN) { p0 ->
            (p0 as String).let { peerID ->
                Log.d("debug", "peerID: $peerID")
                this@MainViewModel.localPeerId.value = peerID
            }
        }
        this.peer.on(Peer.PeerEventEnum.CONNECTION) { p0 ->
            (p0 as DataConnection).let { _dataConnection ->
                this@MainViewModel.dataConnection = _dataConnection
                setupDataCallback()
            }
        }
        this.peer.on(
            Peer.PeerEventEnum.ERROR
        ) { p0 -> Log.d("debug", "peer error $p0") }
        this.peer.on(
            Peer.PeerEventEnum.CLOSE
        ) { Log.d("debug", "close peer connection") }
    }

    private fun setupDataCallback() {
        dataConnection.on(DataConnection.DataEventEnum.DATA) { p0 ->
            var data = ""
            when (p0) {
                p0 is Array<*> -> {
                    for (s in p0 as Array<*>) {
                        data += "${s.toString()}\n"
                    }
                }
                else -> {
                    try {
                        data = p0.toString()
                    } catch (e: Error) {
                        Log.e("debug", "setupDataCallback: ${e.printStackTrace()}")
                    }
                }
            }
            msg.value = data
        }
        dataConnection.on(
            DataConnection.DataEventEnum.CLOSE
        ) { this@MainViewModel.remotePeerId.value = "" }
        dataConnection.on(
            DataConnection.DataEventEnum.ERROR
        ) { p0 -> Log.e("error", (p0 as Error).printStackTrace().toString()) }
    }


}