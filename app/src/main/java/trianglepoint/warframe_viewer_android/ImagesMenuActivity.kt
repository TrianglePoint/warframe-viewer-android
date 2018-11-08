package trianglepoint.warframe_viewer_android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
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

        // The Button, of the all characters.
        btn_ash.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "ash")
            startActivity(intent)
        }
        btn_atlas.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "atlas")
            startActivity(intent)
        }
        btn_ember.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "ember")
            startActivity(intent)
        }
        btn_excalibur.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "excalibur")
            startActivity(intent)
        }
        btn_frost.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "frost")
            startActivity(intent)
        }
        btn_ivara.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "ivara")
            startActivity(intent)
        }
        btn_limbo.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "limbo")
            startActivity(intent)
        }
        btn_loki.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "loki")
            startActivity(intent)
        }
        btn_mag.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "mag")
            startActivity(intent)
        }
        btn_mesa.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "mesa")
            startActivity(intent)
        }
        btn_mirage.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "mirage")
            startActivity(intent)
        }
        btn_nidus.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "nidus")
            startActivity(intent)
        }
        btn_nova.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "nova")
            startActivity(intent)
        }
        btn_oberon.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "oberon")
            startActivity(intent)
        }
        btn_octavia.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "octavia")
            startActivity(intent)
        }
        btn_rhino.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "rhino")
            startActivity(intent)
        }
        btn_vauban.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "vauban")
            startActivity(intent)
        }
        btn_volt.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            intent.putExtra("character", "volt")
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