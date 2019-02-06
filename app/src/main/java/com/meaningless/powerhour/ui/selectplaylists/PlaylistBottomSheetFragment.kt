package com.meaningless.powerhour.ui.selectplaylists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meaningless.powerhour.R
import com.meaningless.powerhour.data.music.common.models.Playlist
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.modal_playlists.*


class PlaylistBottomSheetFragment : BottomSheetDialogFragment(),
    PlaylistAdapter.Delegate {

    var delegate: Delegate? = null
    var playlists: List<Playlist> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.modal_playlists, container, false)

    override fun onStart() {
        super.onStart()
        val adapter = PlaylistAdapter(this)
        adapter.setPlaylists(playlists)
        val linearLayoutManager = LinearLayoutManager(context ?: return)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        playlistsRecyclerView.setHasFixedSize(true)
        playlistsRecyclerView.layoutManager = linearLayoutManager
        playlistsRecyclerView.adapter = adapter
        playlistsSearchButton.setOnClickListener(::onSearchPlaylistsSelected)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.PlaylistsDialogTransition
    }

    override fun onPlaylistSelected(playlist: Playlist) {
        dismiss()
        delegate?.onPlaylistSelected(playlist)
    }

    private fun onSearchPlaylistsSelected(view: View) {
        dismiss()
        delegate?.onSearchPlaylists()
    }

    interface Delegate {
        fun onPlaylistSelected(playlist: Playlist)
        fun onSearchPlaylists()
    }
}