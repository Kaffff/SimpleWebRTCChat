package com.example.simplewebrtcchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplewebrtcchat.ui.theme.SimpleWebRTCChatTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleWebRTCChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainView(viewModel, this)
                }
            }
        }
    }
}

@Composable
fun MainView(viewModel: MainViewModel, activity: MainActivity, modifier: Modifier = Modifier) {
    val localPeerId = viewModel.localPeerId.observeAsState()
    val remotePeerId = viewModel.remotePeerId.observeAsState()
    val msg = viewModel.msg.observeAsState()
    Column(modifier = modifier.padding(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                text = "自分: ",
                modifier = Modifier
                    .weight(1.5f)
            )

            Text(
                text = if (localPeerId.value != null) localPeerId.value!! else "",
                modifier = Modifier
                    .weight(6.5f)
            )
            Button(
                onClick = {
                    viewModel.setup(activity)
                },
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .padding(start = 5.dp)
            ) {
                Text("生成")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                text = "相手: ",
                modifier = Modifier
                    .weight(1.5f)
            )

            var inputText by remember { mutableStateOf("") }
            remotePeerId.value?.let {
                if (it.isNotEmpty()) {
                    inputText = it
                }
            }
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Peer ID") },
                modifier = Modifier
                    .weight(6.5f)
                    .height(50.dp)
            )

            Button(
                onClick = {
                    viewModel.connectPeer(inputText)
                },
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .padding(start = 5.dp)
            ) {
                Text("登録")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            var messageText by remember {
                mutableStateOf("")
            }
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Enter text") },
                maxLines = 8,
                modifier = Modifier.weight(6.5f)
            )
            Button(
                onClick = {
                    viewModel.sendData(messageText)
                },
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .padding(start = 5.dp)
            ) {
                Text("送信", softWrap = false)
            }
        }
        Text(text = msg.value!!, modifier = Modifier.fillMaxWidth())


    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimpleWebRTCChatTheme {
        val activity = MainActivity()
        val viewModel = MainViewModel()
        MainView(viewModel, activity)
    }
}