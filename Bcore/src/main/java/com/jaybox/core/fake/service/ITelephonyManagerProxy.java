package com.jaybox.core.fake.service;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import reflection.android.os.ServiceManager;
import reflection.com.android.internal.telephony.ITelephony;
import com.jaybox.core.BlackBoxCore;
import com.jaybox.core.fake.hook.BinderInvocationStub;
import com.jaybox.core.fake.hook.MethodHook;
import com.jaybox.core.fake.hook.ProxyMethod;
import com.jaybox.core.utils.Md5Utils;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ITelephonyManagerProxy extends BinderInvocationStub {

    public ITelephonyManagerProxy() {
        super(ServiceManager.getService.call(Context.TELEPHONY_SERVICE));
    }

    @Override
    protected Object getWho() {
        IBinder telephony = ServiceManager.getService.call(Context.TELEPHONY_SERVICE);
        return ITelephony.Stub.asInterface.call(telephony);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod(name = "getDeviceId")
    public static class GetDeviceId extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod(name = "getImeiForSlot")
    public static class getImeiForSlot extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod(name = "isUserDataEnabled")
    public static class IsUserDataEnabled extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    @ProxyMethod(name = "getSubscriberId")
    public static class GetSubscriberId extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }
}
