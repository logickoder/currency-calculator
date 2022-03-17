package dev.logickoder.currencycalculator.data.repository

import android.content.Context
import dev.logickoder.currencycalculator.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

/**
 * Base implementation of the repository interface
 */
abstract class Repository<T> {
    /**
     * @return data from the specified [source]
     * */
    abstract suspend fun Context.retrieve(source: DataSource): Flow<ResultWrapper<T>>

    companion object {
        val TAG = Repository::class.simpleName
    }
}