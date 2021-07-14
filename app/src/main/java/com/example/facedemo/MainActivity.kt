package com.example.facedemo

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.example.facedemo.Model.ApiRequest
import com.example.facedemo.Model.response
import com.example.facedemo.Networking.ApiClient
import com.example.facedemo.Networking.ApiService
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var uploadBt: Button
    lateinit var image: ImageView
    lateinit var bitmap:Bitmap
    lateinit var uri:Uri
    lateinit var b1:Button
    lateinit var pgbar:ProgressBar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadBt = findViewById(R.id.upload)
        image = findViewById(R.id.imageView)
        pgbar = findViewById(R.id.progressBar)
        pgbar.visibility = View.GONE
        b1 = findViewById(R.id.button)
        uploadBt.setOnClickListener {
            onUploadClick()
        }
/*        newBt.setOnClickListener {
            val photoFile = File.createTempFile(
                "IMG_",
                ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            uri = FileProvider.getUriForFile(
                this,
                "com.codepath.fileprovider",
                photoFile
            )
            takePicture.launch(uri)
        }
 */
        b1.setOnClickListener {
            pgbar.visibility = View.VISIBLE
            var finalString = getEncodedString()
            Log.e("size", finalString.length.toString())
            CoroutineScope(IO).launch {
                apiCall(finalString)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getEncodedString():String{
        val baos = ByteArrayOutputStream()
        bitmap = image.drawable.toBitmap()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        Log.e("byte_size", imageBytes.size.toString())
        val imageString: String = Base64.getEncoder().encodeToString(imageBytes)
        return imageString
    }

/*    val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { bitmap->
        bitmap?.let {
            image.setImageURI(uri)
        }
    }

 */

    fun apiCall(str: String){
        var request = ApiRequest()
        request.string = str
        var retro = ApiClient.getRetro().create(ApiService::class.java)
            val call: Call<response> = retro.getResults(request)
            call.enqueue(object : Callback<response> {
                override fun onResponse(call: Call<response>, response: Response<response>) {
                    if (response.isSuccessful && response.code() == 200) {
                        pgbar.visibility = View.GONE
                        Toast.makeText(
                            applicationContext,
                            response.body()!!.string,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        pgbar.visibility = View.GONE
                        Toast.makeText(
                            applicationContext,
                            response.code().toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<response>, t: Throwable) {
                    pgbar.visibility = View.GONE
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }
    fun onUploadClick(){
//        var intent:Intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(this);
    }

 /*       private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                if(intent.data != null) {
                    var uri2: Uri = intent.data!!

                    if (result.resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        var res:CropImage.ActivityResult = CropImage.getActivityResult(intent?.data);
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri2)
                    image.setImageBitmap(bitmap)
                }
            }
        }
    }
 */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                val resultUri = result.uri
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, resultUri)
                image.setImageBitmap(bitmap)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}