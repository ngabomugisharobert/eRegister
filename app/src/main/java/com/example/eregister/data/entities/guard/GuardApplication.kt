package com.example.eregister.data.entities.guard



import android.app.Application
import com.example.eregister.data.AppDatabase
import com.example.eregister.data.repositories.GuardRepository
import com.example.eregister.data.repositories.InstituteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class GuardApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val db by lazy { AppDatabase.getDatabase(this,applicationScope) }
    val repository by lazy {GuardRepository(db.guardDao())}

}