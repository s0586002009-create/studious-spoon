package com.studious.spoon;
import android.app.*;import android.content.*;import android.os.Build;
public class ReminderReceiver extends BroadcastReceiver{
 public void onReceive(Context c,Intent i){
  android.content.SharedPreferences p=c.getSharedPreferences("study_prefs",Context.MODE_PRIVATE);
  String text=p.getString("reminder","זמן ללמוד! 📚");
  NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel("study","תזכורות לימוד",NotificationManager.IMPORTANCE_DEFAULT));
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,"study"):new Notification.Builder(c);
  b.setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("מסלול לימוד").setContentText(text).setAutoCancel(true);
  nm.notify(101,b.build());
 }
}
