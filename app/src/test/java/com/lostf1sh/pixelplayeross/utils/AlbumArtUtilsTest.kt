package com.lostf1sh.pixelplayeross.utils

import com.google.common.truth.Truth.assertThat
import kotlin.io.path.createTempDirectory
import org.junit.jupiter.api.Test

class AlbumArtUtilsTest {

    @Test
    fun findExternalAlbumArtFile_returnsExplicitCoverFromDedicatedAlbumFolder() {
        val root = createTempDirectory("album-art-test").toFile()
        val albumDir = root.resolve("Calvin Harris - Funk Wav Bounces").apply { mkdirs() }
        val songFile = albumDir.resolve("Feels.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        val coverFile = albumDir.resolve("cover.jpg").apply { writeBytes(ByteArray(2048) { 7 }) }

        val resolved = AlbumArtUtils.findExternalAlbumArtFile(songFile.absolutePath)

        assertThat(resolved).isEqualTo(coverFile)
        root.deleteRecursively()
    }

    @Test
    fun findExternalAlbumArtFile_ignoresLooseArtworkNames() {
        val root = createTempDirectory("album-art-test").toFile()
        val albumDir = root.resolve("Singles").apply { mkdirs() }
        val songFile = albumDir.resolve("Random Song.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        albumDir.resolve("linkin_park_artwork_random.jpg").writeBytes(ByteArray(2048) { 9 })

        val resolved = AlbumArtUtils.findExternalAlbumArtFile(songFile.absolutePath)

        assertThat(resolved).isNull()
        root.deleteRecursively()
    }

    @Test
    fun findExternalAlbumArtFile_ignoresGenericDownloadsFolder() {
        val root = createTempDirectory("album-art-test").toFile()
        val downloadsDir = root.resolve("Downloads").apply { mkdirs() }
        val songFile = downloadsDir.resolve("Fresh Track.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        downloadsDir.resolve("cover.jpg").writeBytes(ByteArray(2048) { 5 })

        val resolved = AlbumArtUtils.findExternalAlbumArtFile(songFile.absolutePath)

        assertThat(resolved).isNull()
        root.deleteRecursively()
    }

    @Test
    fun findExternalAlbumArtFile_ignoresStudioGalleryFolder() {
        val root = createTempDirectory("album-art-test").toFile()
        val studioDir = root.resolve("Studio").apply { mkdirs() }
        val songFile = studioDir.resolve("Voice Note.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        studioDir.resolve("cover.jpg").writeBytes(ByteArray(2048) { 5 })

        val resolved = AlbumArtUtils.findExternalAlbumArtFile(songFile.absolutePath)

        assertThat(resolved).isNull()
        root.deleteRecursively()
    }

    @Test
    fun readExternalAlbumArtBytes_returnsCoverBytesWhenEnabled() {
        val root = createTempDirectory("album-art-test").toFile()
        val albumDir = root.resolve("Daft Punk - Discovery").apply { mkdirs() }
        val songFile = albumDir.resolve("One More Time.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        val coverBytes = ByteArray(2048) { 7 }
        albumDir.resolve("cover.jpg").writeBytes(coverBytes)

        val resolved = AlbumArtUtils.readExternalAlbumArtBytes(songFile.absolutePath, enabled = true)

        assertThat(resolved).isEqualTo(coverBytes)
        root.deleteRecursively()
    }

    @Test
    fun readExternalAlbumArtBytes_returnsNullWhenDisabled() {
        val root = createTempDirectory("album-art-test").toFile()
        val albumDir = root.resolve("Daft Punk - Discovery").apply { mkdirs() }
        val songFile = albumDir.resolve("Aerodynamic.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        albumDir.resolve("cover.jpg").writeBytes(ByteArray(2048) { 7 })

        val resolved = AlbumArtUtils.readExternalAlbumArtBytes(songFile.absolutePath, enabled = false)

        assertThat(resolved).isNull()
        root.deleteRecursively()
    }

    @Test
    fun readExternalAlbumArtBytes_returnsNullWhenCoverExceedsSizeLimit() {
        val root = createTempDirectory("album-art-test").toFile()
        val albumDir = root.resolve("Daft Punk - Discovery").apply { mkdirs() }
        val songFile = albumDir.resolve("Digital Love.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        albumDir.resolve("cover.jpg").writeBytes(ByteArray(4096) { 7 })

        val resolved = AlbumArtUtils.readExternalAlbumArtBytes(
            filePath = songFile.absolutePath,
            enabled = true,
            maxBytes = 2048L
        )

        assertThat(resolved).isNull()
        root.deleteRecursively()
    }

    @Test
    fun readExternalAlbumArtBytes_stillHonoursExcludedDirectoriesWhenEnabled() {
        val root = createTempDirectory("album-art-test").toFile()
        val downloadsDir = root.resolve("Downloads").apply { mkdirs() }
        val songFile = downloadsDir.resolve("Fresh Track.mp3").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        downloadsDir.resolve("cover.jpg").writeBytes(ByteArray(2048) { 5 })

        val resolved = AlbumArtUtils.readExternalAlbumArtBytes(songFile.absolutePath, enabled = true)

        assertThat(resolved).isNull()
        root.deleteRecursively()
    }
}
