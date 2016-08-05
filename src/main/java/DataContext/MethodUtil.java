package DataContext;

import android.content.Context;
import android.content.Intent;

import com.example.chen.myapplication.LoginActivity;

import java.util.Calendar;

/**
 * Created by chen on 16/2/1.
 */
public class MethodUtil {


    /**
     * 获取当前时间的字符串
     * @return
     */
    public static String geTimeStr(){

        Calendar now = Calendar.getInstance();

        String year = String.valueOf(now.get(Calendar.YEAR));
        String month = String.valueOf(now.get(Calendar.MONTH)-1);
        String day = String.valueOf(now.get(Calendar.DATE));
        String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(now.get(Calendar.MINUTE));
        String second = String.valueOf(now.get(Calendar.SECOND));

        String timeStr = year+"-"+month+"-"+day+" "+hour+":"+minute;
        return timeStr;
    }

    /**
     * 根据视频所有者
     * @param ownerId
     * @return
     */
    public static String createVedioId(String ownerId){

        String timeStr = geTimeStr();

        return ownerId+timeStr;
    }


    /**
     * 将获取到的视频时长转化为字符串格式
     * @param duration
     * @return
     */
    public static String getVedioLengthFromDuration(long duration){
        String vedioLength = "";
        //将时长除以1000得到秒数
        long temp = duration / 1000;

        String secondTemp = "";//临时秒的数值大小
        String minuteTemp = "";//临时分的数值大小
        String hourTemp = "";//临时小时的数值大小
        //判断秒数是否大于十秒
        if (temp%60<10){
            secondTemp = "0"+duration%60;
        }
        else{
            secondTemp = temp%60 +"";
        }
        if ((temp/60)%60<10){
            //分钟数小于10
            minuteTemp = "0"+(temp/60)%60;
        }
        else
        {
            minuteTemp = ""+(temp/60)%60;
        }
        if ((temp/(60*60))%60!=0) {
            if ((temp/(60*60))%60<10){
                hourTemp = "0"+(temp/(60*60))%60;
            }
            else
            {
                hourTemp = ""+(temp/(60*60))%60;
            }
        }


        if (hourTemp.equals("")) {
            vedioLength =  minuteTemp +":"+ secondTemp;
            return vedioLength;
        }
        vedioLength = hourTemp +":"+ minuteTemp +":"+ secondTemp;
        return vedioLength;
    }

    public static String getTYpeNameFromType(int type){
        switch (type){
            case 0:
                return "热点推荐";
            case 1:
                return "互联网";
            case 2:
                return "风土人情";
            case 3:
                return "金融";
            case 4:
                return "爱生活";
            case 5:
                return "英语";
            case 6:
                return "健康";
            case 7:
                return "自制微电影";
            case 8:
                return "法律";
            case 9:
                return "热卖";
            case 10:
                return "工程";
            case 11:
                return "哲学";
            case 12:
                return "明星大v";
            case 13:
                return "电商";
            case 14:
                return "iOS开发";
            case 15:
                return "Android开发";
        }
        return "";
    }

    /**
     * 需要登录才能享受的功能
     * 跳转到制定的界面
     * @param context
     * @param destination
     */
    public static void goToIntent(Context context,Class destination){
        Intent intent = new Intent();
        if (AppData.user == null){
            //当前未登录，跳转到登录界面
            intent.setClass(context,LoginActivity.class);
        }
        else {
            intent.setClass(context, destination);
        }
        context.startActivity(intent);
    }

}
