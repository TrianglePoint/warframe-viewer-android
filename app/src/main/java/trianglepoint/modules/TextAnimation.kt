package trianglepoint.modules

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.util.*

// Extend the AppCompatActivity() because for use the UI update thread.
class TextAnimation(textView: TextView, textBackground: ImageView) : AppCompatActivity(), Runnable{
    private val TAG = "TextAnimation_1"

    var textView : TextView? = null
    var textBackground : ImageView? = null

    val ordisText_Welcome = "\n오셨군요, 오퍼레이터!"
    val ordisText_list = arrayOf(//대화 목록.
        "오퍼레이터, 바깥 경치 보기 좋~아요?",
        "모든 사진이 제자리에 있답니다.\n\n아뇨, 슬쩍 한거 하나도 없어요.",
        "모아두신 사진이 모두 제자리에 있나요?\n\n아뇨, 제게 질투란 기능은 없어요.",
        "모든 사진이 정확히 파악되었답니다, 오퍼레이터. \n\n슬쩍 한거 하나도 없어요.",
        "오퍼레이터의 사진 컬렉션은 인상적이에요.\n\n하지만...\n이외에 더 찍을만한 사진이 있지 않을까요?",
        "오디스는 행ㅂ..\n\n'*화나..!*' \n\n흐음, 아무래도 점검이 필요할 거 같네요.",
        "오디스는 별을 세보고 있었답니다.\n\n모두 제자리에 있네요.",
        "오퍼레이터, 피비린내 나는 전투를\n상상하고 계신가요? \n\n'*나도.*'"
    )
    var text = ""

    var should_welcome = false
    var wantRun = true

    init {
        this.textView = textView
        this.textBackground = textBackground
    }
    override fun run() {
        while(true) {

            // Set the what the say.
            if(should_welcome){
                text = ordisText_Welcome
            }else {
                // CoolTime.
                try {
                    val coolTime_min = 30 // min sec~
                    val coolTime_max = 60 // ~ max sec.
                    val coolTIme = (1000 * coolTime_min) + ((Random().nextInt(coolTime_max - coolTime_min + 1)) * 1000).toLong()

                    Log.d(TAG, "coolTIme: $coolTIme")
                    Thread.sleep(coolTIme)
                }catch (e: Exception){
                    e.printStackTrace()
                    Log.d(TAG, "e: ${e.printStackTrace()}")
                }

                text = ordisText_list[Random().nextInt(ordisText_list.size)]
            }
            runOnUiThread {
                textBackground?.visibility = View.VISIBLE
            }

            // Start the saying.
            var i = 1 // Position of one value in Characters. print the subString. Used for control the time of the sleep().
            while (wantRun && i < text.length) {
                runOnUiThread {
                    textView?.text = text.substring(0, i)
                }
                try {
                    if (text[i - 1] == ',') {
                        Thread.sleep(200)
                    } else if (text[i - 1] == '.') {
                        if (text[i] != '.') {
                            if (text[i] != '!' && text[i] != '?' && text[i] != '*') {
                                Thread.sleep(400)
                            }
                        } else {
                            if (text[i + 1] != '!' && text[i + 1] != '?') {
                                Thread.sleep(200)
                            }
                        }
                    } else if (text[i - 1] == '?' || text[i - 1] == '!') {
                        if (text[i] != '*') {
                            Thread.sleep(400)
                        }
                    }
                    Thread.sleep(50)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                i++
            }
            if(!(wantRun)){
                runOnUiThread {
                    textView?.text = ""
                    textBackground?.visibility = View.GONE
                }
                break
            }
            runOnUiThread {
                textView?.text = text
            }
            try {
                if(should_welcome){
                    Thread.sleep(1000)
                    should_welcome = false
                }
                Thread.sleep(1500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            runOnUiThread {
                textView?.text = ""
                textBackground?.visibility = View.GONE
            }
        }
    }
    fun say_welcome(){
        should_welcome = true
    }
    fun terminate(){
        wantRun = false
        should_welcome = false
    }
}