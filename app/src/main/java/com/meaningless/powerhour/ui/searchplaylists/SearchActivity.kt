package com.meaningless.powerhour.ui.searchplaylists

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.transition.TransitionInflater
import com.meaningless.powerhour.R
import com.meaningless.powerhour.data.database.DataManager
import com.meaningless.powerhour.data.music.common.models.Playlist
import com.meaningless.powerhour.ui.game.GameActivity
import com.meaningless.powerhour.ui.selectplaylists.PlaylistAdapter
import com.meaningless.powerhour.utils.DataUtils
import kotlinx.android.synthetic.main.activity_search_playlists.*

class SearchActivity : AppCompatActivity(), PlaylistAdapter.Delegate {

    lateinit var viewModel: SearchActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_playlists)
        initTransitions()
        initAnimations()
        initView()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        searchEditText.requestFocus()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this)[SearchActivityViewModel::class.java]
        viewModel.errorLiveData.observe(this, onErrorChanged)
        viewModel.searchResultsLiveData.observe(this, onSearchResultsChanged)
    }

    private fun initView() {
        val adapter = PlaylistAdapter(this)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        searchRecyclerView.setHasFixedSize(true)
        searchRecyclerView.layoutManager = linearLayoutManager
        searchRecyclerView.adapter = adapter
        upButton.setOnClickListener(::onUpButtonTapped)
        searchEditText.addTextChangedListener(onTextChanged)
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

    private val onErrorChanged = Observer<String> {
        // TODO: Display error in view
    }

    private val onSearchResultsChanged = Observer<List<Playlist>> {
        val adapter = searchRecyclerView?.adapter as? PlaylistAdapter ?: return@Observer
        adapter.setPlaylists(it ?: listOf())
    }

    private fun search(query: String) {
        viewModel.search(query)
    }

    private fun onUpButtonTapped(view: View) {
        exit()
    }

    private val onTextChanged = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            search(s?.toString() ?: return)
        }
    }

    override fun onPlaylistSelected(playlist: Playlist) {
        // Persist to recents
        val recents = DataManager.getRecentPlaylists(this)
        recents.add(playlist)
        recents.save(this)

        // Navigate back to game activity
        exit(playlist)
    }

    private fun exit(selectedPlaylist: Playlist? = null) {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        val playlist =
            if (selectedPlaylist == null) null else DataUtils.makeJsonString(selectedPlaylist)
        Intent(this, GameActivity::class.java).also {
            it.putExtra(IntentKey.PLAYLIST, playlist)
            startActivity(it, options)
        }
    }

    object IntentKey {
        const val PLAYLIST = "playlist"
    }
}