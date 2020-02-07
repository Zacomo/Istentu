package com.zacomo.istentu;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.zacomo.istentu.BaseApp.CHANNEL_1_ID;

public class AlertReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);
        createNotification(context, intent);
    }

    //metodo per la creazione di una notifica
    private void createNotification(Context context, Intent intent){

        //posizione task
        int position = intent.getIntExtra("position", -1);

        String title = "Hey!";
        String message = intent.getStringExtra("name") + context.getString(R.string.alertReceiver_notificationMessage);
        //Se falso, c'è stato un problema col passaggio della posizione del task
        if (position > -1) {

            //creo intent per aprire activity di rinvio task; conterrà info task da passare a PostponeTaskActivity
            Intent postponeActivityIntent = new Intent(context, PostponeTaskActivity.class);
            postponeActivityIntent.putExtra("position", position);
            postponeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            //wrapper per poter passare l'intent della postponeActivity alla notifica
            //position è l'id request
            PendingIntent postponeActionIntent = PendingIntent.getActivity(context,
                    position, postponeActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //intent per impostare task come "in corso"
            Intent setRunningIntent = new Intent(context, MainActivity.class);
            //setType serve per distinguere questo intent da quello di done, altrimenti sono considerati uguali
            //e l'intent definito dopo sovrascrive quello precedente, quando si usa un pendingIntent
            //https://stackoverflow.com/questions/21652895/pendingintent-from-second-action-overwrites-the-first-action-and-the-contentinte
            setRunningIntent.setType("runningIntent");
            setRunningIntent.putExtra("action","setRunning");
            setRunningIntent.putExtra("position", position);
            setRunningIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            //position è l'id request
            PendingIntent setRunningActionIntent = PendingIntent.getActivity(context,position,setRunningIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //intent per impostare task come "completato"
            Intent setDoneIntent = new Intent(context, MainActivity.class);
            setDoneIntent.setType("doneIntent");
            setDoneIntent.putExtra("action","setDone");
            setDoneIntent.putExtra("position",position);
            setDoneIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            //position è l'id request
            PendingIntent setDoneActionIntent = PendingIntent.getActivity(context, position, setDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Creazione notifica ed aggiunta degli intent creati
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_wb_incandescent_black_24dp)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_watch_later_black_24dp, context.getString(R.string.alertReceiver_notificationAction_postpone_text), postponeActionIntent)
                    .addAction(R.drawable.ic_autorenew_black_24dp, context.getString(R.string.alertReceiver_notificationAction_setAsRunning_text), setRunningActionIntent)
                    .addAction(R.drawable.ic_done_black_24dp, context.getString(R.string.alertReceiver_notificationAction_setAsDone_text), setDoneActionIntent)
                    .build();

            //mando effettivamente la notifica
            notificationManager.notify(position, notification);
        }
    }
}
