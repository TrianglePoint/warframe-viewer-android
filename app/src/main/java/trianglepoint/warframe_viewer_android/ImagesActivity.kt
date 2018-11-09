package trianglepoint.warframe_viewer_android

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_images.*
import trianglepoint.modules.CustomAdapter
import java.io.File
import java.util.*
import java.util.jar.Manifest
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

    // For floating button.
    // unPress(normal), press(touch, long touch, ...)
    val stateList = arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed))

    // Button color.
    val color_saturation = Color.rgb(33, 127, 209)
    val color_saturation_gray = Color.rgb(120, 120, 120)
    val openColor = Color.rgb(233, 233, 233)
    val closeColor_touchMenu = Color.rgb(33, 33, 33)
    val closeColor_delete = Color.rgb(255, 68, 68)
    val closeColor_unable = Color.rgb(153, 153, 153)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        // Prevent screen off.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
        load_imagesName(fireCollection, -1)

        GlideApp.with(this)
            .load(storageRef?.child("public/nothing.jpg"))
            .into(noneImage)

        touchMenuButton.setOnClickListener {
            // Open touchMenu.
            if(touchMenuLayout.visibility == View.GONE) {
                touchMenuLayout.visibility = View.VISIBLE
                touchMenuButton.backgroundTintList = ColorStateList(stateList, intArrayOf(openColor, closeColor_touchMenu))
            }
            // Close touchMenu.
            else{
                touchMenuLayout.visibility = View.GONE
                touchMenuButton.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_touchMenu, openColor))

                deleteMenuLayout.visibility = View.GONE
                button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_delete, openColor))
            }
        }
        button_saturation.setOnClickListener {
            button_saturation.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_unable, openColor))
            button_saturation.isEnabled = false

            // Convert saturation.
            adapter?.wantGray = !(adapter?.wantGray as Boolean)

            load_imagesName(fireCollection, -1)
        }
        button_upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE)
        }
        button_download.setOnClickListener {
            // Position of want download image.
            val currentItem = imagePager.currentItem

            val arr_name = adapter?.getarr_name() as ArrayList<String>
            Log.d(TAG, "download position: $currentItem")

            val downloadRef = storageRef?.child("${arr_name[currentItem]}")

            var temp_name = arr_name[currentItem]
            var temp_j = temp_name.length - 1
            while(temp_name[temp_j] != '_'){
                temp_j--
            }
            var temp_i = temp_j
            while(temp_name[temp_i] != '/'){
                temp_i--
            }

            val download_path = "${Environment.getExternalStorageDirectory()}/DCIM/WarViewer"
            val downloadFimename = "/${temp_name.substring(temp_i + 1, temp_j)}.jpg"

            val folder = File(download_path)

            if(!(folder.exists())){
                // Create folder.
                folder.mkdir()
            }

            Log.d(TAG, "download_path : $download_path$downloadFimename")
            val localFile = File(download_path+downloadFimename)

            // Download image.
            downloadRef?.getFile(localFile)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "Success download the image: ${localFile.path}")
                        Snackbar.make(imagesLayout, "Downloaded image\n${localFile.path}", Snackbar.LENGTH_LONG).show()
                    }else{
                        Log.d(TAG, "Fail download the image")
                        Snackbar.make(imagesLayout, "Fail Download image", Snackbar.LENGTH_SHORT).show()
                    }
                }
        }
        button_delete.setOnClickListener {
            // Open deleteMenu.
            if(deleteMenuLayout.visibility == View.GONE) {
                deleteMenuLayout.visibility = View.VISIBLE
                button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(openColor, closeColor_delete))
            }
            // Close deleteMenu.
            else{
                deleteMenuLayout.visibility = View.GONE
                button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_delete, openColor))
            }
        }
        button_delete_no.setOnClickListener {
            // Close deleteMenu.
            deleteMenuLayout.visibility = View.GONE
            button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_delete, openColor))
        }
        button_delete_yes.setOnClickListener {
            touchMenuLayout.visibility = View.GONE
            touchMenuButton.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_unable, openColor))
            touchMenuButton.isEnabled = false

            deleteMenuLayout.visibility = View.GONE
            button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_delete, openColor))

            // Position of want delete image.
            val currentItem = imagePager.currentItem

            // Close deleteMenu.
            deleteMenuLayout.visibility = View.GONE
            button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_delete, openColor))

            val arr_name = adapter?.getarr_name() as ArrayList<String>
            Log.d(TAG, "delete position: $currentItem")

            val deleteRef = storageRef?.child("${arr_name[currentItem]}")
            // Delete image. can't recovery!
            deleteRef?.delete()
                ?.addOnCompleteListener {
                    touchMenuButton.backgroundTintList =
                            ColorStateList(stateList, intArrayOf(closeColor_touchMenu, openColor))
                    touchMenuButton.isEnabled = true
                    if (it.isSuccessful) {
                        Log.d(TAG, "Success delete the image")
                        Snackbar.make(imagesLayout, "Deleted image", Snackbar.LENGTH_SHORT).show()

                        fireCollection.whereEqualTo("path", arr_name[currentItem]).get()
                            .addOnSuccessListener {
                                it.forEach {
                                    it.reference.delete()
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Success delete on firestore")
                                            load_imagesName(fireCollection, currentItem)
                                        }
                                        .addOnFailureListener {
                                            Log.d(TAG, "Fail delete on firestore")
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "Fail get on firestore")
                            }
                    }else{
                        Log.d(TAG, "Fail delete the image")
                    }
                }
        }
    }

    fun load_imagesName(fireCollection: CollectionReference, deletePosition: Int){
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

                // For keep 'current' do viewing position.
                // If deletePosition is not -1, did deleted image.
                if(deletePosition != -1 && currentPostion > deletePosition) {
                    currentPostion -= 1
                }
                /*
                [ ] : currentPosition, variable value.
                ' ' : really current viewing position.
                { } : deletePosition, variable value.

                For example, currentPosition is 4. (0,1,2,3,['4'],5)
                deletePosition is 2. (0,1,{2},3,['4'],5)
                when complete delete, left 4 images.
                really current position is 3, but currentPosition is 4. (0,1,{ },2,'3',[4])
                So, subtract -1 on currentPosition. (0,1,{ },2,['3'],4)
                 */

                // If delete last position image.
                if(currentPostion >= array.size){
                    // If array.size - 1 is negative number, don't worry. currentItem is not become negative number.
                    imagePager.currentItem = array.size - 1
                }else {
                    imagePager.currentItem = currentPostion
                }

                if(array.size == 0){
                    button_saturation.visibility = View.GONE
                    button_download.visibility = View.GONE
                    button_delete.visibility = View.GONE
                    noneImage.visibility = View.VISIBLE
                }else{
                    button_saturation.visibility = View.VISIBLE
                    button_download.visibility = View.VISIBLE
                    button_delete.visibility = View.VISIBLE
                    noneImage.visibility = View.GONE
                }
                loadingImages.visibility = View.GONE
                if(!(button_saturation.isEnabled)){
                    if(adapter?.wantGray as Boolean){
                        button_saturation.backgroundTintList = ColorStateList(stateList, intArrayOf(color_saturation_gray, openColor))
                    }else{
                        button_saturation.backgroundTintList = ColorStateList(stateList, intArrayOf(color_saturation, openColor))
                    }
                    button_saturation.isEnabled = true
                }
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

                    var currentTime = System.currentTimeMillis()

                    // Set path of storage.
                    storageRef = storageRef?.child("images/${character}/${mAuth?.uid}/${file.lastPathSegment}_$currentTime")

                    docRef = FirebaseFirestore.getInstance().document("/images/${character}/${mAuth?.uid}/$currentTime")
                    image.put("name", "${file.lastPathSegment}_$currentTime")
                    image.put("path", "/images/${character}/${mAuth?.uid}/${file.lastPathSegment}_$currentTime")
                    image.put("date_upload", Date(currentTime))

                    touchMenuLayout.visibility = View.GONE
                    touchMenuButton.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_unable, openColor))
                    touchMenuButton.isEnabled = false

                    deleteMenuLayout.visibility = View.GONE
                    button_delete.backgroundTintList = ColorStateList(stateList, intArrayOf(closeColor_delete, openColor))

                    // Upload File to Firebase Storage.
                    val uploadTask = storageRef?.putFile(data.data)
                    uploadTask?.addOnCompleteListener{
                        touchMenuButton.backgroundTintList =
                                ColorStateList(stateList, intArrayOf(closeColor_touchMenu, openColor))
                        touchMenuButton.isEnabled = true
                        if(it.isSuccessful) {
                            Log.d(TAG, "Success upload the ${file.lastPathSegment}")
                            Snackbar.make(imagesLayout, "Success upload the image", Snackbar.LENGTH_SHORT).show()

                            docRef?.set(image)
                                ?.addOnSuccessListener {
                                    Log.d(TAG, "Success add to db the ${file.lastPathSegment}")
                                    val fireCollection =
                                        FirebaseFirestore.getInstance().collection("images/${character}/${mAuth?.uid}")
                                    load_imagesName(fireCollection, -1)
                                }?.addOnFailureListener {
                                    Log.d(TAG, "Fail add to db the ${file.lastPathSegment}")
                                }
                        }else{
                            Log.d(TAG, "Fail upload the ${file.lastPathSegment}")
                            Snackbar.make(imagesLayout, "Fail upload the image", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}