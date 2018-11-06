package trianglepoint.warframe_viewer_android

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_images.*

class ImagesActivity : AppCompatActivity(){
    private val TAG = "ImagesActivity_1"
    private val SELECT_IMAGE = 1
    private var mAuth: FirebaseAuth? = null
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // TEST.
        var storageRef = storage?.reference


        GlideApp.with(this)
            .load(storageRef?.child("images/ember/${mAuth?.uid}/553167208"))
            .into(imageView_01)


        button_upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SELECT_IMAGE -> {
                if(data != null){
                    Log.d(TAG,"uri: ${data.data}")
                    var storageRef = storage?.reference
                    val character = "ember"
                    val file = data.data

                    storageRef = storageRef?.child("images/${character}/${mAuth?.uid}/${file.lastPathSegment}")

                    Log.d(TAG, "tag ${file.path}")

                    // Upload File to Firebase Storage.
                    val uploadTask = storageRef?.putFile(data.data)
                    uploadTask?.addOnSuccessListener {
                        Log.d(TAG, "Success upload the ${file.lastPathSegment}")
                        Snackbar.make(imagesLayout, "Success upload the ${file.lastPathSegment}", Snackbar.LENGTH_SHORT).show()
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