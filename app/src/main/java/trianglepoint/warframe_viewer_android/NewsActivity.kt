package trianglepoint.warframe_viewer_android

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_news.*
import org.json.JSONArray
import org.json.JSONObject

class NewsActivity : AppCompatActivity() {
    private val TAG = "NewsActivity_1"
    val url = "https://warframe-viewer.herokuapp.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val firebaseInstanceIDService = firebaseInstanceIDService()
        firebaseInstanceIDService.onTokenRefresh()

        loadScreen(lLayout)
    }

    fun loadScreen(lLayout: LinearLayout){

        // Load Json data as string.
        val result = LoadTask().execute("$url/json").get()

        // and convert string to json.
        val jsonObject = JSONObject(result)

        lLayout.removeAllViews()

        // set the design of Header.
        val setTextSize = 26F
        val setMargin = 40

        var textView = TextView(this)
        lLayout.addView(textView)

        var layoutParams = (textView.layoutParams as LinearLayout.LayoutParams).apply {
            bottomMargin = setMargin
        }
        textView.layoutParams = layoutParams
        textView.text = "이벤트"
        textView.textSize = setTextSize
        textView.typeface = (Typeface.DEFAULT_BOLD)


        // and take out the jsonArray.
        var jsonArray = jsonObject.get("events") as JSONArray

        for(i in 0..(jsonArray.length()-1)) {
            textView = TextView(this)
            val data = arrayOf(jsonArray.getJSONObject(i).getString("subject"),
                jsonArray.getJSONObject(i).getString("link"))
            lLayout.addView(textView)

            textView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(data[1]))
                startActivity(intent)
            }

            layoutParams = (textView.layoutParams as LinearLayout.LayoutParams).apply {
                bottomMargin = setMargin / 2
            }
            textView.layoutParams = layoutParams
            textView.text = (data[0])
            textView.textSize = setTextSize / 2
        }

        textView = TextView(this)
        lLayout.addView(textView)

        layoutParams = (textView.layoutParams as LinearLayout.LayoutParams).apply {
            topMargin = setMargin
            bottomMargin = setMargin
        }
        textView.layoutParams = layoutParams
        textView.text = "업데이트"
        textView.textSize = setTextSize
        textView.typeface = (Typeface.DEFAULT_BOLD)


        jsonArray = jsonObject.get("update") as JSONArray

        for(i in 0..(jsonArray.length()-1)) {
            textView = TextView(this)
            val data = arrayOf(jsonArray.getJSONObject(i).getString("subject"),
                jsonArray.getJSONObject(i).getString("link"))
            lLayout.addView(textView)

            textView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(data[1]))
                startActivity(intent)
            }

            layoutParams = (textView.layoutParams as LinearLayout.LayoutParams).apply {
                bottomMargin = setMargin / 2
            }
            textView.layoutParams = layoutParams
            textView.text = (data[0])
            textView.textSize = setTextSize / 2
        }
    }
}