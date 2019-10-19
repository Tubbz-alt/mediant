package io.numbers.mediant.api.zion

import android.app.Application
import org.witness.proofmode.util.DeviceInfo
import timber.log.Timber
import javax.inject.Inject

class ZionService @Inject constructor(
    application: Application
) {

    init {
        Timber.d(
            DeviceInfo.getDeviceInfo(
                application.applicationContext,
                DeviceInfo.Device.DEVICE_HARDWARE_MODEL
            )
        )

    }
}