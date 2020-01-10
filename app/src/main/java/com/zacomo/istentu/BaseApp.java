package com.zacomo.istentu;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

//questa classe è una sorta di wrapper per tutta l'applicazione
public class BaseApp extends Application {

    //Canali notifiche qui
    public static final String CHANNEL_1_ID = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels(){
        //i canali per le notifiche funzionano solo con api >= 26 (O = Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
              CHANNEL_1_ID,
                    "Notifiche Task in scadenza",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Task in scadenza");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }
}
