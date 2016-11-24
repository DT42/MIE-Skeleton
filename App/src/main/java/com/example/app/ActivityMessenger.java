package com.example.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.app.services.MessengerService;
import com.example.app.services.ServiceManager;

public class ActivityMessenger extends ActionBarActivity {
    private static final String TAG = ActivityMessenger.class.getSimpleName();
    private ServiceManager serviceManager = new ServiceManager(this, MessengerService.class) {
        /* DL result callback */
        public void processResult() {
            Log.d(TAG, "Activity's processResult");
        }
    };

    /* Service methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

        serviceManager.bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (serviceManager.isBinding() == true) {
            serviceManager.unbindService();
        }
    }

    /* GUI methods */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_messenger, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isActionSettings = item.getItemId() == R.id.action_settings;
        return isActionSettings || super.onOptionsItemSelected(item);
    }

    public void onHelloButton(View view) {
        if (serviceManager.isBinding() == true) {
            try {
                serviceManager.setupMessenger(MessengerService.MSG_INITIALIZE_MANAGER);
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.d(TAG, "Service manager is not binding any service");
        }
    }

    public void onInferenceButton(View view) {
        if (serviceManager.isBinding() == true) {
            try {
                serviceManager.inference(MessengerService.MSG_DEEPLEARNING_INFERENCE, "/path/to/inputimage");
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.d(TAG, "Service manager is not binding any service");
        }
    }

    // A placeholder fragment containing a simple view.
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_activity_messenger, container, false);
        }
    }
}
