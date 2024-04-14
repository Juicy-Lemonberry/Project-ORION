package edu.singaporetech.inf2007.team48.project_orion

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import edu.singaporetech.inf2007.team48.project_orion.dao.OrionRoomDatabase
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionAssetRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionAssetTypeRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionChecklistRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionPreferenceRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionRecordRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionUserRepository
import kotlinx.coroutines.GlobalScope

class OrionApp : Application() {
    val Context.dataStore by preferencesDataStore(name = "OrionPreferences")

    private val orionAssetDao by lazy { OrionRoomDatabase.getDatabase(this, GlobalScope).orionAssetDao() }
    private val orionAssetTypeDao by lazy { OrionRoomDatabase.getDatabase(this, GlobalScope).orionAssetTypeDao() }
    private val orionChecklistDao by lazy { OrionRoomDatabase.getDatabase(this, GlobalScope).orionChecklistDao() }
    private val orionRecordDao by lazy { OrionRoomDatabase.getDatabase(this, GlobalScope).orionRecordDao() }
    private val orionUserDao by lazy { OrionRoomDatabase.getDatabase(this, GlobalScope).orionUserDao() }

    val orionAssetRepository by lazy { OrionAssetRepository(orionAssetDao) }
    val orionAssetTypeRepository by lazy { OrionAssetTypeRepository(orionAssetTypeDao) }
    val orionChecklistRepository by lazy { OrionChecklistRepository(orionChecklistDao) }
    val orionRecordRepository by lazy { OrionRecordRepository(orionRecordDao) }
    val orionUserRepository by lazy { OrionUserRepository(orionUserDao) }
    val orionPreferenceRepository by lazy { OrionPreferenceRepository(dataStore) }

}