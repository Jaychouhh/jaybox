package com.jaybox.util

import com.jaybox.data.DexDumpRepository
import com.jaybox.view.main.MainFactory


/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:38
 */
object InjectionUtil {

    private val dexDumpRepository = DexDumpRepository()


    fun getMainFactory() : MainFactory {
        return MainFactory(dexDumpRepository)
    }

}