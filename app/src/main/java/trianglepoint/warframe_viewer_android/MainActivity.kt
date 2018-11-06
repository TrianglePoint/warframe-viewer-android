package trianglepoint.warframe_viewer_android

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    private val TAG = "MainActivity_1"
    private val RC_SIGN_IN: Int = 9001
    private var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        btnToNews.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }
        btnToImages.setOnClickListener {
            // Need login. so if not login, should login.
            if(mAuth?.currentUser !is FirebaseUser) {
                signIn()
            }else {
                // if already login, go to images.
                val intent = Intent(this, ImagesMenuActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data) as Task<GoogleSignInAccount>
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if(account is GoogleSignInAccount) {
                firebaseAuthWithGoogle(account)
                Log.d(TAG, "LOGIN!")
            }
        }catch (e: ApiException){
            Log.w(TAG, "signInResult: failed code= ${e.stackTrace}")
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount){
        Log.d(TAG, "firebaseAuthwithGoogle: ${acct.id}")

        val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.idToken, null)

        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d(TAG, "signInWithCredential: success")

                    // did success login and now, go to images.
                    val intent = Intent(this, ImagesMenuActivity::class.java)
                    startActivity(intent)
                }else{
                    Log.w(TAG, "signInWithCredential: failure", it.exception)
                    Snackbar.make(mainLayout, "Authentication Failed", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(){
        Log.d(TAG, "function signIn")
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
}