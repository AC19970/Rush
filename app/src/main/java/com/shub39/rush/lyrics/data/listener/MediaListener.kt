package com.shub39.rush.lyrics.data.listener

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import com.shub39.rush.core.presentation.getMainArtist
import com.shub39.rush.core.presentation.getMainTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object MediaListener {

    private const val TAG = "MediaListener"

    private var msm: MediaSessionManager? = null
    private var nls: ComponentName? = null
    private var activeMediaController: MediaController? = null
    private val internalCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()
    val songInfoFlow = MutableSharedFlow<Pair<String, String>>()
    val songPositionFlow = MutableSharedFlow<Long>()
    val playbackSpeedFlow = MutableSharedFlow<Float>()

    private var initialised = false

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        if (!NotificationListener.canAccessNotifications(context) || initialised) return

        initialised = true

        msm = context.getSystemService<MediaSessionManager>()
        nls = ComponentName(context, NotificationListener::class.java)

        msm?.let { manager ->
            manager.addOnActiveSessionsChangedListener(
                { onActiveSessionsChanged(it) },
                nls
            )

            val activeSessions = manager.getActiveSessions(nls!!)
            val activeSession = activeSessions.find { isActive(it.playbackState) }
            activeMediaController = activeSession ?: activeSessions.firstOrNull()
            onActiveSessionsChanged(activeSessions)
            Log.d(TAG, "init $manager")
        } ?: Log.e(TAG, "MediaSessionManager is null")
    }

    fun destroy() {
        if (!initialised) return
        internalCallbacks.forEach { (_, callback) ->
            activeMediaController?.unregisterCallback(callback)
        }
        internalCallbacks.clear()
        initialised = false
    }

    private fun onActiveSessionsChanged(controllers: List<MediaController?>?) {
        val newCallbacks = mutableMapOf<MediaSession.Token, MediaController.Callback>()

        controllers?.filterNotNull()?.forEach { controller ->
            Log.d(TAG, "Session: $controller (${controller.sessionToken})")

            // Workaround for spotify, dunno if this is the most elegant solution but works :)
            if (controller.packageName.contains("spotify")) {
                coroutineScope.launch {
                    delay(2000)
                    setActiveMediaSession(controller)
                }
            }

            if (internalCallbacks.containsKey(controller.sessionToken)) {
                newCallbacks[controller.sessionToken] = internalCallbacks[controller.sessionToken]!!
            } else {
                val callback = object : MediaController.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackState?) {
                        onPlaybackStateChanged(controller, state)
                    }

                    override fun onMetadataChanged(metadata: MediaMetadata?) {
                        updateMetadata(controller, metadata)
                    }
                }

                controller.registerCallback(callback)
                newCallbacks[controller.sessionToken] = callback
            }
        }

        internalCallbacks.clear()
        internalCallbacks.putAll(newCallbacks)
    }

    private fun isActive(playbackState: PlaybackState?): Boolean {
        if (playbackState == null) return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            playbackState.isActive
        } else {
            when (playbackState.state) {
                PlaybackState.STATE_FAST_FORWARDING,
                PlaybackState.STATE_REWINDING,
                PlaybackState.STATE_SKIPPING_TO_PREVIOUS,
                PlaybackState.STATE_SKIPPING_TO_NEXT,
                PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM,
                PlaybackState.STATE_BUFFERING,
                PlaybackState.STATE_CONNECTING,
                PlaybackState.STATE_PLAYING -> true

                else -> false
            }
        }
    }

    private fun onPlaybackStateChanged(controller: MediaController, state: PlaybackState?) {
        if (isActive(state)) setActiveMediaSession(controller)
    }

    private fun setActiveMediaSession(newActive: MediaController) {
        activeMediaController = newActive
        updateMetadata(newActive, newActive.metadata)
    }

    private fun updateMetadata(controller: MediaController, metadata: MediaMetadata?) {
        if (controller.sessionToken != activeMediaController?.sessionToken) return

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) ?: ""

        coroutineScope.launch {
            songInfoFlow.emit(Pair(getMainTitle(title), getMainArtist(artist)))
            playbackSpeedFlow.emit(
                if (controller.playbackState?.let { isActive(it) } == true)
                    controller.playbackState?.playbackSpeed ?: 1f
                else 0f
            )
            controller.playbackState?.position?.let { songPositionFlow.emit(it) }
        }

    }

    // 🙏
    fun seek(timestamp: Long) {
        activeMediaController?.transportControls?.seekTo(timestamp)
    }
}