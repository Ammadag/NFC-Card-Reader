package com.example.nfccardreader.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.nfccardreader.R


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSend = findViewById<Button>(R.id.btn_Sender)
        val buttonReceive = findViewById<Button>(R.id.btn_reciever)

        buttonSend.setOnClickListener {
            val intent = Intent(this, SenderActivity::class.java)
            startActivity(intent)
        }
        buttonReceive.setOnClickListener {
            val intent = Intent(this, CardReaderActivity::class.java)
            startActivity(intent)
        }



    }
}
