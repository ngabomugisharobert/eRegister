package com.example.eregister.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.eregister.data.dao.GuardDao
import com.example.eregister.data.dao.InstituteDao
import com.example.eregister.data.dao.MovementDao
import com.example.eregister.data.dao.VisitorDao
import com.example.eregister.data.entities.institute.Institute
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.data.entities.guard.Guard
import com.example.eregister.data.entities.movement.Movement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Visitor::class, Institute::class, Guard::class, Movement::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun visitorDao(): VisitorDao
    abstract fun instituteDao(): InstituteDao
    abstract fun guardDao(): GuardDao
    abstract fun movementDao(): MovementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eRegister_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(
                            database.visitorDao(),
                            database.instituteDao(),
                            database.guardDao(),
                            database.movementDao()
                        )
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more data, just add them.
         */
        suspend fun populateDatabase(
            visitorDao: VisitorDao,
            instituteDao: InstituteDao,
            guardDao: GuardDao,
            movementDao: MovementDao
        ) {

// Add sample data.


            instituteDao.insert(
                Institute(4, "HOGL", "Rwaza-Musanze")
            )
            guardDao.insert(
                Guard(
                    12,
                    "Alex",
                    "Ngabo",
                    "alex",
                    "alex",
                    "alex@gmail.com",
                    7894562,
                    7861234,
                    19958000000055556
                )
            )
        }
    }
}
