package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.local.DocumentDatabase
import com.example.data.repository.DocumentRepositoryImpl
import com.example.data.repository.FirebaseDocumentRepositoryImpl
import com.example.domain.repository.DocumentRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.FirebaseFirestore

class DocForgeApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase manually if google-services.json is missing
        val isFirebaseInitialized = try {
            FirebaseApp.getInstance() != null
        } catch (e: Exception) {
            val options = FirebaseOptions.Builder()
                .setApplicationId(if (BuildConfig.FIREBASE_APP_ID.isNotEmpty()) BuildConfig.FIREBASE_APP_ID else "1:1234567890:android:abcdef")
                .setProjectId(if (BuildConfig.FIREBASE_PROJECT_ID.isNotEmpty()) BuildConfig.FIREBASE_PROJECT_ID else "dummy-project")
                .setApiKey(if (BuildConfig.FIREBASE_API_KEY.isNotEmpty()) BuildConfig.FIREBASE_API_KEY else "dummy-api-key")
                .setDatabaseUrl("https://dummy-project-default-rtdb.firebaseio.com")
                .build()
            FirebaseApp.initializeApp(this, options)
            true
        }

        if (isFirebaseInitialized) {
            val db = FirebaseFirestore.getInstance()
            val settings = firestoreSettings {
                setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            }
            db.firestoreSettings = settings
        }

        container = AppContainer(this)
    }
}

class AppContainer(private val application: Application) {
    val database: DocumentDatabase by lazy {
        Room.databaseBuilder(
            application,
            DocumentDatabase::class.java,
            "document_db"
        ).build()
    }

    // Toggle this to switch between Room and Firebase
    val useFirebase = true

    val documentRepository: DocumentRepository by lazy {
        if (useFirebase) {
            FirebaseDocumentRepositoryImpl(FirebaseFirestore.getInstance())
        } else {
            DocumentRepositoryImpl(database.documentDao)
        }
    }
}
