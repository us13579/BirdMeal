package com.ssafy.birdmeal.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.math.BigDecimal
import java.text.DecimalFormat

// 다이얼로그 사이즈 조절
fun Context.dialogResize(dialog: Dialog, width: Float, height: Float){
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    if(Build.VERSION.SDK_INT < 30){
        val display = windowManager.defaultDisplay
        val size = Point()

        display.getSize(size)

        val window = dialog.window

        val x = (size.x * width).toInt()
        val y = (size.y * height).toInt()

        window?.setLayout(x, y)
    } else {
        val rect = windowManager.currentWindowMetrics.bounds

        val window = dialog.window
        val x = (rect.width() * width).toInt()
        val y = (rect.height() * height).toInt()

        window?.setLayout(x, y)
    }
}

// 서버에서 이미지 받아오는 포맷
fun ImageView.imageFormatter(imageSeq: Int){
    Glide.with(this.context).load("${BASE_URL}images/${imageSeq}")
        //.placeholder(R.drawable.img)
        .into(this)
}

// 지갑 path 가져오기
fun Context.getWalletPath() = run { "$filesDir/wallet" }

// 숫자 천단위 표시
object DecimalConverter {
    fun BigDecimal.priceConvert(): String{
        val myFormat = DecimalFormat("###,###")
        return myFormat.format(this)
    }
}