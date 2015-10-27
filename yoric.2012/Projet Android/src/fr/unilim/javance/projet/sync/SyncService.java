package fr.unilim.javance.projet.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {
    private static final Object syncAdapterLock = new Object();

    private static SyncAdapter sa = null;

    @Override
    public void onCreate() {
        synchronized (this.syncAdapterLock) {
            if (this.sa == null) {
            	this.sa = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.sa.getSyncAdapterBinder();
    }
}
