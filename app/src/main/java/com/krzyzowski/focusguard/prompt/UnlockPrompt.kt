package com.krzyzowski.focusguard.prompt

import android.content.Context
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.krzyzowski.focusguard.R
import com.krzyzowski.focusguard.data.RecordedUnlock
import com.krzyzowski.focusguard.service.FocusNotifications
import com.krzyzowski.focusguard.unlock.UnlockTimeText
import com.krzyzowski.focusguard.words.UnlockWord
import java.time.Instant

class UnlockPrompt(
    private val context: Context,
    private val unlockTimeText: UnlockTimeText,
    private val notifications: FocusNotifications,
) {
    private val windowManager = context.getSystemService(WindowManager::class.java)
    private var card: View? = null

    fun show(unlock: RecordedUnlock, word: UnlockWord) {
        val previousUnlockLine = unlock.previousUnlock
            ?.let { context.getString(R.string.prompt_last_unlock, unlockTimeText.describe(it, Instant.now())) }
            ?: context.getString(R.string.prompt_no_previous_unlock)
        if (!Settings.canDrawOverlays(context)) {
            notifications.unlockReminder(previousUnlockLine)
            return
        }
        dismiss()
        val view = LayoutInflater.from(ContextThemeWrapper(context, R.style.Theme_FocusGuard_Prompt))
            .inflate(R.layout.unlock_prompt, null)
        view.findViewById<TextView>(R.id.unlock_count).text =
            context.getString(R.string.prompt_unlock_count, unlock.unlocksToday)
        view.findViewById<TextView>(R.id.previous_unlock).text = previousUnlockLine
        view.findViewById<TextView>(R.id.word_english).text = word.english
        view.findViewById<TextView>(R.id.word_polish).text = word.polish
        val ok = view.findViewById<Button>(R.id.ok)
        ok.setOnClickListener { dismiss() }
        windowManager.addView(
            view,
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT,
            ),
        )
        card = view
        holdOkButton(ok)
    }

    fun dismiss() {
        card?.let(windowManager::removeView)
        card = null
    }

    private fun holdOkButton(ok: Button) {
        ok.isEnabled = false
        for (secondsLeft in OK_DELAY_SECONDS downTo 1) {
            ok.postDelayed(
                { ok.text = context.getString(R.string.prompt_ok_wait, secondsLeft) },
                (OK_DELAY_SECONDS - secondsLeft) * 1_000L,
            )
        }
        ok.postDelayed(
            {
                ok.isEnabled = true
                ok.setText(R.string.prompt_ok)
            },
            OK_DELAY_SECONDS * 1_000L,
        )
    }

    private companion object {
        const val OK_DELAY_SECONDS = 2
    }
}
