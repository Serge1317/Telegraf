package com.example.telegraf.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

@Suppress("Deprecation")
class AppVoiceRecorder() {

    private var recorder: MediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(APP_ACTIVITY.applicationContext);
    } else {
        MediaRecorder();
    };

    private lateinit var file: File;
    private lateinit var messageKey: String;

    fun startRecord(messageKey: String) {
        this.messageKey = messageKey;
        try {
            createFileForRecorder();
            prepareMediaRecorder();
            recorder.start();
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    private fun prepareMediaRecorder() {
        recorder.apply {
            reset();
            setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(file.absolutePath);
            prepare();
        }
    }

    private fun createFileForRecorder() {
        file = File(APP_ACTIVITY.filesDir, messageKey)
        file.createNewFile();
    }

    fun stopRecord(onSuccess: (file: File, messageKey: String) -> Unit) {
        try {
            recorder.stop();
            onSuccess(file, messageKey);
        } catch (e: Exception) {
            e.message.toString()
            file.delete();
            showToast("fail stopRecord")
        }
    }

    fun releaseRecorder() {
        try {
            recorder.release();
        } catch (e: Exception) {
            e.message.toString()
        }
    }
}