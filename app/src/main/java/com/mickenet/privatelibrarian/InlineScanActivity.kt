package com.mickenet.privatelibrarian

/**
 * Class to handle scanning actiivty.
 */

import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.view.View
import android.widget.TextView
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

/**
 * Construct.
 */
class InlineScanActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val adapter = BookAdapter()
    var scanState: Boolean = false
    var torchState: Boolean = false
    var db = DatabaseHandler(this)

    /**
     * Function fired on create.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inline_scan)
        getVersionInfo()
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
        val recyclerView: RecyclerView = simple_recyclerview
        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        deleteBook(adapter.currentList.get(position))
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                      adapter.notifyDataSetChanged()

                    }
                })
        )
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

    /**
     *  Function to hndle a pause in execution.
     */
    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    /**
     * Fucntion fired when resuming.
     */
    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    /**
     * On destroing object.
     */
    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    /**
     * Fucntion to delete a book.
     */
    public fun deleteBook(isbn: LocalBook) {
        val duration = Toast.LENGTH_SHORT
        try {
            //val indx = adapter.currentList.indexOf(isbn)
            //adapter.currentList.removeAt(indx)
            db.deleteBook(isbn)
            var booklist = db.allBooks
            adapter.submitList(booklist)
        } catch (e: Exception) {
            val toast = Toast.makeText(applicationContext, e.message, duration)
            toast.show()
        }
}

   /**
     * Fucntion to fetch a book.
     */
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
            }
            }
        } catch (e: Exception) {
            val toast = Toast.makeText(applicationContext, e.message, duration)
            toast.show()
        }finally {
            saveData()
        }

    }
    private fun saveData() {
        var booklist = db.allBooks
        adapter.submitList(booklist)
    }

    //get the current version number and name
    private fun getVersionInfo() {
        var versionName = ""
        var versionCode = -1
        try {
            val packageInfo =
                packageManager.getPackageInfo(packageName, 0)
            versionName = packageInfo.versionName
            versionCode = packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val textViewVersionInfo = findViewById<TextView>(R.id.textview_version_info)
        textViewVersionInfo.text = String.format(
            "Version name = %s Version code = %d",
            versionName,
            versionCode
        )
    }
    private fun syncData(){
        //todo: Skapa anrop till soap services för att spara och ta bord data från bibliotek.
    }
}


