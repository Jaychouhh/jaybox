// IBDumpService.aidl
package com.jaybox.core.core.system.dump;

import android.os.IBinder;
import com.jaybox.core.entity.dump.DumpResult;

interface IBDumpManagerService {
    void registerMonitor(IBinder monitor);
    void unregisterMonitor(IBinder monitor);
    void noticeMonitor(in DumpResult result);
}