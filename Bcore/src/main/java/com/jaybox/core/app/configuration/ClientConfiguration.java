package com.jaybox.core.app.configuration;

import java.io.File;

import com.jaybox.core.BlackBoxCore;
import com.jaybox.core.utils.FileUtils;

/**
 * Created by Milk on 5/4/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public abstract class ClientConfiguration {
    private File mExternalDir;

    public final void init() {
        mExternalDir = BlackBoxCore.getContext().getExternalCacheDir().getParentFile();
    }

    public abstract String getHostPackageName();

    public String getDexDumpDir() {
        // Try multiple locations for Android 16 compatibility
        // 1. Try /data/local/tmp (accessible with root)
        File rootDump = new File("/data/local/tmp/jaybox/dump");
        if (rootDump.exists() || rootDump.mkdirs()) {
            return rootDump.getAbsolutePath();
        }
        // 2. Fallback to external storage
        File dump = new File(mExternalDir, "dump");
        FileUtils.mkdirs(dump);
        return dump.getAbsolutePath();
    }

    public boolean isFixCodeItem() {
        return false;
    }

    public boolean isEnableHookDump() {
        return true;
    }
}
