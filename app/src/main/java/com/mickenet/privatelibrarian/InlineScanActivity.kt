package com.mickenet.privatelibrarian


import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.mickenet.privatelibrarian.ISBN.BookClient
import com.mickenet.privatelibrarian.books.Book
import com.mickenet.privatelibrarian.books.BookAdapter
import com.mickenet.privatelibrarian.database.DatabaseHandler
import kotlinx.android.synthetic.main.activity_inline_scan.*
import okhttp3.Headers
import org.json.JSONArray


class InlineScanActivity : AppCompatActivity() {
    private lateinit var captureManager: CaptureManager
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val adapter = BookAdapter()
    private var torchState: Boolean = false
    var db = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inline_scan)
        val text = "scanning..."
        val duration = Toast.LENGTH_SHORT

        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        viewManager = LinearLayoutManager(this)
        simple_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //https://android.jlelse.eu/recylerview-list-adapter-template-in-kotlin-6b9814201458
        simple_recyclerview.adapter = adapter
        btnScan.setOnClickListener {
            //txtResult.text = "scanning..."
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
            barcodeView.decodeSingle(object: BarcodeCallback{
                override fun barcodeResult(result: BarcodeResult?) {
                    result?.let {
                        txtisbn.setText(it.text.toString())
                       fetchBooks(it.text.toString())
                        val vib: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        if(vib.hasVibrator())
                            // void vibrate (VibrationEffect vibe)
                            vib.vibrate(
                                VibrationEffect.createOneShot(
                                    100,
                                    // The default vibration strength of the device.
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                    }
                }

                override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                }
            })
        }

        btnTorch.setOnClickListener {
            if(torchState){
                torchState = false
                barcodeView.setTorchOff()
            } else {
                torchState = true
                barcodeView.setTorchOn()
            }
        }
        btnSearch.setOnClickListener {
            fetchBooks(txtisbn.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }
    private fun fetchBooks(isbn:String){
        val client = BookClient()

        client.getBooks(isbn, object: JsonHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                val docs: JSONArray
                if (json != null) {
                    // Get the docs json array
                    docs = json.jsonObject.getJSONArray("docs")
                    // Parse json array into array of model objects
                    val books: ArrayList<Book> = Book.fromJson(docs)
                    // Load model objects into the adapter
                    try {
                        for (book in books) {
                            db.addBook(book)
                         }
                    } catch (e: Exception) {
                    }
                    adapter.submitList(books)

                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                val text = "Failure"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()

            }

        })
    }

}
