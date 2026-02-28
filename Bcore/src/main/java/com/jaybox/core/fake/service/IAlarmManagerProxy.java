package com.jaybox.core.fake.service;

import android.content.Context;

import java.lang.reflect.Method;

import reflection.android.app.IAlarmManager;
import reflection.android.os.ServiceManager;
import com.jaybox.core.fake.hook.BinderInvocationStub;
import com.jaybox.core.fake.hook.MethodHook;
import com.jaybox.core.fake.hook.ProxyMethod;
import com.jaybox.core.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/3/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IAlarmManagerProxy extends BinderInvocationStub {

    public IAlarmManagerProxy() {
        super(ServiceManager.getService.call(Context.ALARM_SERVICE));
    }

    @Override
    protected Object getWho() {
        return IAlarmManager.Stub.asInterface.call(ServiceManager.getService.call(Context.ALARM_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ALARM_SERVICE);
    }

    @ProxyMethod(name = "set")
    public static class Set extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
