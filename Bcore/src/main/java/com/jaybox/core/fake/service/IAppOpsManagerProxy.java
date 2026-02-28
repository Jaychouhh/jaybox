package com.jaybox.core.fake.service;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Method;

import reflection.android.os.ServiceManager;
import reflection.com.android.internal.app.IAppOpsService;
import com.jaybox.core.BlackBoxCore;
import com.jaybox.core.fake.hook.BinderInvocationStub;
import com.jaybox.core.fake.hook.MethodHook;
import com.jaybox.core.fake.hook.ProxyMethod;
import com.jaybox.core.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IAppOpsManagerProxy extends BinderInvocationStub {
    public IAppOpsManagerProxy() {
        super(ServiceManager.getService.call(Context.APP_OPS_SERVICE));
    }

    @Override
    protected Object getWho() {
        IBinder call = ServiceManager.getService.call(Context.APP_OPS_SERVICE);
        return IAppOpsService.Stub.asInterface.call(call);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        if (reflection.android.app.AppOpsManager.mService != null) {
            AppOpsManager appOpsManager = (AppOpsManager) BlackBoxCore.getContext().getSystemService(Context.APP_OPS_SERVICE);
            try {
                reflection.android.app.AppOpsManager.mService.set(appOpsManager, (IInterface) getProxyInvocation());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        replaceSystemService(Context.APP_OPS_SERVICE);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstAppPkg(args);
        MethodParameterUtils.replaceLastUserId(args);
        return super.invoke(proxy, method, args);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod(name = "checkPackage")
    public static class CheckPackage extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // todo
            return AppOpsManager.MODE_ALLOWED;
        }
    }

    @ProxyMethod(name = "checkPackage")
    public static class CheckOperation extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod(name = "noteOperation")
    public static class NoteOperation extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }
}
