package trianglepoint.warframe_viewer_android

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class firebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "firebaseMsgService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")
        if(remoteMessage.data.size > 0){
            Log.d(TAG, "msg payload: ${remoteMessage.data.get("link")}")
        }
        if(remoteMessage.notification != null){
            Log.d(TAG, "msg notification: ${remoteMessage.notification?.body}")
        }

        sendNotification(remoteMessage)
    }

    private fun sendNotification(remoteMessage: RemoteMessage){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(remoteMessage.data["link"]))

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, "Notification")
            .setSmallIcon(R.drawable.notification_ordis)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["body"])
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}