package com.jaybox.core;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;

import com.jaybox.core.app.configuration.ClientConfiguration;
import com.jaybox.core.fake.delegate.ContentProviderDelegate;
import com.jaybox.core.fake.frameworks.BDumpManager;
import com.jaybox.core.proxy.ProxyManifest;
import com.jaybox.core.app.configuration.AppLifecycleCallback;
import com.jaybox.core.fake.hook.HookManager;
import com.jaybox.core.entity.pm.InstallOption;
import com.jaybox.core.entity.pm.InstallResult;
import com.jaybox.core.core.system.DaemonService;
import com.jaybox.core.utils.FileUtils;
import com.jaybox.core.utils.ShellUtils;
import com.jaybox.core.utils.compat.BuildCompat;
import com.jaybox.core.utils.compat.BundleCompat;
import com.jaybox.core.utils.compat.HiddenApiCompat;
import com.jaybox.core.utils.provider.ProviderCall;
import com.jaybox.core.fake.frameworks.BActivityManager;
import com.jaybox.core.fake.frameworks.BPackageManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.weishu.reflection.Reflection;
import reflection.android.app.ActivityThread;
import com.jaybox.core.fake.frameworks.BStorageManager;
import com.jaybox.core.core.system.ServiceManager;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@SuppressLint("StaticFieldLeak")
public class BlackBoxCore extends ClientConfiguration {
    public static final String TAG = "BlackBoxCore";
    public static final int USER_ID = 0;

    private static final BlackBoxCore sBlackBoxCore = new BlackBoxCore();
    private static Context sContext;
    private ProcessType mProcessType;
    private final Map<String, IBinder> mServices = new HashMap<>();
    private ClientConfiguration mClientConfiguration;
    private AppLifecycleCallback mAppLifecycleCallback = AppLifecycleCallback.EMPTY;

    public static BlackBoxCore get() {
        return sBlackBoxCore;
    }

    public static PackageManager getPackageManager() {
        return sContext.getPackageManager();
    }

    public static String getHostPkg() {
        return get().getHostPackageName();
    }

    public static Context getContext() {
        return sContext;
    }

    public void doAttachBaseContext(Context context, ClientConfiguration clientConfiguration) {
        if (clientConfiguration == null) {
            throw new IllegalArgumentException("ClientConfiguration is null!");
        }
        Reflection.unseal(context);
        // Additional hidden API bypass for Android 14+/15+/16+ where free_reflection may fail
        HiddenApiCompat.tryBypassHiddenApi();
        sContext = context;
        mClientConfiguration = clientConfiguration;
        mClientConfiguration.init();
        String processName = getProcessName(getContext());
        if (processName.equals(BlackBoxCore.getHostPkg())) {
            mProcessType = ProcessType.Main;
            startLogcat();
        } else if (processName.endsWith(getContext().getString(R.string.black_box_service_name))) {
            mProcessType = ProcessType.Server;
        } else {
            mProcessType = ProcessType.BAppClient;
        }
        if (BlackBoxCore.get().isVirtualProcess()) {
            if (processName.endsWith("p0")) {
//                android.os.Debug.waitForDebugger();
            }
//            android.os.Debug.waitForDebugger();
        }
        if (isServerProcess()) {
//            Intent intent = new Intent();
//            intent.setClass(getContext(), DaemonService.class);
//            if (BuildCompat.isOreo()) {
//                getContext().startForegroundService(intent);
//            } else {
//                getContext().startService(intent);
//            }
        }
        HookManager.get().init();
    }

    public void doCreate() {
        if (isVirtualProcess()) {
            ContentProviderDelegate.init();
        }
        if (!isServerProcess()) {
            initService();
        }
    }

    private void initService() {
        get().getService(ServiceManager.ACTIVITY_MANAGER);
        get().getService(ServiceManager.PACKAGE_MANAGER);
        get().getService(ServiceManager.STORAGE_MANAGER);
        get().getService(ServiceManager.DUMP_MANAGER);
    }

    public static Object mainThread() {
        return ActivityThread.currentActivityThread.call();
    }

    public void startActivity(Intent intent, int userId) {
        getBActivityManager().startActivity(intent, userId);
    }

    public static BPackageManager getBPackageManager() {
        return BPackageManager.get();
    }

    public static BActivityManager getBActivityManager() {
        return BActivityManager.get();
    }

    public static BStorageManager getBStorageManager() {
        return BStorageManager.get();
    }

    public static BDumpManager getBDumpManager() {
        return BDumpManager.get();
    }

    public boolean launchApk(String packageName) {
        Intent launchIntentForPackage = getBPackageManager().getLaunchIntentForPackage(packageName, USER_ID);
        if (launchIntentForPackage == null) {
            return false;
        }
        startActivity(launchIntentForPackage, USER_ID);
        return true;
    }

    public boolean isInstalled(String packageName) {
        return getBPackageManager().isInstalled(packageName, USER_ID);
    }

    public void uninstallPackage(String packageName) {
        getBPackageManager().uninstallPackageAsUser(packageName, USER_ID);
    }

    public InstallResult installPackage(String packageName) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            return getBPackageManager().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), USER_ID);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new InstallResult().installError(e.getMessage());
        }
    }

    public InstallResult installPackage(File apk) {
        return getBPackageManager().installPackageAsUser(apk.getAbsolutePath(), InstallOption.installByStorage(), USER_ID);
    }

    public InstallResult installPackage(Uri apk) {
        return getBPackageManager().installPackageAsUser(apk.toString(), InstallOption.installByStorage().makeUriFile(), USER_ID);
    }

    public AppLifecycleCallback getAppLifecycleCallback() {
        return mAppLifecycleCallback;
    }

    public void setAppLifecycleCallback(AppLifecycleCallback appLifecycleCallback) {
        if (appLifecycleCallback == null) {
            throw new IllegalArgumentException("AppLifecycleCallback is null!");
        }
        mAppLifecycleCallback = appLifecycleCallback;
    }

    public IBinder getService(String name) {
        IBinder binder = mServices.get(name);
        if (binder != null && binder.isBinderAlive()) {
            return binder;
        }
        Bundle bundle = new Bundle();
        bundle.putString("_VM_|_server_name_", name);
        Bundle vm = ProviderCall.callSafely(ProxyManifest.getBindProvider(), "VM", null, bundle);
        assert vm != null;
        binder = BundleCompat.getBinder(vm, "_VM_|_server_");
        mServices.put(name, binder);
        return binder;
    }

    private enum ProcessType {
        Server,
        BAppClient,
        Main,
    }

    public boolean isVirtualProcess() {
        return mProcessType == ProcessType.BAppClient;
    }

    public boolean isMainProcess() {
        return mProcessType == ProcessType.Main;
    }

    public boolean isServerProcess() {
        return mProcessType == ProcessType.Server;
    }

    @Override
    public String getHostPackageName() {
        return mClientConfiguration.getHostPackageName();
    }

    @Override
    public String getDexDumpDir() {
        return mClientConfiguration.getDexDumpDir();
    }

    @Override
    public boolean isFixCodeItem() {
        return mClientConfiguration.isFixCodeItem();
    }

    @Override
    public boolean isEnableHookDump() {
        return mClientConfiguration.isEnableHookDump();
    }

    private void startLogcat() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getContext().getPackageName() + "_logcat.txt");
        FileUtils.deleteDir(file);
        ShellUtils.execCommand("logcat -c", false);
        ShellUtils.execCommand("logcat >> " + file.getAbsolutePath() + " &", false);
    }

    private static String getProcessName(Context context) {
        int pid = Process.myPid();
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (info.pid == pid) {
                processName = info.processName;
                break;
            }
        }
        if (processName == null) {
            throw new RuntimeException("processName = null");
        }
        return processName;
    }

    public static boolean is64Bit() {
        if (BuildCompat.isM()) {
            return Process.is64Bit();
        } else {
            return Build.CPU_ABI.equals("arm64-v8a");
        }
    }
}
