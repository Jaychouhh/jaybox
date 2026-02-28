package com.jaybox.core.fake.hook;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import com.jaybox.core.fake.service.IActivityManagerProxy;
import com.jaybox.core.fake.service.IActivityTaskManagerProxy;
import com.jaybox.core.fake.service.HCallbackProxy;
import com.jaybox.core.fake.service.IAlarmManagerProxy;
import com.jaybox.core.fake.service.IAppOpsManagerProxy;
import com.jaybox.core.fake.service.IJobServiceProxy;
import com.jaybox.core.fake.service.ITelephonyRegistryProxy;
import com.jaybox.core.fake.service.IDeviceIdentifiersPolicyProxy;
import com.jaybox.core.fake.service.IStorageManagerProxy;
import com.jaybox.core.fake.service.ILauncherAppsProxy;
import com.jaybox.core.fake.service.IPackageManagerProxy;
import com.jaybox.core.BlackBoxCore;
import com.jaybox.core.fake.delegate.AppInstrumentation;
import com.jaybox.core.fake.service.libcore.OsStub;
import com.jaybox.core.fake.service.ITelephonyManagerProxy;
import com.jaybox.core.utils.compat.BuildCompat;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class HookManager {
    public static final String TAG = "HookManager";

    private static final HookManager sHookManager = new HookManager();

    private final Map<Class<?>, IInjectHook> mInjectors = new HashMap<>();

    public static HookManager get() {
        return sHookManager;
    }

    public void init() {
        if (BlackBoxCore.get().isVirtualProcess()) {
            addInjector(new OsStub());
            addInjector(new IActivityManagerProxy());
            addInjector(new IPackageManagerProxy());
            addInjector(new ITelephonyManagerProxy());
            addInjector(new HCallbackProxy());
            addInjector(new IAppOpsManagerProxy());
            addInjector(new IAlarmManagerProxy());
            addInjector(new IStorageManagerProxy());
            addInjector(new ILauncherAppsProxy());
            addInjector(new IJobServiceProxy());
            addInjector(new ITelephonyRegistryProxy());

            addInjector(AppInstrumentation.get());

            // 11.0
            if (BuildCompat.isR()) {
            }
            // 10.0
            if (BuildCompat.isQ()) {
                addInjector(new IActivityTaskManagerProxy());
            }
            // 9.0
            if (BuildCompat.isPie()) {
            }
            // 8.0
            if (BuildCompat.isOreo()) {
                addInjector(new IDeviceIdentifiersPolicyProxy());
            }
            // 7.1
            if (BuildCompat.isN_MR1()) {
            }
            // 7.0
            if (BuildCompat.isN()) {
            }
            // 6.0
            if (BuildCompat.isM()) {
            }
            // 5.0
            if (BuildCompat.isL()) {
                addInjector(new IJobServiceProxy());
            }
        }
        injectAll();
    }

    public void checkEnv(Class<?> clazz) {
        IInjectHook iInjectHook = mInjectors.get(clazz);
        if (iInjectHook != null && iInjectHook.isBadEnv()) {
            Log.d(TAG, "checkEnv: " + clazz.getSimpleName() + " is bad env");
            iInjectHook.injectHook();
        }
    }

    void addInjector(IInjectHook injectHook) {
        mInjectors.put(injectHook.getClass(), injectHook);
    }

    void injectAll() {
        for (IInjectHook value : mInjectors.values()) {
            try {
                value.injectHook();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
