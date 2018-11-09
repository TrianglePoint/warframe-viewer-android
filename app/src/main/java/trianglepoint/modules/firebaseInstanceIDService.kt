package trianglepoint.warframe_viewer_android

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging

class firebaseInstanceIDService : FirebaseInstanceIdService() {
    override public fun onTokenRefresh(){
        val refreshedToken = FirebaseInstanceId.getInstance().getToken()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().subscribeToTopic("news")

        Log.d("token","Refresed token: " + refreshedToken)

        sendRegistrationToServer(refreshedToken)
    }
    private fun sendRegistrationToServer(token: String?){
    }
}