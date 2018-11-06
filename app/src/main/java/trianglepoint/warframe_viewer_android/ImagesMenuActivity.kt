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
import kotlinx.android.synthetic.main.activity_images_menu.*

class ImagesMenuActivity : AppCompatActivity(){
    private val TAG = "ImagesMenuActivity_1"
    private var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_menu)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        sign_out_button.setOnClickListener{signOut()}

        testBtn.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            startActivity(intent)
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