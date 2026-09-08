package com.lostf1sh.pixelplayeross.utils

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

/**
 * The app-wide observer mirrors the folder-cover preference into [AlbumArtUtils]. Folder covers
 * outrank embedded art, so a value that changes outside the settings screen — a backup restore
 * writing straight into DataStore — leaves every cached cover resolved under the old precedence.
 */
class FolderAlbumArtUpdateTest {

    @Test
    fun firstObservedValue_onlyMirrors() {
        assertThat(resolveFolderAlbumArtUpdate(previous = null, observed = true))
            .isEqualTo(FolderAlbumArtUpdate.MIRROR_ONLY)
        assertThat(resolveFolderAlbumArtUpdate(previous = null, observed = false))
            .isEqualTo(FolderAlbumArtUpdate.MIRROR_ONLY)
    }

    @Test
    fun valueAlreadyMirrored_isIgnored() {
        assertThat(resolveFolderAlbumArtUpdate(previous = true, observed = true))
            .isEqualTo(FolderAlbumArtUpdate.IGNORE)
        assertThat(resolveFolderAlbumArtUpdate(previous = false, observed = false))
            .isEqualTo(FolderAlbumArtUpdate.IGNORE)
    }

    @Test
    fun outOfBandChange_invalidatesCachedArtwork() {
        assertThat(resolveFolderAlbumArtUpdate(previous = false, observed = true))
            .isEqualTo(FolderAlbumArtUpdate.MIRROR_AND_INVALIDATE)
        assertThat(resolveFolderAlbumArtUpdate(previous = true, observed = false))
            .isEqualTo(FolderAlbumArtUpdate.MIRROR_AND_INVALIDATE)
    }
}
