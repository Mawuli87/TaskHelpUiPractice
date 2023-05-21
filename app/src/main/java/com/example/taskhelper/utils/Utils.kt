package com.example.taskhelper.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.taskhelper.R
import com.example.taskhelper.ui.screen.SearchScreen
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoast.StyleableToast
import java.util.*


fun <A : Activity> Activity.startNewActivity( activity: Class<A> , intentID:Int) {
    Intent(this, activity).also {
        it.putExtra("ArrowKey" , intentID)
        startActivity(it)
    }
}

fun  Activity.startSearchActivity(SearchID:Int){
    Intent(this , SearchScreen::class.java).also {
        it.putExtra("SearchID" , SearchID)
        startActivity(it)
        this.finish()
    }
}

fun Activity.getGreetingMessage(){
    val calender = Calendar.getInstance()
    val handEmo = "\uD83D\uDC4B"
    when (calender.get(Calendar.HOUR_OF_DAY)) {

        in 0..11 -> setToastMessage("Good Morning  $handEmo", Color.parseColor("#1AC231"))
        in 12..15 -> setToastMessage("Good Afternoon $handEmo", Color.parseColor("#1AC231"))
        in 16..20 -> setToastMessage("Good Evening $handEmo", Color.parseColor("#1AC231"))
        in 21..23 -> setToastMessage("Good Night $handEmo", Color.parseColor("#1AC231"))
        else -> Toast.makeText(this , "Hello" , Toast.LENGTH_LONG).show()
    }

}

fun Activity.setToastMessage(message:String , color:Int){
    StyleableToast.Builder(this)
        .text(message)
        .textColor(Color.WHITE)
        .textBold()
        .gravity(Gravity.TOP)
        .cornerRadius(15)
        .backgroundColor(color)
        .textBold()
        .length(2000)
        .show()
}

@SuppressLint("ResourceAsColor", "InflateParams")
fun showingSnackBar(view: View, message: String, color: String){

    val snackBar = Snackbar.make(view, message , Snackbar.LENGTH_SHORT)
    val layoutParams = CoordinatorLayout.LayoutParams(snackBar.view.layoutParams)
    snackBar.view.setBackgroundColor(Color.parseColor(color))
    val textView = snackBar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    val textAction = snackBar.view.findViewById(com.google.android.material.R.id.snackbar_action) as TextView
    textAction.typeface = Typeface.DEFAULT_BOLD
    textAction.setTextColor(R.color.colorDark)
    textAction.textSize = 17f
    textView.text = message
    textView.setTextColor(Color.WHITE)
    textView.textSize = 18f
    layoutParams.gravity = Gravity.TOP
    layoutParams.setMargins(5 , 0 , 5 , 0)
    layoutParams.width = CoordinatorLayout.LayoutParams.MATCH_PARENT
    layoutParams.height = 250
    snackBar.duration = 3000
    snackBar.view.layoutParams = layoutParams
    snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
    snackBar.setAction("Undo"){
        snackBar.dismiss()
    }

    snackBar.show()
}

inline val Activity.rootView: View get() = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
