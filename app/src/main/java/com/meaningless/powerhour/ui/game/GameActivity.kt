package com.meaningless.powerhour.ui.game

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.meaningless.powerhour.R
import com.meaningless.powerhour.data.database.DataManager
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity() {

    // region Fields
    private lateinit var viewModel: GameActivityViewModel
    // endregion

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        initViews()
        initViewModel()
    }
    // endregion

    // region Setup Functions
    private fun initViews() {
        startButton.setOnClickListener(::onStartTapped)
        playlistButton.setOnClickListener(::onPlaylistTapped)
        uriEditTextView.setOnEditorActionListener(::onEditorAction)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this)[GameActivityViewModel::class.java]
        viewModel.init(application)
        viewModel.roundLiveData.observe(this, onRoundChanged)
    }
    // endregion

    // region Control Methods
    private fun startTimer() {
        // TODO: Correct duration
        val animation = AnimationUtils.loadAnimation(this,
            R.anim.rotation_clock
        )
        clockImageView.startAnimation(animation)
    }

    private fun resetClock() {
        clockImageView.clearAnimation()
        clockImageView.rotation = 0f
    }
    // endregion

    // region Listeners
    private val onRoundChanged = Observer<Int> {
        if (it == 0) {
            resetClock()
            roundTextView.text = getString(R.string.round_placeholder)
        } else {
            startTimer()
            roundTextView.text = getString(R.string.round_number, it)
        }
    }

    private fun onStartTapped(view: View) {
        val isGameStarted = viewModel.toggleGameStart()
        val backgroundResource =
            if (isGameStarted) R.drawable.ic_pause_black_24dp else R.drawable.ic_play_arrow_black_24dp
        startButton.setImageResource(backgroundResource)
    }

    private fun onPlaylistTapped(view: View) {
        uriEditTextView.visibility = View.VISIBLE
        uriEditTextView.isFocusableInTouchMode = true
        uriEditTextView.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(uriEditTextView, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val uri = uriEditTextView.text?.toString() ?: ""
            DataManager.setPlayListUri(this, uri)

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN
            )

            uriEditTextView.visibility = View.GONE
            uriEditTextView.text = null
            return true
        }
        return false
    }
    // endregion
}
