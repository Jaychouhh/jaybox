package com.jaybox.core.utils.compat;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DexFile;
import com.jaybox.core.utils.Reflector;

/**
 * Created by Milk on 2021/5/16.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处無Bug
 */
public class DexFileCompat {
    public static final String TAG = "DexFileCompat";

    public static List<String> getClassNameList(ClassLoader classLoader) {
        List<String> allClass = new ArrayList<>();
        try {
            List<DexFile> dexFiles = getDexFiles(classLoader);
            for (DexFile dexFile : dexFiles) {
                Object object = getCookieField(dexFile);
                if (object == null) continue;
                String[] classNameList = getClassNameList(object);
                if (classNameList != null) {
                    allClass.addAll(Arrays.asList(classNameList));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allClass;
    }

    private static String[] getClassNameList(Object cookie) {
        try {
            String[] list;
            if (BuildCompat.isM()) {
                list = Reflector.on(DexFile.class)
                        .method("getClassNameList", Object.class)
                        .call(cookie);
            } else {
                list = Reflector.on(DexFile.class)
                        .method("getClassNameList", long.class)
                        .call(cookie);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Long> getCookies(ClassLoader classLoader) {
        List<Long> cookies = new ArrayList<>();
        List<DexFile> dexFiles = getDexFiles(classLoader);
        for (DexFile dexFile : dexFiles) {
            cookies.addAll(getCookies(dexFile));
        }
        return cookies;
    }

    public static List<Long> getCookies(DexFile dexFile) {
        List<Long> cookies = new ArrayList<>();
        if (dexFile == null)
            return cookies;
        try {
            Object object = getCookieField(dexFile);
            if (object == null) {
                Log.w(TAG, "Failed to get mCookie from DexFile");
                return cookies;
            }
            if (BuildCompat.isM()) {
                for (long l : (long[]) object) {
                    cookies.add(l);
                }
            } else {
                cookies.add((long) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cookies;
    }

    /**
     * Get the mCookie field from DexFile with fallback for Android 14+/15+/16+
     * where hidden API restrictions may block direct reflection access.
     */
    private static Object getCookieField(DexFile dexFile) {
        // Primary approach: use Reflector
        try {
            Object cookie = Reflector.with(dexFile)
                    .field("mCookie")
                    .get();
            if (cookie != null) {
                return cookie;
            }
        } catch (Exception ignored) {
        }

        // Fallback 1: ensure hidden API bypass is applied and retry
        if (!HiddenApiCompat.isExemptionApplied()) {
            HiddenApiCompat.tryBypassHiddenApi();
            try {
                Object cookie = Reflector.with(dexFile)
                        .field("mCookie")
                        .get();
                if (cookie != null) {
                    return cookie;
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback 2: direct Field access with setAccessible
        try {
            Field cookieField = DexFile.class.getDeclaredField("mCookie");
            cookieField.setAccessible(true);
            return cookieField.get(dexFile);
        } catch (Exception ignored) {
        }

        // Fallback 3: try mInternalCookie (used in some Android versions)
        try {
            Field cookieField = DexFile.class.getDeclaredField("mInternalCookie");
            cookieField.setAccessible(true);
            return cookieField.get(dexFile);
        } catch (Exception ignored) {
        }

        Log.e(TAG, "All approaches to get DexFile cookie failed");
        return null;
    }

    private static List<DexFile> getDexFiles(ClassLoader classLoader) {
        List<DexFile> dexFiles = new ArrayList<>();
        Object[] dexElements = getDexElements(classLoader);
        for (Object dexElement : dexElements) {
            try {
                DexFile dexFile = getDexFileFromElement(dexElement);
                if (dexFile != null) {
                    dexFiles.add(dexFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dexFiles;
    }

    /**
     * Get DexFile from a DexPathList.Element with fallback for newer Android versions.
     */
    private static DexFile getDexFileFromElement(Object dexElement) {
        // Primary: use Reflector
        try {
            DexFile result = Reflector.with(dexElement)
                    .field("dexFile")
                    .<DexFile>get();
            if (result != null) {
                return result;
            }
        } catch (Exception ignored) {
        }

        // Fallback: direct field access
        try {
            Field dexFileField = dexElement.getClass().getDeclaredField("dexFile");
            dexFileField.setAccessible(true);
            return (DexFile) dexFileField.get(dexElement);
        } catch (Exception ignored) {
        }

        return null;
    }

    private static Object[] getDexElements(ClassLoader classLoader) {
        Object dexPathList = getDexPathList(classLoader);
        if (dexPathList == null) {
            return new Object[]{};
        }
        // Primary: use Reflector
        try {
            Object[] elements = Reflector.with(dexPathList)
                    .field("dexElements")
                    .get();
            if (elements != null) {
                return elements;
            }
        } catch (Exception ignored) {
        }

        // Fallback: direct field access
        try {
            Field elementsField = dexPathList.getClass().getDeclaredField("dexElements");
            elementsField.setAccessible(true);
            return (Object[]) elementsField.get(dexPathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[]{};
    }

    private static Object getDexPathList(ClassLoader classLoader) {
        // Primary: use Reflector
        try {
            Object result = Reflector.on("dalvik.system.BaseDexClassLoader")
                    .field("pathList")
                    .get(classLoader);
            if (result != null) {
                return result;
            }
        } catch (Exception ignored) {
        }

        // Fallback: direct field access walking the class hierarchy
        try {
            Class<?> cls = classLoader.getClass();
            while (cls != null) {
                try {
                    Field pathListField = cls.getDeclaredField("pathList");
                    pathListField.setAccessible(true);
                    return pathListField.get(classLoader);
                } catch (NoSuchFieldException ignored) {
                }
                cls = cls.getSuperclass();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
