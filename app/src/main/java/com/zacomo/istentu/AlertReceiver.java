package com.zacomo.istentu;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static com.zacomo.istentu.BaseApp.CHANNEL_1_ID;

public class AlertReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);
        //da intent prendere dati task da far comparire nel messaggio della notifica
        //creazione notifica qua
        createNotification(context, intent);
    }

    private void createNotification(Context context, Intent intent){

        //posizione task
        int position = intent.getIntExtra("position", -1);

        String title = "Hey!";
        String message = intent.getStringExtra("name") + " è scade oggi!";
        if (position > -1) {

            //creo intent per aprire activity di rinvio task; conterrà info task da passare a PostponeTaskActivity
            Intent activityIntent = new Intent(context, PostponeTaskActivity.class);
            activityIntent.putExtra("position", position);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //wrapper per poter passare l'intent alla notifica
            PendingIntent actionIntent = PendingIntent.getActivity(context,
                    0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_wb_incandescent_black_24dp)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_watch_later_black_24dp, "Rinvia", actionIntent)
                    .build();

            //mando effettivamente la notifica
            notificationManager.notify(position, notification);

            Toast.makeText(context, "NotificaRicevuta", Toast.LENGTH_SHORT).show();
        }
    }
}
