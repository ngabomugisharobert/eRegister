package com.example.eregister.data.entities.visitor

import android.app.Application
import com.example.eregister.data.AppDatabase
import com.example.eregister.data.repositories.VisitorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class VisitorApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val db by lazy { AppDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { VisitorRepository(db.visitorDao()) }

}