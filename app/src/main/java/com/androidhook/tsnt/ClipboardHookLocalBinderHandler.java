package com.androidhook.tsnt;

import android.content.ClipData;
import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by ting说你跳 on 2017/8/16.
 */

public class ClipboardHookLocalBinderHandler implements InvocationHandler {
    private Object localProxyBinder;

    public ClipboardHookLocalBinderHandler(IBinder remoteBinder, Class<?> stubClass) {
        try {
            Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class);
            localProxyBinder = asInterfaceMethod.invoke(null, remoteBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("setPrimaryClip".equals(methodName)) {
            int argsLength = args.length;
            if (argsLength >= 2 && args[0] instanceof ClipData) {
                ClipData data = (ClipData) args[0];
                String text = data.getItemAt(0).getText().toString();
                text += "   -- this is shared from ClipboardHookService by ting说你跳";
                args[0] = ClipData.newPlainText(data.getDescription().getLabel(), text);
            }
        }

        return method.invoke(localProxyBinder, args);
    }
}
