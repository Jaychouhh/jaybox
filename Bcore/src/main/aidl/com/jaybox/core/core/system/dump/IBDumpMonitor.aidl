package com.jaybox.core.core.system.dump;

import com.jaybox.core.entity.dump.DumpResult;

interface IBDumpMonitor {
    void onDump(in DumpResult result);
}