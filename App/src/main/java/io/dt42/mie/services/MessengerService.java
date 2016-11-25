package io.dt42.mie.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MessengerService extends Service {
    private static final String TAG = MessengerService.class.getSimpleName();
    private static final String HELLO = "hello!";
    private static final String BINDING = "binding";
    // Command to the service to display a message
    public static final int MSG_DEBUG= 1000;
    public static final int MSG_INITIALIZE_MANAGER = 1001;
    public static final int MSG_DEEPLEARNING_INFERENCE = 1002;
    // Target we publish for clients to send messages to IncomingHandler.
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(MessengerService.this, BINDING, Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    // Handler of incoming messages from clients.
    class IncomingHandler extends Handler {
        Messenger managerMessenger;
        Message reply;
        String inputImagePath;
        Map<String, String> inferenceResults;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DEBUG:
                    Toast.makeText(MessengerService.this, HELLO, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_INITIALIZE_MANAGER:
                    Log.d(TAG, "MSG_INITIALIZE_MANAGER");
                    reply = Message.obtain(null, MSG_INITIALIZE_MANAGER, 0, 0);
                    reply.obj = "Service gets the manager messenger";
                    managerMessenger = msg.replyTo;
                    try {
                        managerMessenger.send(reply);
                    } catch (Exception e) {
                        Log.d(TAG, "Can not get manager messenger");
                    }
                    break;
                case MSG_DEEPLEARNING_INFERENCE:
                    Log.d(TAG, "MSG_DEEPLEARNING_INFERENCE");
                    inputImagePath = (String) msg.obj;
                    Log.d(TAG, "Analyze input image " + inputImagePath);
                    inferenceResults = generateExampleResult();
                    reply = Message.obtain(null, MSG_DEEPLEARNING_INFERENCE, 0, 0);
                    reply.obj = inferenceResults;
                    try {
                        managerMessenger.send(reply);
                    } catch (Exception e) {
                        Log.d(TAG, "Can not get manager messenger");
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    static HashMap<String, String> generateExampleResult() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("apple", "0.8");
        result.put("banana", "0.15");
        result.put("cc", "0.05");
        return result;
    }
}
