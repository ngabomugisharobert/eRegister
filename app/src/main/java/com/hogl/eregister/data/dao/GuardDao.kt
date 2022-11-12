package com.hogl.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hogl.eregister.data.entities.guard.Guard
import com.hogl.eregister.data.entities.movement.Movement
import kotlinx.coroutines.flow.Flow


@Dao
abstract class GuardDao : BaseDao<Guard>() {
    @get:Query("SELECT * from tb_guards")
    abstract val guards: List<Guard>

    @Query("SELECT * FROM tb_guards WHERE gua_username LIKE :username AND gua_password LIKE :password")
    abstract fun checkLogin(username: String,password:String): Flow<Guard>

    @Query("SELECT * from tb_guards")
    abstract fun allGuards(): Flow<List<Guard>>

}