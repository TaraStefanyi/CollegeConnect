package com.college.collegeconnect.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.college.collegeconnect.datamodels.FirebaseUserInfo
import com.college.collegeconnect.datamodels.User
import com.college.collegeconnect.models.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.kotest.core.spec.style.StringSpec
import io.mockk.*

class HomeViewModelTest : StringSpec({

    val app = mockk<Application>()

    fun mockFirebaseFirestore() {
        every { FirebaseFirestore.getInstance() } returns mockk()
        every { FirebaseFirestore.getInstance().firestoreSettings = any() } just runs
    }

    fun withMocks(block: () -> Unit) {
        mockkObject(FirebaseUserInfo)
        mockkConstructor(MutableLiveData::class)
        mockkStatic(FirebaseFirestore::class) {
            mockFirebaseFirestore()
            block()
        }
        unmockkObject(FirebaseUserInfo)
        unmockkConstructor(MutableLiveData::class)
    }

    "loadData" {
        withMocks {
            val user = User("rollNo", "email", "name", "branch", "college")
            val model = HomeViewModel(app)
            every { FirebaseUserInfo.getUserInfo(any(), any()) } answers {
                (secondArg() as (User) -> Unit).invoke(user); mockk()
            }

            every { anyConstructed<MutableLiveData<String>>().postValue(any()) } just runs

            model.loadData()
            verify(exactly = 1) { model.nameLive.postValue(user.name) }
            verify(exactly = 1) { model.branchLive.postValue(user.branch) }
            verify(exactly = 1) { model.rollNoLive.postValue(user.rollNo) }
        }
    }
})