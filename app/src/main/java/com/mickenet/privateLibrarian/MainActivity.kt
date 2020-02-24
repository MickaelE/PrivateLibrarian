package com.mickenet.privateLibrarian

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.mickenet.privateLibrarian.Books.Book
import com.mickenet.privateLibrarian.Books.BookAdapter
import com.mickenet.privateLibrarian.R.id
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var scannedResult: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val adapter = BookAdapter()

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val books =ArrayList<Book>()
        viewManager = LinearLayoutManager(this)
//https://android.jlelse.eu/recylerview-list-adapter-template-in-kotlin-6b9814201458
            recyclerView.adapter = adapter
        btnScan.setOnClickListener {
            run {
                IntentIntegrator(this@MainActivity).initiateScan()s
                adapter.submitList()
            }
        }
        // Parse json array into array of model objects

        recyclerView = findViewById<RecyclerView>(id.simple_recyclerview).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                scannedResult = result.contents
                txtValue.text = scannedResult
            } else {
                txtValue.text = "scan failed"
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        outState?.putString("scannedResult", scannedResult)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            scannedResult = it.getString("scannedResult")!!
            txtValue.text = scannedResult
        }
    }

}

