package com.hogl.eregister.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hogl.eregister.data.dao.*
import com.hogl.eregister.data.entities.*
import com.hogl.eregister.data.entities.guard.Guard
import com.hogl.eregister.data.entities.institute.Institute
import com.hogl.eregister.data.entities.movement.Movement
import com.hogl.eregister.data.entities.visitor.Visitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Visitor::class, Guard::class, Institute::class, Movement::class, Group::class, GroupMovement::class], version = 35, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun visitorDao(): VisitorDao
    abstract fun instituteDao(): InstituteDao
    abstract fun guardDao(): GuardDao
    abstract fun movementDao(): MovementDao
    abstract fun groupDao(): GroupDao
    abstract fun groupMovementDao(): GroupMovementDao

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
                    "eregister_database.db"
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

            override fun onOpen(db: SupportSQLiteDatabase) {
                db.disableWriteAheadLogging()
                super.onOpen(db)
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(
                            database.visitorDao(),
                            database.guardDao(),
                            database.instituteDao(),
                            database.movementDao(),
                            database.groupDao(),
                            database.groupMovementDao()
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
            guardDao: GuardDao,
            instituteDao: InstituteDao,
            movementDao: MovementDao,
            groupDao: GroupDao,
            groupMovementDao: GroupMovementDao
        ) {

// Add sample data.

            guardDao.insert(
                Guard(
                    12,
                    "Bernard",
                    "Lacroix",
                    "Bernard",
                    "1234",
                    "Bernard@gmail.com",
                    7894562,
                    "Kigali",
                    1,
                    "PC12212121",
                    System.currentTimeMillis()
                )
            )
        }
    }
}