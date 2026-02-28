package com.jaybox.core.fake.service;

import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import reflection.android.app.job.IJobScheduler;
import reflection.android.os.ServiceManager;
import com.jaybox.core.fake.hook.BinderInvocationStub;
import com.jaybox.core.fake.hook.MethodHook;
import com.jaybox.core.fake.hook.ProxyMethod;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IJobServiceProxy extends BinderInvocationStub {
    public static final String TAG = "JobServiceStub";

    public IJobServiceProxy() {
        super(ServiceManager.getService.call(Context.JOB_SCHEDULER_SERVICE));
    }

    @Override
    protected Object getWho() {
        IBinder jobScheduler = ServiceManager.getService.call("jobscheduler");
        return IJobScheduler.Stub.asInterface.call(jobScheduler);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @ProxyMethod(name = "schedule")
    public static class Schedule extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod(name = "cancel")
    public static class Cancel extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @ProxyMethod(name = "cancelAll")
    public static class CancelAll extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
