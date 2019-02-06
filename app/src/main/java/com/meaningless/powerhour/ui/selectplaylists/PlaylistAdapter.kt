package com.meaningless.powerhour.ui.selectplaylists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meaningless.powerhour.R
import com.meaningless.powerhour.data.music.common.models.Playlist
import kotlinx.android.synthetic.main.item_playlist.view.*

class PlaylistAdapter(private val delegate: Delegate?) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.playlistNameTextView.text = playlist.name
        holder.itemView.setOnClickListener {
            delegate?.onPlaylistSelected(playlist)
        }
        when (playlist.type) {
            Playlist.Type.INCLUDED -> {
                holder.playlistIconImageView.visibility = View.VISIBLE
                holder.playlistIconImageView.setImageResource(R.drawable.ic_music_note_black_24dp)
            }
            Playlist.Type.HISTORY -> {
                holder.playlistIconImageView.visibility = View.VISIBLE
                holder.playlistIconImageView.setImageResource(R.drawable.ic_history_black_24dp)
            }
            Playlist.Type.NONE -> {
                holder.playlistIconImageView.visibility = View.GONE
            }
        }
    }

    fun setPlaylists(playlists: List<Playlist>) {
        val sorts = listOf(Playlist.Type.HISTORY, Playlist.Type.INCLUDED, Playlist.Type.NONE)
        val sortedPlaylists = playlists.sortedBy {
            sorts.indexOf(it.type)
        }
        this.playlists.clear()
        this.playlists.addAll(sortedPlaylists)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playlistNameTextView: TextView = view.playlistNameTextView
        val playlistIconImageView: ImageView = view.playlistIconImageView
    }

    interface Delegate {
        fun onPlaylistSelected(playlist: Playlist)
    }
}