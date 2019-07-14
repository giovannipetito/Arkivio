package it.giovanni.kotlin.persistence.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import io.reactivex.Flowable
@Dao
interface UserPreferencesDao {

    /**
     * Get a user by id.
     * @return the user from the table with a specific id.
     */
    @Query("SELECT * from UserPreferences")
    fun getPreferences(): Flowable<List<UserPreferences>>

    /**
     * Insert a user in the database. If the user already exists, replace it.
     * @param preference the preferenc key value to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPreference(preference: UserPreferences)

    /**
     * Delete all preferences.
     */
    @Query("DELETE FROM UserPreferences")
    fun deleteAllPreferences()
}