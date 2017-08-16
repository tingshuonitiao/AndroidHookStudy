package com.androidhook.tsnt;

import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Joinwe on 2017/8/16.
 */

public class IClipboardHookBinderHandler implements InvocationHandler {
    private IBinder realSubject;
    private Class iInterface;
    private Class stubClass;

    public IClipboardHookBinderHandler(IBinder realSubject) {
        this.realSubject = realSubject;
        try {
            this.stubClass = Class.forName("android.content.IClipboard$Stub");
            this.iInterface = Class.forName("android.content.IClipboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("queryLocalInterface".equals(method.getName())) {
            return Proxy.newProxyInstance(realSubject.getClass().getClassLoader(),
                    new Class[]{this.iInterface},
                    new ClipboardHookBinderHandler(realSubject,stubClass));
        }

        return method.invoke(realSubject, args);
    }
}
