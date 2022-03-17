package dev.logickoder.currencycalculator.data.store

import android.content.Context
import dev.logickoder.currencycalculator.data.Identifiable
import dev.logickoder.currencycalculator.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

/**
 * Saves an [Identifiable] item to a data store
 */
interface DataStoreManager<T : Identifiable> {
    /**
     * @param data the data to save
     * saves [data] in the underlying storage
     */
    suspend fun save(data: T, context: Context)

    /**
     * @param id the id of the [Identifiable] data item
     *
     * returns the data if it exists or null if not
     */
    fun get(id: Long, context: Context): Flow<ResultWrapper<T>>
}