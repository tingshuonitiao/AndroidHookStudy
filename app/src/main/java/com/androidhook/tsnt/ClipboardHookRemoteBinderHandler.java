package com.androidhook.tsnt;

import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ting说你跳 on 2017/8/16.
 */

public class ClipboardHookRemoteBinderHandler implements InvocationHandler {
    private IBinder remoteBinder;
    private Class   iInterface;
    private Class   stubClass;

    public ClipboardHookRemoteBinderHandler(IBinder remoteBinder) {
        this.remoteBinder = remoteBinder;
        try {
            this.iInterface = Class.forName("android.content.IClipboard");
            this.stubClass = Class.forName("android.content.IClipboard$Stub");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("queryLocalInterface".equals(method.getName())) {
            return Proxy.newProxyInstance(remoteBinder.getClass().getClassLoader(),
                    new Class[]{this.iInterface},
                    new ClipboardHookLocalBinderHandler(remoteBinder,stubClass));
        }

        return method.invoke(remoteBinder, args);
    }
}
