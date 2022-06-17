package com.example.eregister.data.entities.institute



import android.app.Application
import com.example.eregister.data.AppDatabase
import com.example.eregister.data.repositories.InstituteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class InstituteApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val db by lazy { AppDatabase.getDatabase(this,applicationScope) }
    val repository by lazy {InstituteRepository(db.instituteDao())}

}