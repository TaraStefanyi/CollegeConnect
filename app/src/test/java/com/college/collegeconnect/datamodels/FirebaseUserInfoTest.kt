package com.college.collegeconnect.datamodels

import android.content.Context
import android.util.Log
import com.college.collegeconnect.security.Security
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import io.kotest.core.spec.style.StringSpec
import io.mockk.*
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class FirebaseUserInfoTest : StringSpec({

    val ctx = mockk<Context>()

    fun String.mockEncrypt() = "encrypted_$this"
    fun String.mockDecrypt() = substring(10)

    fun mockFirebaseAuth() {
        every { FirebaseAuth.getInstance() } returns mockk()
        every { FirebaseAuth.getInstance().currentUser } returns mockk()
        every { FirebaseAuth.getInstance().currentUser?.uid } returns "USER_UID"
    }

    fun mockFirebaseFirestore() {
        every { FirebaseFirestore.getInstance() } returns mockk()
        every { FirebaseFirestore.getInstance().collection(any()) } returns mockk()
        every { FirebaseFirestore.getInstance().collection(any()).document(any()) } returns mockk()
    }

    fun mockSecurity() {
        every { Security.encrypt(any(), any()) } answers { (firstArg() as String).mockEncrypt() }
        every { Security.decrypt(any(), any()) } answers { (firstArg() as String).mockDecrypt() }
    }

    fun mockLog() {
        every { Log.e(any(), any()) } returns 1
    }

    fun withMocks(block: () -> Unit) {
        mockkObject(Security)
        mockkStatic(FirebaseAuth::class, FirebaseFirestore::class, Log::class) {
            mockFirebaseAuth()
            mockFirebaseFirestore()
            mockSecurity()
            mockLog()
            block()
        }
        unmockkObject(Security)
    }

    "uploadUserInfo" {
        withMocks {
            val argument = slot<User>()
            val user = User("rollNo", "email", "name", "branch", "college")

            every { FirebaseFirestore.getInstance().collection(any()).document(any()).set(capture(argument)) } returns mockk(relaxed = true)

            FirebaseUserInfo.uploadUserInfo(user, ctx)
            expectThat(argument.captured) {
                get { name }.isEqualTo(user.name.mockEncrypt())
                get { email }.isEqualTo(user.email.mockEncrypt())
                get { college }.isEqualTo(user.college.mockEncrypt())
                get { branch }.isEqualTo(user.branch.mockEncrypt())
                get { rollNo }.isEqualTo(user.rollNo.mockEncrypt())
            }
        }
    }

    "uploadUserInfo - null user" {
        withMocks {
            every { FirebaseAuth.getInstance().currentUser } returns null
            FirebaseUserInfo.uploadUserInfo(User(), ctx)
            verify(exactly = 0) { FirebaseFirestore.getInstance() }
        }
    }

    "getUserInfo" {
        withMocks {
            val user = User("rollNo", "email", "name", "branch", "college")
            val document = mockk<DocumentSnapshot>()
            listOf("rollNo", "email", "name", "branch", "college").forEach { every { document.getString(it) } returns it.mockEncrypt() }
            every { FirebaseFirestore.getInstance().collection(any()).document(any()).addSnapshotListener(any<MetadataChanges>(), any()) } answers {
                (secondArg() as EventListener<DocumentSnapshot>).onEvent(document, null); mockk()
            }
            FirebaseUserInfo.getUserInfo(ctx) {
                expectThat(it) {
                    get { name }.isEqualTo(user.name)
                    get { email }.isEqualTo(user.email)
                    get { college }.isEqualTo(user.college)
                    get { branch }.isEqualTo(user.branch)
                    get { rollNo }.isEqualTo(user.rollNo)
                }
            }
        }
    }

    "getUserInfo - document is null" {
        withMocks {
            every { FirebaseFirestore.getInstance().collection(any()).document(any()).addSnapshotListener(any<MetadataChanges>(), any()) } answers {
                (secondArg() as EventListener<DocumentSnapshot>).onEvent(null, null); mockk()
            }
            FirebaseUserInfo.getUserInfo(ctx) { throw RuntimeException() }
        }
    }

    "getUserInfo - error" {
        withMocks {
            val error = mockk<FirebaseFirestoreException>()
            every { error.message } returns "error message"
            every { FirebaseFirestore.getInstance().collection(any()).document(any()).addSnapshotListener(any<MetadataChanges>(), any()) } answers {
                (secondArg() as EventListener<DocumentSnapshot>).onEvent(mockk(), error); mockk()
            }
            FirebaseUserInfo.getUserInfo(ctx) { throw RuntimeException() }
        }
    }

    "getUserInfo - null user" {
        withMocks {
            every { FirebaseAuth.getInstance().currentUser } returns null
            FirebaseUserInfo.getUserInfo(ctx) { }
            verify(exactly = 0) { FirebaseFirestore.getInstance() }
        }
    }
})