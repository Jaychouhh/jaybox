package com.jaybox.core.fake.service;
import com.jaybox.core.utils.SafeCast;

import android.os.IInterface;
import android.os.Process;
import android.os.storage.StorageVolume;

import java.lang.reflect.Method;

import reflection.android.os.ServiceManager;
import reflection.android.os.mount.IMountService;
import reflection.android.os.storage.IStorageManager;
import com.jaybox.core.BlackBoxCore;
import com.jaybox.core.app.BActivityThread;
import com.jaybox.core.fake.hook.BinderInvocationStub;
import com.jaybox.core.fake.hook.MethodHook;
import com.jaybox.core.fake.hook.ProxyMethod;
import com.jaybox.core.utils.compat.BuildCompat;

/**
 * Created by Milk on 4/10/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IStorageManagerProxy extends BinderInvocationStub {

    public IStorageManagerProxy() {
        super(ServiceManager.getService.call("mount"));
    }

    @Override
    protected Object getWho() {
        IInterface mount;
        if (BuildCompat.isOreo()) {
            mount = IStorageManager.Stub.asInterface.call(ServiceManager.getService.call("mount"));
        } else {
            mount = IMountService.Stub.asInterface.call(ServiceManager.getService.call("mount"));
        }
        return mount;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("mount");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod(name = "getVolumeList")
    public static class GetVolumeList extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args == null) {
                StorageVolume[] volumeList = BlackBoxCore.getBStorageManager().getVolumeList(Process.myUid(), null, 0, BActivityThread.getUserId());
                if (volumeList == null) {
                    return method.invoke(who, args);
                }
                return volumeList;
            }
            try {
                int uid = SafeCast.toInt(args[0];
                String packageName = (String) args[1];
                int flags = SafeCast.toInt(args[2];
                StorageVolume[] volumeList = BlackBoxCore.getBStorageManager().getVolumeList(uid, packageName, flags, BActivityThread.getUserId());
                if (volumeList == null) {
                    return method.invoke(who, args);
                }
            } catch (Throwable t) {
                return method.invoke(who, args);
            }
            return method.invoke(who, args);
        }
    }
}
