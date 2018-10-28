package trianglepoint.warframe_viewer_android

import android.os.AsyncTask
import android.util.Log
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class LoadTask() : AsyncTask<String, Void, String>(){
    private val TAG = "LoadTask"

    override fun doInBackground(vararg url: String?): String {
        val url = URL(url[0])
        val httpClient = url.openConnection() as HttpURLConnection
        var result = ""
        try {
            //Configure
            httpClient.requestMethod = "GET"
            httpClient.setRequestProperty("Cache-Control", "no-cache")
            httpClient.setRequestProperty("Content-Type", "application/json")
            httpClient.setRequestProperty("Accept", "text/html")
            httpClient.doInput = true

            Log.d(TAG, "Try connect to Server...")
            httpClient.connect()

            //httpClient.inputStream is mean "Create Stream".
            result = readStream(inputStream = httpClient.inputStream)

            Log.d(TAG,"Try outputStream.close() .")
        }catch (e: Exception){
            e.printStackTrace()
            Log.d(TAG, "EXCEPTION : " + e.printStackTrace().toString())
        }finally {
            httpClient.disconnect()
            Log.d(TAG, "Disconnected on Server.")
        }
        return result
    }
    fun readStream(inputStream: InputStream): String{
        val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuffer = StringBuffer()
        var responeResult : String? = ""

        while(true) {
            responeResult = bufferedReader.readLine()
            if(responeResult != null){
                stringBuffer.append(responeResult)
            }else{
                break
            }
        }
        return stringBuffer.toString()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Log.d(TAG,"result: " + result)
    }
}