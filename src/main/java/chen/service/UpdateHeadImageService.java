package chen.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by chen on 16/3/23.
 */
public class UpdateHeadImageService extends Service {

    public static String ACTION_UPDATE_HEAD = "ACTION_UPDATE_HEAD";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent update = new Intent();
        update.setAction(ACTION_UPDATE_HEAD);
        sendBroadcast(update);
        return super.onStartCommand(intent, flags, startId);
    }
}
