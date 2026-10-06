package com.studious.spoon;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    static final String PREF="study_prefs", DED="dedication";
    SharedPreferences p; LinearLayout root, content; TextView dedicationView;
    String dedication; ArrayList<String> selected=new ArrayList<>();
    static final String[][] DATA={
      {"זרעים","ברכות|9","פאה|8","דמאי|7","כלאיים|9","שביעית|10","תרומות|11","מעשרות|5","מעשר שני|5","חלה|4","ערלה|3","בכורים|4"},
      {"מועד","שבת|24","עירובין|10","פסחים|10","שקלים|8","יומא|8","סוכה|5","ביצה|5","ראש השנה|4","תענית|4","מגילה|4","מועד קטן|3","חגיגה|3"},
      {"נשים","יבמות|16","כתובות|13","נדרים|11","נזיר|9","סוטה|9","גיטין|9","קידושין|4"},
      {"נזיקין","בבא קמא|10","בבא מציעא|10","בבא בתרא|10","סנהדרין|11","מכות|3","שבועות|8","עדיות|8","עבודה זרה|5","אבות|6","הוריות|3"},
      {"קדשים","זבחים|14","מנחות|13","חולין|12","בכורות|9","ערכין|9","תמורה|7","כריתות|6","מעילה|6","תמיד|7","מדות|5","קנים|3"},
      {"טהרות","כלים|30","אהלות|18","נגעים|14","פרה|12","טהרות|10","מקואות|10","נדה|10","מכשירין|6","זבים|5","טבול יום|4","ידים|4","עוקצים|3"}
    };

    @Override public void onCreate(Bundle b){super.onCreate(b); p=getSharedPreferences(PREF,0); dedication=p.getString(DED,"הלימוד לעילוי נשמת ר' מאיר משה בן ר' בן ציון הלוי ומינקה בת ר' משה שמואל ע"ה"); buildShell(); if(!p.getBoolean("setup",false)) showSetup(); else showHome();}
    int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,float size){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(Color.rgb(35,32,40));v.setGravity(Gravity.RIGHT);v.setFontFeatureSettings("kern");return v;}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);return b;}
    void buildShell(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.rgb(250,247,255));setContentView(root);dedicationView=tv(dedication,15);dedicationView.setTextColor(Color.WHITE);dedicationView.setPadding(dp(18),dp(14),dp(18),dp(14));dedicationView.setBackgroundResource(com.studious.spoon.R.drawable.bg_primary);root.addView(dedicationView,new LinearLayout.LayoutParams(-1,-2));content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(14),dp(14),dp(14),dp(8));ScrollView sc=new ScrollView(this);sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));}
    void clear(){content.removeAllViews();}
    TextView title(String s){TextView v=tv(s,25);v.setTypeface(null,1);v.setPadding(0,dp(8),0,dp(10));return v;}
    void card(View v){v.setBackgroundResource(R.drawable.bg_card);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(6),0,dp(6));content.addView(v,lp);}
    int totalSelected(){int n=0;for(String s:selected){String[] x=s.split("\|");n+=Integer.parseInt(x[1]);}return n;}
    void loadSelected(){selected.clear();String q=p.getString("selected","");if(!q.isEmpty())selected.addAll(Arrays.asList(q.split(";;")));}
    void saveSelected(){StringBuilder s=new StringBuilder();for(String x:selected){if(s.length()>0)s.append(";;");s.append(x);}p.edit().putString("selected",s.toString()).apply();}

    void showSetup(){clear();content.addView(title("מתחילים את המסלול שלך")); TextView intro=tv("בחר את המסכתות שאתה לומד, קבע יעד וקצב. הכל נשמר במכשיר בלבד.",16);intro.setPadding(0,0,0,dp(10));content.addView(intro); 
      LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL); final ArrayList<CheckBox> boxes=new ArrayList<>();
      for(String[] order:DATA){TextView h=tv(order[0],19);h.setTypeface(null,1);list.addView(h);for(int i=1;i<order.length;i++){String[] x=order[i].split("\|");CheckBox c=new CheckBox(this);c.setText(x[0]+" ("+x[1]+" פרקים)");c.setTextSize(16);c.setGravity(Gravity.RIGHT);c.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);c.setTag(order[0]+"|"+x[0]+"|"+x[1]);boxes.add(c);list.addView(c);}} card(list);
      EditText date=new EditText(this);date.setHint("תאריך יעד, למשל 31/12/2026");date.setText(p.getString("target",""));date.setInputType(2);card(date);
      EditText daily=new EditText(this);daily.setHint("כמה פרקים ביום?");daily.setInputType(2);daily.setText(String.valueOf(p.getInt("daily",3)));card(daily);
      Button save=btn("שמור והתחל 🚀");save.setTextSize(17);save.setOnClickListener(v->{selected.clear();for(CheckBox c:boxes)if(c.isChecked()){String[] z=c.getTag().toString().split("\|");selected.add(z[0]+"|"+z[1]+"|"+z[2]);}if(selected.isEmpty()){Toast.makeText(this,"בחר לפחות מסכת אחת",Toast.LENGTH_SHORT).show();return;}int d;try{d=Integer.parseInt(daily.getText().toString());}catch(Exception e){d=0;}if(d<1){Toast.makeText(this,"הזן קצב של לפחות פרק אחד",Toast.LENGTH_SHORT).show();return;}String ds=date.getText().toString().trim();if(ds.length()<8){Toast.makeText(this,"הזן תאריך יעד",Toast.LENGTH_SHORT).show();return;}p.edit().putBoolean("setup",true).putInt("daily",d).putString("target",ds).apply();saveSelected();scheduleReminder();showHome();});card(save);
    }

    void showHome(){loadSelected();clear();content.addView(title("המעקב שלי"));int total=totalSelected(), done=countDone(), left=Math.max(0,total-done);int daily=p.getInt("daily",1);
      LinearLayout hero=new LinearLayout(this);hero.setOrientation(LinearLayout.VERTICAL);TextView a=tv(statusText(total,done,left,daily),19);a.setTextColor(Color.WHITE);a.setTypeface(null,1);hero.addView(a);TextView b=tv(done+" מתוך "+total+" פרקים • "+(total==0?0:(done*100/total))+"%",16);b.setTextColor(Color.WHITE);hero.addView(b);hero.setPadding(dp(20),dp(18),dp(20),dp(18));hero.setBackgroundResource(R.drawable.bg_primary);card(hero);
      TextView st=tv("🔥 רצף: "+streak()+" ימים",18);st.setTypeface(null,1);card(st);
      Button learn=btn("📚 המשך לימוד");learn.setOnClickListener(v->showChapters());card(learn);
      Button stats=btn("📊 סטטיסטיקות והישגים");stats.setOnClickListener(v->showStats());card(stats);
      Button settings=btn("⚙️ הגדרות");settings.setOnClickListener(v->showSettings());card(settings);
    }
    String statusText(int total,int done,int left,int daily){try{DateFormat f=new SimpleDateFormat("dd/MM/yyyy",Locale.US);Date target=f.parse(p.getString("target",""));long days=(target.getTime()-strip(new Date()).getTime())/86400000L+1;int req=(int)Math.ceil((double)left/Math.max(1,days));long finish=strip(new Date()).getTime()+(long)Math.max(0,((long)Math.ceil((double)left/daily)-1))*86400000L;String fd=f.format(new Date(finish));if(req<=daily)return "אתה בקצב מצוין! נשארו "+left+" פרקים";return "צריך "+req+" פרקים ביום כדי להגיע ליעד";}catch(Exception e){return "מוכן ללימוד — בוא נתקדם!";}}
    Date strip(Date d){Calendar c=Calendar.getInstance();c.setTime(d);c.set(Calendar.HOUR_OF_DAY,0);c.set(Calendar.MINUTE,0);c.set(Calendar.SECOND,0);c.set(Calendar.MILLISECOND,0);return c.getTime();}
    void showChapters(){loadSelected();clear();content.addView(title("פרקי הלימוד"));TextView hint=tv("לחיצה על ✓ מסמנת את הפרק כנלמד.",15);content.addView(hint);for(String s:selected){String[] z=s.split("\|");String tract=z[1], order=z[0];int n=Integer.parseInt(z[2]);TextView h=tv(order+" › "+tract,19);h.setTypeface(null,1);content.addView(h);for(int i=1;i<=n;i++){String key=order+"_"+tract+"_"+i;CheckBox c=new CheckBox(this);c.setText("פרק "+i);c.setTextSize(16);c.setGravity(Gravity.RIGHT);c.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);c.setChecked(p.getBoolean("done_"+key,false));c.setOnClickListener(v->{p.edit().putBoolean("done_"+key,c.isChecked()).putString("date_"+key,new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date())).apply();});content.addView(c);}}Button back=btn("← חזרה");back.setOnClickListener(v->showHome());content.addView(back);}
    int countDone(){loadSelected();int n=0;for(String s:selected){String[] z=s.split("\|");for(int i=1;i<=Integer.parseInt(z[2]);i++)if(p.getBoolean("done_"+z[0]+"_"+z[1]+"_"+i,false))n++;}return n;}
    int streak(){HashSet<String> days=new HashSet<>();loadSelected();for(String s:selected){String[] z=s.split("\|");for(int i=1;i<=Integer.parseInt(z[2]);i++){String k="date_"+z[0]+"_"+z[1]+"_"+i;if(p.contains(k))days.add(p.getString(k,""));}}Calendar c=Calendar.getInstance();int n=0;while(days.contains(new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(c.getTime()))){n++;c.add(Calendar.DATE,-1);}return n;}
    void showStats(){clear();content.addView(title("סטטיסטיקות"));int total=totalSelected(),done=countDone();TextView t=tv("השלמת "+done+" מתוך "+total+" פרקים ("+(total==0?0:done*100/total)+"%)",20);card(t);String[] wd={"א","ב","ג","ד","ה","ו","ש"};for(int i=0;i<7;i++){Calendar c=Calendar.getInstance();c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);c.add(Calendar.DATE,i);String d=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(c.getTime());int n=countDay(d);TextView v=tv(wd[i]+"  "+bar(n)+"  "+n,16);card(v);}TextView badges=tv("🏅 הישגים\n"+(streak()>=7?"✓ 7 ימי רצף\n":"")+"✓ "+(done>0?"התחלת את המסלול":"השלם פרק ראשון"),17);card(badges);Button exp=btn("📄 ייצוא PDF");exp.setOnClickListener(v->exportPdf());card(exp);Button back=btn("← חזרה");back.setOnClickListener(v->showHome());content.addView(back);}
    String bar(int n){StringBuilder s=new StringBuilder();for(int i=0;i<Math.min(n,20);i++)s.append("▮");return s.toString();}
    int countDay(String day){int n=0;loadSelected();for(String s:selected){String[] z=s.split("\|");for(int i=1;i<=Integer.parseInt(z[2]);i++)if(day.equals(p.getString("date_"+z[0]+"_"+z[1]+"_"+i,"")))n++;}return n;}
    void showSettings(){clear();content.addView(title("הגדרות"));EditText d=new EditText(this);d.setText(p.getString(DED,dedication));d.setHint("הקדשה");d.setMinLines(3);card(d);Button save=btn("שמור הקדשה");save.setOnClickListener(v->{p.edit().putString(DED,d.getText().toString()).apply();dedication=d.getText().toString();dedicationView.setText(dedication);Toast.makeText(this,"נשמר",Toast.LENGTH_SHORT).show();});card(save);EditText msg=new EditText(this);msg.setText(p.getString("reminder","זמן ללמוד! 📚"));msg.setHint("נוסח התזכורת");card(msg);Button save2=btn("שמור תזכורת");save2.setOnClickListener(v->{p.edit().putString("reminder",msg.getText().toString()).apply();scheduleReminder();Toast.makeText(this,"התזכורת עודכנה",Toast.LENGTH_SHORT).show();});card(save2);Button time=btn("⏰ שעת תזכורת: "+p.getInt("hour",20)+":"+String.format(Locale.US,"%02d",p.getInt("minute",0)));time.setOnClickListener(v->{TimePickerDialog td=new TimePickerDialog(this,(x,h,m)->{p.edit().putInt("hour",h).putInt("minute",m).apply();scheduleReminder();showSettings();},p.getInt("hour",20),p.getInt("minute",0),true);td.show();});card(time);Button back=btn("← חזרה");back.setOnClickListener(v->showHome());content.addView(back);if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},77);}
    void scheduleReminder(){AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);Intent i=new Intent(this,ReminderReceiver.class);PendingIntent pi=PendingIntent.getBroadcast(this,12,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);Calendar c=Calendar.getInstance();c.set(Calendar.HOUR_OF_DAY,p.getInt("hour",20));c.set(Calendar.MINUTE,p.getInt("minute",0));c.set(Calendar.SECOND,0);if(c.before(Calendar.getInstance()))c.add(Calendar.DATE,1);am.setInexactRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);}
    void exportPdf(){PdfDocument doc=new PdfDocument();PdfDocument.PageInfo info=new PdfDocument.PageInfo.Builder(595,842,1).create();PdfDocument.Page page=doc.startPage(info);Canvas c=page.getCanvas();Paint paint=new Paint();paint.setTextSize(22);paint.setColor(Color.DKGRAY);c.drawText("מסלול לימוד",400,55,paint);paint.setTextSize(14);c.drawText(dedication,570,85,paint);c.drawText("התקדמות: "+countDone()+" / "+totalSelected()+" פרקים",500,125,paint);c.drawText("רצף: "+streak()+" ימים",500,150,paint);doc.finishPage(page);try{File f=new File(getCacheDir(),"study_report.pdf");FileOutputStream out=new FileOutputStream(f);doc.writeTo(out);out.close();doc.close();Intent share=new Intent(Intent.ACTION_SEND);share.setType("application/pdf");share.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f));startActivity(Intent.createChooser(share,"שיתוף דוח"));}catch(Exception e){Toast.makeText(this,"לא ניתן לייצא כרגע",Toast.LENGTH_SHORT).show();}}
}