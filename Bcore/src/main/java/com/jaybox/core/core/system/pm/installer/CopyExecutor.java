package com.jaybox.core.core.system.pm.installer;


import android.content.pm.ApplicationInfo;

import java.io.File;
import java.io.IOException;

import com.jaybox.core.BlackBoxCore;
import com.jaybox.core.core.env.BEnvironment;
import com.jaybox.core.entity.pm.InstallOption;
import com.jaybox.core.core.system.pm.BPackageSettings;
import com.jaybox.core.utils.FileUtils;
import com.jaybox.core.utils.NativeUtils;

/**
 * Created by Milk on 4/24/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 * 拷贝文件相关
 */
public class CopyExecutor implements Executor {

    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        try {
            NativeUtils.copyNativeLib(new File(ps.pkg.baseCodePath), BEnvironment.getAppLibDir(ps.pkg.packageName));
            ApplicationInfo applicationInfo = BlackBoxCore.getContext().getApplicationInfo();
            FileUtils.copyFile(new File(applicationInfo.nativeLibraryDir, "libjaybox.so"),
                    new File(BEnvironment.getAppLibDir(ps.pkg.packageName), "libjaybox.so"));

            FileUtils.copyFile(new File(applicationInfo.nativeLibraryDir, "libjaybox_d.so"),
                    new File(BEnvironment.getAppLibDir(ps.pkg.packageName), "libjaybox_d.so"));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        if (option.isFlag(InstallOption.FLAG_STORAGE)) {
            // 外部安装
            File origFile = new File(ps.pkg.baseCodePath);
            File newFile = BEnvironment.getBaseApkDir(ps.pkg.packageName);
            try {
                if (option.isFlag(InstallOption.FLAG_URI_FILE)) {
                    boolean b = FileUtils.renameTo(origFile, newFile);
                    if (!b) {
                        FileUtils.copyFile(origFile, newFile);
                    }
                } else {
                    FileUtils.copyFile(origFile, newFile);
                }
                // update baseCodePath
                ps.pkg.baseCodePath = newFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        } else if (option.isFlag(InstallOption.FLAG_SYSTEM)) {
            // 系统安装
        }
        return 0;
    }
}
