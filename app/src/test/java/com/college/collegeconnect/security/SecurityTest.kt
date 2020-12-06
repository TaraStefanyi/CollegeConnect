package com.college.collegeconnect.security

import android.content.Context
import com.college.collegeconnect.datamodels.SaveSharedPreference
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.*

class SecurityTest : StringSpec({

    val ctx = mockk<Context>()

    fun withMocks(block: () -> Unit) {
        mockkStatic(SaveSharedPreference::class, android.util.Base64::class) {
            every { android.util.Base64.decode(any<String>(), any()) } answers {
                Base64.getDecoder().decode(firstArg() as String)
            }
            every { android.util.Base64.encodeToString(any(), any()) } answers {
                Base64.getEncoder().encodeToString(firstArg() as ByteArray)
            }
            block()
        }
    }

    "encrypt and decrypt" {
        withMocks {
            every { SaveSharedPreference.getEncryptionKey(any()) } returns "AAECAwQFBgcICQoLDA0ODw=="
            expectThat(Security.decrypt(Security.encrypt("Hello", ctx), ctx)).isEqualTo("Hello")
            verify(exactly = 2) { SaveSharedPreference.getEncryptionKey(ctx) }
        }
    }

    "encrypt and decrypt - key does not exist" {
        withMocks {
            val key = slot<String>()
            every { SaveSharedPreference.getEncryptionKey(any()) } returns null andThen { key.captured }
            every { SaveSharedPreference.setEncryptionKey(any(), capture(key)) } just runs
            expectThat(Security.decrypt(Security.encrypt("World", ctx), ctx)).isEqualTo("World")
        }
    }
})