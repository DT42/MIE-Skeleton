package com.example.app.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

import com.example.app.ActivityMessenger;
import com.example.app.services.MessengerService;

public class ServiceManager {
    private static final String TAG = ServiceManager.class.getSimpleName();
    private Class<? extends MessengerService> mServiceClass;
    private Context mActivity;
    Messenger mServiceMessenger = null;
    boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            mServiceMessenger = new Messenger(iBinder);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mServiceMessenger = null;
            mBound = false;
        }
    };
    final Messenger mManagerMessenger = new Messenger(new ServiceManager.IncomingHandler());

    public ServiceManager(Context context, Class<? extends MessengerService> serviceClass) {
        this.mActivity = context;
        this.mServiceClass = serviceClass;
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerService.MSG_SAY_HELLO:
                    Log.d(TAG, "MSG_SAY_HELLO response");
                    break;
                case MessengerService.MSG_INITIALIZE_MANAGER:
                    Log.d(TAG, "MSG_INITIALIZE_MANAGER response" + (String) msg.obj);
                    break;
                case MessengerService.MSG_DEEPLEARNING_INFERENCE:
                    Log.d(TAG, "MSG_DEEPLEARNING_INFERENCE response");
                    printMap((Map) msg.obj);
                    processResult();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void bindService() {
        mActivity.bindService(new Intent(mActivity, mServiceClass), mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    public void unbindService() {
        mActivity.unbindService(mConnection);
        mBound = false;
    }

    public boolean isBinding() {
        return mBound;
    }

    public void setupMessenger(int messageType) throws RemoteException {
        Message msg = Message.obtain(null, messageType, 0, 0);
        msg.replyTo = mManagerMessenger;
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void inference(int messageType, String inputImagePath) throws RemoteException {
        Message msg = Message.obtain(null, messageType, 0, 0);
        msg.obj = inputImagePath;
        msg.replyTo = mManagerMessenger;
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void processResult() {
        Log.d(TAG, "ServiceManager's processResult");
    }

    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Log.d(TAG, pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
