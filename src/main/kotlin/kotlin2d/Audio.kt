package kotlin2d

import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.stb.STBVorbis
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.nio.ShortBuffer

object Audio {

    private var tracks = emptyList<File>()

    private var device: Long = 0
    private var context: Long = 0
    private var source: Int = 0
    private var currentBuffer: Int = 0
    private var lastTrackIndex: Int = -1
    private var available = false

    // SFX
    private const val SFX_POOL_SIZE = 8
    private val sfxSources = IntArray(SFX_POOL_SIZE)
    private var sfxNextIndex = 0

    private sealed class SoundEntry {
        class Single(val buffer: Int) : SoundEntry()
        class Group(val buffers: List<Int>, var lastIndex: Int = -1) : SoundEntry()
    }

    private val sfxEntries = mutableMapOf<String, SoundEntry>()
    private val groupPattern = Regex("^(.*)-(\\d+)$")

    fun init() {
        device = alcOpenDevice(null as CharSequence?)
        if (device == 0L) {
            println("Warning: No audio device found, continuing without music")
            return
        }

        context = alcCreateContext(device, null as IntArray?)
        if (context == 0L) {
            alcCloseDevice(device)
            device = 0
            println("Warning: Failed to create audio context")
            return
        }

        alcMakeContextCurrent(context)
        AL.createCapabilities(ALC.createCapabilities(device))

        source = alGenSources()

        // SFX: allocate source pool and preload sounds
        for (i in 0 until SFX_POOL_SIZE) {
            sfxSources[i] = alGenSources()
        }
        val soundsDir = File("sounds")
        val rawBuffers = mutableMapOf<String, Int>()
        if (soundsDir.isDirectory) {
            soundsDir.walk().filter { it.isFile && it.extension == "ogg" }.forEach { file ->
                val name = file.relativeTo(soundsDir).path.removeSuffix(".ogg")
                rawBuffers[name] = decodeOgg(file)
            }
        }

        // Group by convention: names ending in -{digits} form a group
        val grouped = mutableMapOf<String, MutableList<Int>>()
        val singles = mutableMapOf<String, Int>()
        for ((name, buffer) in rawBuffers) {
            val match = groupPattern.matchEntire(name)
            if (match != null) {
                val groupName = match.groupValues[1]
                grouped.getOrPut(groupName) { mutableListOf() }.add(buffer)
            } else {
                singles[name] = buffer
            }
        }
        for ((name, buffers) in grouped) {
            sfxEntries[name] = SoundEntry.Group(buffers)
        }
        for ((name, buffer) in singles) {
            if (name !in sfxEntries) {
                sfxEntries[name] = SoundEntry.Single(buffer)
            }
        }

        available = true

        val musicDir = File("music")
        tracks = musicDir.listFiles { f -> f.extension == "ogg" }?.toList().orEmpty()
        if (tracks.isEmpty()) {
            println("Warning: No music files found in music/ directory")
        } else {
            playRandomTrack()
        }
    }

    fun playSound(name: String) {
        if (!available) return
        val entry = sfxEntries[name]
        if (entry == null) {
            println("Warning: Unknown sound '$name'")
            return
        }
        val buffer = when (entry) {
            is SoundEntry.Single -> entry.buffer
            is SoundEntry.Group -> {
                val candidates = entry.buffers.indices.filter { it != entry.lastIndex }
                val idx = candidates.random()
                entry.lastIndex = idx
                entry.buffers[idx]
            }
        }
        val src = sfxSources[sfxNextIndex % SFX_POOL_SIZE]
        alSourceStop(src)
        alSourcei(src, AL_BUFFER, buffer)
        alSourcePlay(src)
        sfxNextIndex++
    }

    fun update() {
        if (!available) return
        if (tracks.isNotEmpty() && alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED) {
            playRandomTrack()
        }
    }

    fun cleanup() {
        if (!available) return
        alSourceStop(source)
        alDeleteSources(source)
        if (currentBuffer != 0) alDeleteBuffers(currentBuffer)
        for (src in sfxSources) {
            alSourceStop(src)
            alDeleteSources(src)
        }
        for (entry in sfxEntries.values) {
            when (entry) {
                is SoundEntry.Single -> alDeleteBuffers(entry.buffer)
                is SoundEntry.Group -> entry.buffers.forEach { alDeleteBuffers(it) }
            }
        }
        sfxEntries.clear()
        alcMakeContextCurrent(0)
        alcDestroyContext(context)
        alcCloseDevice(device)
        available = false
    }

    private fun playRandomTrack() {
        val candidates = tracks.indices.filter { it != lastTrackIndex }
        val nextIndex = candidates.random()
        lastTrackIndex = nextIndex

        // Delete previous buffer
        if (currentBuffer != 0) {
            alSourcei(source, AL_BUFFER, 0)
            alDeleteBuffers(currentBuffer)
        }

        currentBuffer = decodeOgg(tracks[nextIndex])
        alSourcei(source, AL_BUFFER, currentBuffer)
        alSourcePlay(source)
    }

    private fun decodeOgg(file: File): Int {
        val bytes = file.readBytes()
        val buf = MemoryUtil.memAlloc(bytes.size)
        buf.put(bytes).flip()

        MemoryStack.stackPush().use { stack ->
            val channels = stack.mallocInt(1)
            val sampleRate = stack.mallocInt(1)

            val pcm: ShortBuffer = STBVorbis.stb_vorbis_decode_memory(buf, channels, sampleRate)
                ?: error("Failed to decode OGG: ${file.name}")

            MemoryUtil.memFree(buf)

            val format = if (channels[0] == 1) AL_FORMAT_MONO16 else AL_FORMAT_STEREO16
            val buffer = alGenBuffers()
            alBufferData(buffer, format, pcm, sampleRate[0])
            MemoryUtil.memFree(pcm)

            return buffer
        }
    }
}
