package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.*

object GiftSoundPlayer {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 44100

    /**
     * Synthesizes and plays dedicated audio effects for TikTok-style gifts
     * using standard Android AudioTrack PCM generation.
     */
    fun playGiftSound(giftId: String?) {
        scope.launch {
            try {
                when {
                    giftId?.contains("lion") == true -> playLionRoar()
                    giftId?.contains("galaxy") == true -> playGalaxyCosmic()
                    giftId?.contains("sports_car") == true || giftId?.contains("car") == true -> playSportsCarRev()
                    giftId?.contains("jet") == true -> playJetFlyby()
                    giftId?.contains("money_gun") == true || giftId?.contains("money") == true -> playMoneyGunSound()
                    giftId?.contains("falcon") == true -> playFalconSound()
                    giftId?.contains("yacht") == true || giftId?.contains("train") == true -> playFanfareSound()
                    else -> playSparkleChime()
                }
            } catch (_: Exception) {
                // Failsafe on emulated environments without audio rendering
            }
        }
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            val durationMs = (buffer.size * 1000L / SAMPLE_RATE) + 60L
            Thread.sleep(durationMs.coerceAtMost(3000L))
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        } catch (_: Exception) {}
    }

    // 1. Lion Roar & Majestic Brass Power Chord
    private fun playLionRoar() {
        val durationSec = 2.0
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = when {
                t < 0.25 -> (t / 0.25)
                t < 1.3 -> 1.0
                else -> (1.0 - (t - 1.3) / 0.7).coerceAtLeast(0.0)
            }
            // Low rumble frequencies with growl tremolo (16Hz modulation)
            val tremolo = 1.0 + 0.35 * sin(2.0 * PI * 16.0 * t)
            val subBass = sin(2.0 * PI * 65.4 * t) // C2
            val lowGrowl = sin(2.0 * PI * 98.0 * t) // G2
            val brass = sin(2.0 * PI * 130.8 * t) + 0.5 * sin(2.0 * PI * 261.6 * t) // C3, C4
            val sample = (subBass * 0.4 + lowGrowl * 0.3 + brass * 0.3) * tremolo * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.85).toInt().toShort()
        }
        playPcm(buffer)
    }

    // 2. Galaxy Cosmic Sweep & Celestial Shimmer
    private fun playGalaxyCosmic() {
        val durationSec = 2.2
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = if (t < 0.3) t / 0.3 else exp(-1.2 * (t - 0.3))
            // Sweep from 320Hz to 1650Hz
            val freq = 320.0 + 1330.0 * (t / durationSec).pow(0.8)
            val lfo = sin(2.0 * PI * 7.0 * t)
            val wave = sin(2.0 * PI * freq * t) + 0.35 * sin(2.0 * PI * (freq * 1.5 + lfo * 25.0) * t)
            val sparkle = if (t > 0.4) 0.25 * sin(2.0 * PI * 2093.0 * t) else 0.0
            val sample = (wave * 0.7 + sparkle) * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.78).toInt().toShort()
        }
        playPcm(buffer)
    }

    // 3. Sports Car Engine Rev
    private fun playSportsCarRev() {
        val durationSec = 1.8
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = if (t < 0.1) t / 0.1 else if (t < 1.3) 1.0 else (1.8 - t) / 0.5
            // RPM acceleration curve
            val rpmFreq = if (t < 1.0) 85.0 + 360.0 * (t / 1.0).pow(1.5) else 445.0 - 160.0 * (t - 1.0)
            val cylinder1 = sin(2.0 * PI * rpmFreq * t)
            val cylinder2 = sin(2.0 * PI * (rpmFreq * 2.0) * t)
            val rumble = sin(2.0 * PI * 45.0 * t)
            val sample = (cylinder1 * 0.45 + cylinder2 * 0.35 + rumble * 0.2) * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.82).toInt().toShort()
        }
        playPcm(buffer)
    }

    // 4. Jet Supersonic Flyby
    private fun playJetFlyby() {
        val durationSec = 2.0
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        var randomState = 12345
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val dist = (t - 0.9) / 0.6
            val envelope = exp(-dist * dist)
            randomState = (randomState * 1103515245 + 12345) and 0x7fffffff
            val noise = (randomState / 2147483648.0) * 2.0 - 1.0
            val turbineFreq = 1250.0 - 550.0 * (t / durationSec)
            val turbine = sin(2.0 * PI * turbineFreq * t)
            val sample = (noise * 0.6 + turbine * 0.4) * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.75).toInt().toShort()
        }
        playPcm(buffer)
    }

    // 5. Money Gun Rapid Cash Register Cha-Ching
    private fun playMoneyGunSound() {
        val durationSec = 1.6
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        val dingTimes = listOf(0.0, 0.32, 0.64, 0.96)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sample = 0.0
            for (dt in dingTimes) {
                if (t >= dt) {
                    val localT = t - dt
                    val env = exp(-8.0 * localT)
                    val tone1 = sin(2.0 * PI * 1975.5 * localT) // B6
                    val tone2 = sin(2.0 * PI * 2637.0 * localT) // E7
                    sample += (tone1 * 0.4 + tone2 * 0.6) * env
                }
            }
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.82).toInt().toShort()
        }
        playPcm(buffer)
    }

    // 6. Falcon Soaring Call
    private fun playFalconSound() {
        val durationSec = 1.5
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = if (t < 0.1) t / 0.1 else exp(-2.5 * (t - 0.1))
            val vibrato = sin(2.0 * PI * 22.0 * t) * 80.0
            val freq = (2450.0 - 450.0 * (t / durationSec)) + vibrato
            val sample = sin(2.0 * PI * freq * t) * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.76).toInt().toShort()
        }
        playPcm(buffer)
    }

    // 7. Fanfare for Yacht & Train
    private fun playFanfareSound() {
        val durationSec = 1.8
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        val notes = listOf(
            Pair(0.0..0.35, 523.25),  // C5
            Pair(0.35..0.7, 659.25),  // E5
            Pair(0.7..1.05, 783.99),  // G5
            Pair(1.05..1.8, 1046.50)  // C6
        )
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sample = 0.0
            for ((range, freq) in notes) {
                if (t in range) {
                    val localT = t - range.start
                    val noteLen = range.endInclusive - range.start
                    val env = if (localT < 0.04) localT / 0.04 else (1.0 - (localT / noteLen) * 0.5)
                    sample = (sin(2.0 * PI * freq * t) + 0.3 * sin(2.0 * PI * (freq * 2.0) * t)) * env
                    break
                }
            }
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.8).toInt().toShort()
        }
        playPcm(buffer)
    }

    // 8. Magic Twinkle / Chime for Sweet Gifts
    private fun playSparkleChime() {
        val durationSec = 1.6
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        val arpeggio = listOf(
            Pair(0.0..0.25, 784.0),   // G5
            Pair(0.25..0.5, 1046.5),  // C6
            Pair(0.5..0.75, 1318.5),  // E6
            Pair(0.75..1.6, 1568.0)   // G6
        )
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sample = 0.0
            for ((range, freq) in arpeggio) {
                if (t >= range.start) {
                    val localT = t - range.start
                    val env = exp(-3.5 * localT)
                    sample += (sin(2.0 * PI * freq * localT) + 0.4 * sin(2.0 * PI * (freq * 2.0) * localT)) * env * 0.5
                }
            }
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767 * 0.75).toInt().toShort()
        }
        playPcm(buffer)
    }
}
