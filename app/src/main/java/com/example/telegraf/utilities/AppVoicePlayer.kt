package com.example.telegraf.utilities

import android.media.MediaPlayer
import com.example.telegraf.database.getFileFromStorage
import java.io.File
import java.io.IOException

class AppVoicePlayer {
    private lateinit var mediaPlayer: MediaPlayer;
    private lateinit var mediaFile: File;

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        mediaFile = File(APP_ACTIVITY.filesDir, messageKey);
        if (mediaFile.exists() && mediaFile.length() > 0 && mediaFile.isFile) {
            startPlay {
                function();
            }
        } else {
            getFileFromStorage(mediaFile, fileUrl) {
                startPlay {
                    function();
                }
            }
        }
    }

    private fun startPlay(function: () -> Unit) {
        try {
            mediaPlayer.setDataSource(mediaFile.absolutePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener {
                stop {
                    function();
                }
            }
        } catch (e: IOException) {
            e.message.toString()
        }
    }

    fun stop(function: () -> Unit) {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            function();
        } catch (e: IOException) {
            e.message.toString()
            function();
        }
    }

    fun release() {
        mediaPlayer.release();
    }

    fun init() {
        mediaPlayer = MediaPlayer();
    }
}