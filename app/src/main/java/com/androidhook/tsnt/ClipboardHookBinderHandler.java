package com.androidhook.tsnt;

import android.content.ClipData;
import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Joinwe on 2017/8/16.
 */

public class ClipboardHookBinderHandler implements InvocationHandler {
    private Object invoke;

    public ClipboardHookBinderHandler(IBinder realSubject,Class<?> stubClass ) {
        try {
            Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class);
             invoke = asInterfaceMethod.invoke(null, realSubject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        //每次从本应用复制的文本，后面都加上分享的出处
        if ("setPrimaryClip".equals(methodName)) {
            int argsLength = args.length;
            if (argsLength >= 2 && args[0] instanceof ClipData) {
                ClipData data = (ClipData) args[0];
                String text = data.getItemAt(0).getText().toString();
                text += "this is shared from ServiceHook-----by Shawn_Dut";
                args[0] = ClipData.newPlainText(data.getDescription().getLabel(), text);
            }
        }
        return method.invoke(invoke, args);
    }
}
