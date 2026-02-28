package com.jaybox.core.core.system.pm.installer;

import com.jaybox.core.core.env.BEnvironment;
import com.jaybox.core.entity.pm.InstallOption;
import com.jaybox.core.core.system.pm.BPackageSettings;
import com.jaybox.core.core.system.pm.BPackageUserState;
import com.jaybox.core.utils.FileUtils;

/**
 * Created by Milk on 4/24/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 * 创建用户相关
 */
public class RemoveUserExecutor implements Executor {

    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        String packageName = ps.pkg.packageName;
        // delete user dir
        FileUtils.deleteDir(BEnvironment.getDataDir(packageName, userId));
        FileUtils.deleteDir(BEnvironment.getDeDataDir(packageName, userId));
        FileUtils.deleteDir(BEnvironment.getExternalDataDir(packageName, userId));
        return 0;
    }
}
