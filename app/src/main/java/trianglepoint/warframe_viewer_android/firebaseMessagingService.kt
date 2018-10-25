package trianglepoint.warframe_viewer_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
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
            Log.d(TAG, "msg payload: ${remoteMessage.data}")
        }
        if(remoteMessage.notification != null){
            Log.d(TAG, "msg notification: ${remoteMessage.notification?.body}")
        }
    }

    private fun senNotification(messageBody: String){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", messageBody)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, "Notification")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Push Notification FCM")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}