package com.jaybox.data

import android.content.pm.ApplicationInfo
import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.jaybox.core.BlackBoxCore
import com.jaybox.core.BlackBoxCore.getPackageManager
import com.jaybox.core.JayboxCore
import com.jaybox.core.entity.pm.InstallResult
import com.jaybox.core.utils.AbiUtils
import com.jaybox.R
import com.jaybox.app.App
import com.jaybox.app.AppManager
import com.jaybox.data.entity.AppInfo
import com.jaybox.data.entity.DumpInfo
import java.io.File

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/23 14:29
 */
class DexDumpRepository {

    private var dumpTaskId = 0

    fun getAppList(mAppListLiveData: MutableLiveData<List<AppInfo>>) {

        val installedApplications: List<ApplicationInfo> =
                getPackageManager().getInstalledApplications(0)
        val installedList = mutableListOf<AppInfo>()

        for (installedApplication in installedApplications) {
            val file = File(installedApplication.sourceDir)

            if ((installedApplication.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue

            if (!AbiUtils.isSupport(file)) continue


            val info = AppInfo(
                    installedApplication.loadLabel(getPackageManager()).toString(),
                    installedApplication.packageName,
                    installedApplication.loadIcon(getPackageManager())
            )
            installedList.add(info)
        }

        mAppListLiveData.postValue(installedList)
    }

    fun dumpDex(source: String, dexDumpLiveData: MutableLiveData<DumpInfo>) {
        dexDumpLiveData.postValue(DumpInfo(DumpInfo.LOADING))
        val result = if (URLUtil.isValidUrl(source)) {
            JayboxCore.get().dumpDex(Uri.parse(source))
        } else if (source.contains("/")) {
            JayboxCore.get().dumpDex(File(source))
        } else {
            JayboxCore.get().dumpDex(source)
        }

        if (result != null) {
            dumpTaskId++
            startCountdown(result, dexDumpLiveData)
        } else {
            dexDumpLiveData.postValue(DumpInfo(DumpInfo.TIMEOUT))
        }
    }


    fun dumpSuccess() {
        dumpTaskId++
    }

    private fun startCountdown(installResult: InstallResult, dexDumpLiveData: MutableLiveData<DumpInfo>) {
        GlobalScope.launch {
            val tempId = dumpTaskId
            while (JayboxCore.get().isRunning) {
                delay(20000)
                //10s
                if (!AppManager.mBlackBoxLoader.isFixCodeItem()) {
                    break
                }
                //fixCodeItem 需要长时间运行，普通内存dump不需要
            }
            if (tempId == dumpTaskId) {
                if (JayboxCore.get().isExistDexFile(installResult.packageName)) {
                    dexDumpLiveData.postValue( DumpInfo(
                            DumpInfo.SUCCESS,
                            App.getContext().getString(R.string.dex_save, File(BlackBoxCore.get().dexDumpDir, installResult.packageName).absolutePath)
                    ))
                } else {
                    dexDumpLiveData.postValue(DumpInfo(DumpInfo.TIMEOUT))
                }
            }
        }
    }
}