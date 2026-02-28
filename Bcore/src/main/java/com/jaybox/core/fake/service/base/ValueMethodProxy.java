package com.jaybox.core.fake.service.base;

import java.lang.reflect.Method;

import com.jaybox.core.fake.hook.MethodHook;

public class ValueMethodProxy extends MethodHook {

	Object mValue;
	String mName;

	public ValueMethodProxy(String name, Object value) {
		mValue = value;
		mName = name;
	}

	public String getName() {
		return mName;
	}

	@Override
	protected Object hook(Object who, Method method, Object[] args) throws Throwable {
		return mValue;
	}
}
