package com.jaybox.core.utils.compat;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * Hidden API bypass for Android 9+ (API 28+).
 * Provides fallback mechanisms when the primary bypass (free_reflection) fails
 * on newer Android versions (14+/15+/16+).
 */
public class HiddenApiCompat {
    private static final String TAG = "HiddenApiCompat";
    private static boolean sExemptionApplied = false;

    /**
     * Attempt to bypass hidden API restrictions using multiple strategies.
     * Should be called early during initialization, after the primary bypass.
     */
    public static void tryBypassHiddenApi() {
        if (sExemptionApplied) {
            return;
        }
        if (!BuildCompat.isPie()) {
            // Hidden API restrictions only exist on Android 9+
            sExemptionApplied = true;
            return;
        }

        // On Android 15+/16+, setHiddenApiExemptions is completely blocked
        // and writing DEX files is not allowed. Skip the bypass attempts.
        if (BuildCompat.isV()) {
            Log.w(TAG, "Android 15+ detected - hidden API bypass disabled, using reflection fallbacks only");
            sExemptionApplied = false;
            return;
        }

        // Strategy 1: VMRuntime.setHiddenApiExemptions (Android 10+)
        if (trySetHiddenApiExemptions()) {
            sExemptionApplied = true;
            Log.d(TAG, "Hidden API bypass via setHiddenApiExemptions succeeded");
            return;
        }

        // Strategy 2: Double-reflection technique
        if (tryDoubleReflection()) {
            sExemptionApplied = true;
            Log.d(TAG, "Hidden API bypass via double-reflection succeeded");
            return;
        }

        // Strategy 3: Meta-reflection via getDeclaredMethod bootstrap
        if (tryMetaReflection()) {
            sExemptionApplied = true;
            Log.d(TAG, "Hidden API bypass via meta-reflection succeeded");
            return;
        }

        Log.w(TAG, "All hidden API bypass strategies failed");
    }

    /**
     * Uses VMRuntime.setHiddenApiExemptions to exempt all classes.
     * This is the most reliable approach when available.
     */
    private static boolean trySetHiddenApiExemptions() {
        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod(
                    "getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null,
                    "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass,
                    "getRuntime", null);
            Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass,
                    "setHiddenApiExemptions", new Class[]{String[].class});

            Object vmRuntime = getRuntime.invoke(null);
            setHiddenApiExemptions.invoke(vmRuntime, (Object) new String[]{"L"});
            return true;
        } catch (Throwable e) {
            Log.d(TAG, "setHiddenApiExemptions failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Uses the double-reflection technique: first use a public reflection API
     * to obtain a Method object for getDeclaredMethod itself, then use that
     * to access hidden APIs without triggering the restriction check.
     */
    private static boolean tryDoubleReflection() {
        try {
            // Get getDeclaredMethod via the public getMethod (which is not restricted)
            Method getDeclaredMethod = Class.class.getMethod("getDeclaredMethod",
                    String.class, Class[].class);

            // Now use it to get VMRuntime methods
            Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass,
                    "getRuntime", null);
            if (getRuntime == null) {
                return false;
            }

            Object vmRuntime = getRuntime.invoke(null);

            // Try setHiddenApiExemptions via the double-reflected method
            Method setExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass,
                    "setHiddenApiExemptions", new Class[]{String[].class});
            if (setExemptions != null) {
                setExemptions.invoke(vmRuntime, (Object) new String[]{"L"});
                return true;
            }
            return false;
        } catch (Throwable e) {
            Log.d(TAG, "double-reflection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Meta-reflection: use Class.getDeclaredMethod to get itself, then
     * use the resulting unrestricted Method to access hidden APIs.
     */
    private static boolean tryMetaReflection() {
        try {
            // Bootstrap: getDeclaredMethod is public, so calling it on itself works
            Method metaGetDeclaredMethod = Class.class.getDeclaredMethod(
                    "getDeclaredMethod", String.class, Class[].class);
            // Use it to get forName
            Method metaForName = (Method) metaGetDeclaredMethod.invoke(
                    Class.class, "forName", new Class[]{String.class, boolean.class, ClassLoader.class});
            if (metaForName == null) {
                return false;
            }

            Class<?> vmRuntimeClass = (Class<?>) metaForName.invoke(null,
                    "dalvik.system.VMRuntime", false, null);
            if (vmRuntimeClass == null) {
                return false;
            }

            Method getRuntime = (Method) metaGetDeclaredMethod.invoke(vmRuntimeClass,
                    "getRuntime", null);
            if (getRuntime == null) {
                return false;
            }

            Object runtime = getRuntime.invoke(null);

            Method setExemptions = (Method) metaGetDeclaredMethod.invoke(vmRuntimeClass,
                    "setHiddenApiExemptions", new Class[]{String[].class});
            if (setExemptions == null) {
                return false;
            }

            setExemptions.invoke(runtime, (Object) new String[]{"L"});
            return true;
        } catch (Throwable e) {
            Log.d(TAG, "meta-reflection failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean isExemptionApplied() {
        return sExemptionApplied;
    }
}
