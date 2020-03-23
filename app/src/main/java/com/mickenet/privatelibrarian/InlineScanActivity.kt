package com.mickenet.privatelibrarian


import android.content.Context
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.mickenet.privatelibrarian.ISBN.GoogleBooksApi
import com.mickenet.privatelibrarian.books.BookAdapter
import com.mickenet.privatelibrarian.books.LocalBook
import com.mickenet.privatelibrarian.database.DatabaseHandler
import kotlinx.android.synthetic.main.activity_inline_scan.*


class InlineScanActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val adapter = BookAdapter()
    var scanState: Boolean = false
    var torchState: Boolean = false
    var db = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inline_scan)

        val text = "scanning..."
        val duration = Toast.LENGTH_SHORT
        var policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy)
        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        simple_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //https://android.jlelse.eu/recylerview-list-adapter-template-in-kotlin-6b9814201458
        simple_recyclerview.adapter = adapter
        var booklist = db.allBooks
        adapter.submitList(booklist)


        btnScan.setOnClickListener {
            //txtResult.text = "scanning..."
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
            barcodeView.decodeSingle(object : BarcodeCallback {
                override fun barcodeResult(result: BarcodeResult?) {
                    result?.let {
                        txtIbdn.setText(it.text.toString())
                        fetchBooks(it.text.toString())
                        val vib: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        if (vib.hasVibrator()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // void vibrate (VibrationEffect vibe)
                            vib.vibrate(
                                VibrationEffect.createOneShot(
                                    100,
                                    // The default vibration strength of the device.
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            )
                        } else {
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
            if (torchState) {
                torchState = false
                barcodeView.setTorchOff()
            } else {
                torchState = true
                barcodeView.setTorchOn()
            }
        }
        btnSearch.setOnClickListener() {
            fetchBooks(txtIbdn.text.toString())
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
public fun deleteBook(isbn: LocalBook) {
    db.deleteBook(isbn)
    adapter.notifyItemRemoved( adapter.currentList.indexOf(isbn))
}
    private fun fetchBooks(isbn: String) {
        val text = "Failure"
        var exist: Long
        var oldList = adapter.currentList
        lateinit var  bookList : List<LocalBook?>
        val duration = Toast.LENGTH_SHORT
        try {
            var query = "isbn:$isbn"
            val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
            bookList = GoogleBooksApi.queryGoogleBooks(jsonFactory, query)
             exist = db.getBooksCount(isbn)
            if(exist.equals(0L) ){
            for (book in bookList) {
                var index = oldList.size
                db.addBook(book)
                oldList.add(index + 1,book)
            }
            }
        } catch (e: Exception) {
            val toast = Toast.makeText(applicationContext, e.message, duration)
            toast.show()
        }finally {
            adapter.submitList(oldList)
        }

    }
}


