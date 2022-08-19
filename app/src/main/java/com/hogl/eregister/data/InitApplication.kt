package com.hogl.eregister.data

import android.app.Application
import com.hogl.eregister.data.repositories.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class InitApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val db by lazy { AppDatabase.getDatabase(this,applicationScope) }
    val guardRepository by lazy { GuardRepository(db.guardDao()) }
    val instituteRepository by lazy { InstituteRepository(db.instituteDao()) }
    val movementRepository by lazy { MovementRepository(db.movementDao()) }
    val visitorRepository by lazy { VisitorRepository(db.visitorDao()) }
    val groupRepository by lazy { GroupRepository(db.groupDao()) }
    val groupMovementRepository by lazy { GroupMovementRepository(db.groupMovementDao()) }
}