package com.mickenet.privateLibrarian


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.mickenet.privateLibrarian.Books.Book
import com.mickenet.privateLibrarian.Books.BookAdapter
import com.mickenet.privateLibrarian.ISBN.BookClient
import com.mickenet.privateLibrarian.database.DatabaseHandler
import kotlinx.android.synthetic.main.activity_inline_scan.*
import okhttp3.Headers
import org.json.JSONArray


class InlineScanActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager
    var scanState: Boolean = false
    var torchState: Boolean = false
    var bookAdapter: BookAdapter? = null
    val aBooks: ArrayList<Book> = ArrayList<Book>()
    var db = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inline_scan)
        var lvBooks = findViewById<ListView>(R.id.lvbooks);
        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        bookAdapter = BookAdapter(this, aBooks)
        lvBooks?.adapter = bookAdapter

        btnScan.setOnClickListener {
            txtResult.text = "scanning..."
            barcodeView.decodeSingle(object: BarcodeCallback{
                override fun barcodeResult(result: BarcodeResult?) {
                    result?.let {
                        txtResult.text = it.text
                       fetchBooks(it.text)
                        val vib: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        if(vib.hasVibrator()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            // void vibrate (VibrationEffect vibe)
                            vib.vibrate(
                                VibrationEffect.createOneShot(
                                    100,
                                    // The default vibration strength of the device.
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        }else{
                            // This method was deprecated in API level 26
                            vib.vibrate(100)
                        }
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
        var client = BookClient()

        client.getBooks(isbn, object: JsonHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                var docs: JSONArray
                var book: Book
                if (json != null) {
                    // Get the docs json array
                    docs = json.jsonObject.getJSONArray("docs")
                    // Parse json array into array of model objects
                    var books: ArrayList<Book> = Book.fromJson(docs);
                    // Remove all books from the adapter
                    bookAdapter?.clear();
                    // Load model objects into the adapter
                    for (book in books) {
                       db.addBook(book)
                        bookAdapter?.add(book); // add book through the adapter
                    }
                    bookAdapter?.notifyDataSetChanged();
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
