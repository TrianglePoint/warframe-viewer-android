package trianglepoint.warframe_viewer_android

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_images.*

class ImagesActivity : AppCompatActivity(){
    private val TAG = "ImagesActivity_1"
    private val SELECT_IMAGE = 1
    private var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient : GoogleSignInClient? = null
    var storage : FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        storage = FirebaseStorage.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        sign_out_button.setOnClickListener{signOut()}

        testBtn.setOnClickListener {

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
    private fun signOut(){
        // firebase sign out.
        mAuth?.signOut()

        // google sign out.
        mGoogleSignInClient?.signOut()
        finish()
    }
}