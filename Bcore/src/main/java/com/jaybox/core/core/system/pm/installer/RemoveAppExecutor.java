package com.jaybox.core.core.system.pm.installer;

import com.jaybox.core.core.env.BEnvironment;
import com.jaybox.core.entity.pm.InstallOption;
import com.jaybox.core.core.system.pm.BPackageSettings;
import com.jaybox.core.utils.FileUtils;

/**
 * Created by Milk on 4/27/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class RemoveAppExecutor implements Executor {
    @Override
    public int exec(BPackageSettings ps, InstallOption option, int userId) {
        FileUtils.deleteDir(BEnvironment.getAppDir(ps.pkg.packageName));
        return 0;
    }
}
