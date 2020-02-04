package com.mickenet.privateLibrarian


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.mickenet.privateLibrarian.Books.Book
import com.mickenet.privateLibrarian.Books.BookAdapter
import com.mickenet.privateLibrarian.ISBN.BookClient
import kotlinx.android.synthetic.main.activity_inline_scan.*
import okhttp3.Headers
import okhttp3.internal.http2.Header
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject



class InlineScanActivity : AppCompatActivity() {
    lateinit var captureManager: CaptureManager
    var scanState: Boolean = false
    var torchState: Boolean = false
    val lvBooks: ListView? = null
    var bookAdapter: BookAdapter? = null
    val aBooks: ArrayList<Book> = ArrayList<Book>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inline_scan)

        captureManager = CaptureManager(this, barcodeView)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        bookAdapter = BookAdapter(this, aBooks)
        lvBooks?.setAdapter(bookAdapter)
        btnScan.setOnClickListener {
            txtResult.text = "scaning..."
            barcodeView.decodeSingle(object: BarcodeCallback{
                override fun barcodeResult(result: BarcodeResult?) {
                    result?.let {
                        txtResult.text = it.text
                     //  fetchBooks(it.text)
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
    private fun fetchBooks(isbn:String)
    var client: BookClient()
    client.


    }

}
