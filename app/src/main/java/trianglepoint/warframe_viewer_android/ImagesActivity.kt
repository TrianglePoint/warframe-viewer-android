package trianglepoint.warframe_viewer_android

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_images.*
import trianglepoint.modules.CustomAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ImagesActivity : AppCompatActivity(){
    private val TAG = "ImagesActivity_1"
    private val SELECT_IMAGE = 1
    private var mAuth: FirebaseAuth? = null
    private var character = ""
    var storage : FirebaseStorage? = null
    var docRef : DocumentReference? = null
    var adapter : CustomAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        // Who are character?
        if(intent.hasExtra("character")){
            character = intent.getStringExtra("character")
        }

        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // Set reference of FireStore.
        var storageRef = storage?.reference
        // Set path about images of user.
        val fireCollection = FirebaseFirestore.getInstance().collection("images/${character}/${mAuth?.uid}")
        adapter = CustomAdapter(layoutInflater)
        adapter?.storageRef = storageRef
        load_imagesName(fireCollection)

        GlideApp.with(this)
            .load(storageRef?.child("public/nothing.jpg"))
            .into(noneImage)

        touchMenuButton.setOnClickListener {
            // unPress(normal), press(touch, long touch, ...)
            val stateList = arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed))
            val openColor = Color.rgb(233, 233, 233)
            val closeColor = Color.rgb(33, 33, 33)
            val closeColor_delete = Color.rgb(255, 68, 68)

            // Open touchMenu.
            if(touchMenuLayout.visibility == View.GONE) {
                touchMenuLayout.visibility = View.VISIBLE
                touchMenuButton.backgroundTintList = ColorStateList(stateList, intArrayOf(openColor, closeColor))
            }
            // Close touchMenu.
            else{
                touchMenuLayout.visibility = View.GONE
                touchMenuButton.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor, openColor))

                deleteMenuLayout.visibility = View.GONE
                button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_delete, openColor))
            }
        }
        button_upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE)
        }
        button_delete.setOnClickListener {
            // unPress(normal), press(touch, long touch, ...)
            val stateList = arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed))
            val openColor = Color.rgb(233, 233, 233)
            val closeColor = Color.rgb(255, 68, 68)

            // Open deleteMenu.
            if(deleteMenuLayout.visibility == View.GONE) {
                deleteMenuLayout.visibility = View.VISIBLE
                button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(openColor, closeColor))
            }
            // Close deleteMenu.
            else{
                deleteMenuLayout.visibility = View.GONE
                button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor, openColor))
            }
        }
        button_delete_no.setOnClickListener {
            // unPress(normal), press(touch, long touch, ...)
            val stateList = arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed))
            val openColor = Color.rgb(233, 233, 233)
            val closeColor = Color.rgb(255, 68, 68)
            
            // Close deleteMenu.
            deleteMenuLayout.visibility = View.GONE
            button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor, openColor))
        }
    }

    fun load_imagesName(fireCollection: CollectionReference){
        var array: ArrayList<String> = ArrayList()
        loadingImages.visibility = View.VISIBLE
        fireCollection.get()
            .addOnSuccessListener {
                for (i in it) {
                    array.add(i.getString("path") as String)
                }
                adapter?.setarr_name(array)
                var currentPostion = imagePager.currentItem
                imagePager.adapter = adapter
                imagePager.currentItem = currentPostion
                if(array.size == 0){
                    noneImage.visibility = View.VISIBLE
                }else{
                    noneImage.visibility = View.GONE
                }
                loadingImages.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.w(TAG, "Fail the fireCollection.get()")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SELECT_IMAGE -> {
                if(data != null){
                    Log.d(TAG,"uri: ${data.data}")
                    var storageRef = storage?.reference
                    val file = data.data
                    var image = HashMap<String, Any>()

                    // Set path of storage.
                    storageRef = storageRef?.child("images/${character}/${mAuth?.uid}/${file.lastPathSegment}")

                    var currentTime = System.currentTimeMillis()
                    docRef = FirebaseFirestore.getInstance().document("/images/${character}/${mAuth?.uid}/$currentTime")
                    image.put("name", file.lastPathSegment)
                    image.put("path", "/images/${character}/${mAuth?.uid}/${file.lastPathSegment}")
                    image.put("date_upload", Date(currentTime))

                    // Upload File to Firebase Storage.
                    val uploadTask = storageRef?.putFile(data.data)
                    uploadTask?.addOnSuccessListener {
                        Log.d(TAG, "Success upload the ${file.lastPathSegment}")
                        Snackbar.make(imagesLayout, "Success upload the ${file.lastPathSegment}", Snackbar.LENGTH_SHORT).show()
                        docRef?.set(image)
                            ?.addOnSuccessListener {
                                Log.d(TAG, "Success add to db the ${file.lastPathSegment}")
                                val fireCollection = FirebaseFirestore.getInstance().collection("images/${character}/${mAuth?.uid}")
                                load_imagesName(fireCollection)
                            }?.addOnFailureListener{
                                    Log.d(TAG, "Fail add to db the ${file.lastPathSegment}")
                            }
                    }
                        ?.addOnFailureListener {
                            Log.d(TAG, "Fail upload the ${file.lastPathSegment}")
                            Snackbar.make(imagesLayout, "Fail upload the ${file.lastPathSegment}", Snackbar.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}