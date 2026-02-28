package com.jaybox.core.core.system.pm.installer;

import com.jaybox.core.entity.pm.InstallOption;
import com.jaybox.core.core.system.pm.BPackageSettings;

/**
 * Created by Milk on 4/24/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public interface Executor {
    public static final String TAG = "InstallExecutor";

    int exec(BPackageSettings ps, InstallOption option, int userId);
}
