package com.koloos.app.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.koloos.app.data.sales.SalesRepository
import com.koloos.app.data.sync.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val salesRepository: SalesRepository,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        salesRepository.pushPendingSales()
        syncRepository.pullLatest()
        Result.success()
    } catch (ex: Exception) {
        Result.retry()
    }
}
