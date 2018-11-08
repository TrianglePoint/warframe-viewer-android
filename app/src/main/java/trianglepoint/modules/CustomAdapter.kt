package trianglepoint.modules

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.storage.StorageReference
import trianglepoint.warframe_viewer_android.GlideApp
import trianglepoint.warframe_viewer_android.R

public class CustomAdapter(inflater: LayoutInflater) : PagerAdapter(){
    private final val TAG = "CustomAdapter_1"
    var inflater: LayoutInflater? = null
    var storageRef : StorageReference? = null // For load image from fireStorage.
    var wantGray = false // Gray-Scale image?
    var arr_name : ArrayList<String> = ArrayList()
    var countVar = 0 // image count.

    init {
        this.inflater = inflater
    }
    override fun getCount(): Int {
        return countVar
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {// Init page when scrolling

        // Template of ImageView when shows the image.
        val view = inflater?.inflate(R.layout.viewpager_childlayout, null)
        val img = view?.findViewById(R.id.img_viewpager_childimage) as ImageView// Use ImageView of in view.

        GlideApp.with(inflater?.context as Context)
            .load(storageRef?.child("${arr_name[position]}"))
            .into(img)
        Log.d(TAG, "position:$position")

        if(wantGray){
            val matrix = ColorMatrix()
            val zero_float0 = 0
            matrix.setSaturation(zero_float0.toFloat()) // 0 is grayScale.
            val cf = ColorMatrixColorFilter(matrix)
            img.colorFilter = cf // For apply matrix to ImageView, Should ColorMatrixColorFilter.
        }
        container.addView(view) // Shows the Pager.
        return view
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // Delete the disappeared image on screen when did scrolling.
        // Keep 3 images that current image and both side image.
        container.removeView(`object` as View)
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    fun getarr_name(): ArrayList<String>{
        return this.arr_name
    }
    fun setarr_name(arr: ArrayList<String>){
        this.arr_name = arr
        countVar = arr.size
        Log.d(TAG, "count is $countVar")
    }
}