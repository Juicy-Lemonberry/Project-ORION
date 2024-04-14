package edu.singaporetech.inf2007.team48.project_orion.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAsset
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAssetType
import edu.singaporetech.inf2007.team48.project_orion.models.OrionChecklist
import edu.singaporetech.inf2007.team48.project_orion.models.OrionRecord
import edu.singaporetech.inf2007.team48.project_orion.models.OrionUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [OrionAsset::class, OrionAssetType::class, OrionChecklist::class, OrionRecord::class, OrionUser::class],
    version = 1,
    exportSchema = false
)
abstract class OrionRoomDatabase : RoomDatabase() {
    // Define abstract functions to access the DAOs for each entity
    abstract fun orionAssetDao(): OrionAssetDao
    abstract fun orionAssetTypeDao(): OrionAssetTypeDao
    abstract fun orionChecklistDao(): OrionChecklistDao
    abstract fun orionRecordDao(): OrionRecordDao
    abstract fun orionUserDao(): OrionUserDao

    companion object {
        @Volatile
        private var INSTANCE: OrionRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): OrionRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrionRoomDatabase::class.java,
                    "orion_room_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(OrionRoomDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class OrionRoomDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        // Populate the database in the background if needed
                        // For example, pre-populate the database with default data
                    }
                }
            }
        }
    }
}