package com.soundmeter.application.utils

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.soundmeter.application.R

fun showSnackBarWithAction(view: View, context: Context, message: String, isSuccess: Boolean, actionText: String, action: () -> Unit) {
    @DrawableRes val background: Int = if (isSuccess) R.drawable.bg_round_10_green else R.drawable.bg_round_10_red
    val snackBar: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    val paramView = snackBar.view
    val params = paramView.layoutParams as FrameLayout.LayoutParams
    val snackBarText = paramView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    val snackBarAction = paramView.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
    snackBarText.apply {
        textSize = 12f
    }
    snackBarAction.setTextColor(ContextCompat.getColor(context, R.color.white))
    paramView.layoutParams = params
    paramView.background = ContextCompat.getDrawable(context, background)
    snackBar.apply {
        setAction(actionText) { action.invoke() }
        animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        show()
    }
}