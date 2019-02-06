package com.meaningless.powerhour.ui.game

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.meaningless.powerhour.utils.GlideApp
import com.meaningless.powerhour.R
import com.meaningless.powerhour.data.database.DataManager
import com.meaningless.powerhour.utils.DataUtils
import com.meaningless.powerhour.data.music.common.models.Playlist
import com.meaningless.powerhour.data.music.common.models.TrackMetaData
import com.meaningless.powerhour.ui.searchplaylists.SearchActivity
import com.meaningless.powerhour.ui.selectplaylists.PlaylistBottomSheetFragment
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.partial_track_data.*
import kotlinx.android.synthetic.main.partial_track_view.*


class GameActivity : AppCompatActivity(), PlaylistBottomSheetFragment.Delegate,
    AuthenticationDialogFragment.Delegate {

    // region Fields
    private lateinit var viewModel: GameActivityViewModel
    // endregion

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTransitions()
        initAnimations()
        setContentView(R.layout.activity_game)
        initViews()
        initViewModel()
        viewModel.handleIntent(intent)
    }
    // endregion

    // region Setup Functions
    private fun initViews() {
        startButton.setOnClickListener(::onStartTapped)
        playlistButton.setOnClickListener(::onPlaylistTapped)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this)[GameActivityViewModel::class.java]
        viewModel.roundLiveData.observe(this, onRoundChanged)
        viewModel.trackLiveData.observe(this, onTrackChanged)
        viewModel.playlistLiveData.observe(this, onPlaylistChanged)
    }

    private fun initTransitions() {
        // Search activity is only called from the game activity
        // Only the enter and exit need to be set
        val fade = TransitionInflater.from(this).inflateTransition(R.transition.fade)
        window.enterTransition = fade
        window.exitTransition = fade
    }

    private fun initAnimations() {
        window?.attributes?.windowAnimations = R.style.PlaylistsDialogTransition
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
    override fun onPlaylistSelected(playlist: Playlist) {
        viewModel.pickPlaylist(playlist)
    }

    override fun onSearchPlaylists() {
        Intent(this, SearchActivity::class.java).also {
            startActivity(it, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    override fun onContinueAuthentication() {
        viewModel.authorize()
    }

    private val onPlaylistChanged = Observer<Playlist> {
        selectedPlaylistTextView.text = getString(R.string.playlist_current, it.name) ?:
                getString(R.string.playlist_current_placeholder)
    }

    private val onRoundChanged = Observer<Int> {
        if (it == 0) {
            resetClock()
            roundTextView.text = getString(R.string.round_placeholder)
        } else {
            startTimer()
            roundTextView.text = getString(R.string.round_number, it)
        }
    }

    private val onTrackChanged = Observer<TrackMetaData> {
        trackTextView.text = it?.name ?: getString(R.string.track_name_placeholder)
        artistTextView.text = it?.artist ?: getString(R.string.track_artist_placeholder)
        val bitmap = if (it?.image == null) null else DataUtils.makeBitmap(it.image)
        GlideApp.with(this)
            .load(bitmap)
            .placeholder(R.drawable.ic_music_note_black_24dp)
            .centerInside()
            .into(albumImageView)
    }

    private fun onStartTapped(view: View) {
        val isGameStarted = viewModel.toggleGameStart()
        val backgroundResource =
            if (isGameStarted) R.drawable.ic_pause_black_24dp else R.drawable.ic_play_arrow_black_24dp
        startButton.setImageResource(backgroundResource)
    }

    private fun onPlaylistTapped(view: View) =
        if (viewModel.isAuthenticated) showPlaylists() else showAuthenticationAlert()

    private fun showAuthenticationAlert() {
        val alert = AuthenticationDialogFragment()
        alert.delegate = this
        alert.show(supportFragmentManager, alert.tag)
    }

    private fun showPlaylists() {
        val playlistsModal =
            PlaylistBottomSheetFragment()
        playlistsModal.playlists = viewModel.getPlaylists()
        playlistsModal.delegate = this
        playlistsModal.show(supportFragmentManager, playlistsModal.tag)
    }
    // endregion
}
