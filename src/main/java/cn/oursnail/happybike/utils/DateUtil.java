package cn.oursnail.happybike.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;



@Component
@SuppressWarnings("unused")
public class DateUtil {

    public static final String hhmmFormat="HH:mm";
    public static final String MMddFormat="MM-dd";
    public static final String yyyyFormat="yyyy";
    public static final String yyyyChineseFormat="yyyy年";
    public static final String yyyyMMddFormat="yyyy-MM-dd";
    public static final String fullFormat="yyyy-MM-dd HH:mm:ss";
    public static final String MMddChineseFormat="MM月dd日";
    public static final String yyyyMMddHHmmChineseFormat="yyyy年MM月dd日 HH时mm分";
    public static final String yyyyMMddChineseFormat="yyyy年MM月dd日";
    public static final String fullChineseFormat="yyyy年MM月dd日 HH时mm分ss秒";
    public static final String yyyyMMddHHmmssSSS = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String [] WEEKS={"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
    public static final String NO_SEPARATOR="yyyyMMddHHmmss";
    public static final String NO_SEPARATOR_SSS="yyyyMMddHHmmssSSS";
    public static final Long mills4oneHour = 60 * 60 * 1000l;
    public static final Float mills4oneMinuteFloat = 60 * 1000f;
    public static final Float mills4oneHourFloat = 60 * 60 * 1000f;
    public static final long mills4oneDay = 24 * mills4oneHourFloat.longValue();
    public static final float mills4oneDayFloat = 24 * mills4oneHourFloat;

    public static final String yyyyMMddHHmm="yyyy-MM-dd HH:mm";
    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();



    /**
     * 返回当前日期 格式参照fullFormat
     * @return
     */
    public static String curDateStr(){
        DateFormat df=new SimpleDateFormat(fullFormat);
        return df.format(new Date());
    }

    /**
     * 得到指定时间的时间日期格式
     * @param date 指定的时间
     * @param format 时间日期格式
     * @return
     */
    public static String getFormatDateTime(Date date,String format){
        DateFormat df=new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 判断是否是润年
     * @param date 指定的时间
     * @return true:是润年,false:不是润年
     */
    public static boolean isLeapYear(Date date) {
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        return isLeapYear(cal.get(Calendar.YEAR));
    }

    /**
     * 判断是否是润年
     * @param  year 指定的年
     * @return true:是润年,false:不是润年
     */
    public static boolean isLeapYear(int year) {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.isLeapYear(year);
    }

    /**
     * 判断指定的时间是否是今天
     * @param date 指定的时间
     * @return true:是今天,false:非今天
     */
    public static boolean isInToday(Date date){
        boolean flag=false;
        Date now=new Date();
        String fullFormat=getFormatDateTime(now, DateUtil.yyyyMMddFormat);
        String beginString=fullFormat+" 00:00:00";
        String endString=fullFormat+" 23:59:59";
        DateFormat df=new SimpleDateFormat(DateUtil.fullFormat);
        try {
            Date beginTime=df.parse(beginString);
            Date endTime=df.parse(endString);
            flag=date.before(endTime)&&date.after(beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断两时间是否是同一天
     * @param from 第一个时间点
     * @param to 第二个时间点
     * @return true:是同一天,false:非同一天
     */
    public static boolean isSameDay(Date from,Date to){
        boolean isSameDay=false;
        DateFormat df=new SimpleDateFormat(DateUtil.yyyyMMddFormat);
        String firstDate=df.format(from);
        String secondDate=df.format(to);
        isSameDay=firstDate.equals(secondDate);
        return isSameDay;
    }

    /**
     * 求出指定的时间那天是星期几
     * @param date 指定的时间
     * @return 星期X
     */
    public static String getWeekString(Date date){
        return DateUtil.WEEKS[getWeek(date)-1];
    }

    /**
     * 求出指定时间那天是星期几
     * @param date 指定的时间
     * @return 1-7
     */
    public static int getWeek(Date date){
        int week=0;
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        week=cal.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 取得指定时间离现在是多少时间以前，如：3秒前,2小时前等
     * 注意：此计算方法不是精确的
     * @param date 已有的指定时间
     * @return 时间段描述
     */
    public static String getAgoTimeString(Date date){
        Date now=new Date();
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        Date agoTime=cal.getTime();
        long mtime=now.getTime()-agoTime.getTime();
        String str="";
        long stime=mtime/1000;
        long minute=60;
        long hour=60*60;
        long day=24*60*60;
        long weeks=7*24*60*60;
        long months=100*24*60*60;
        if(stime<minute){
            long time_value=stime;
            if(time_value<=0){
                time_value=1;
            }
            str=time_value+"秒前";
        }else if(stime>=minute && stime<hour){
            long time_value=stime/minute;
            if(time_value<=0){
                time_value=1;
            }
            str=time_value+"分前";
        }else if(stime>=hour && stime<day){
            long time_value=stime/hour;
            if(time_value<=0){
                time_value=1;
            }
            str=time_value+"小时前";
        }else if(stime>=day&&stime<weeks){
            long time_value=stime/day;
            if(time_value<=0){
                time_value=1;
            }
            str=time_value+"天前";
        }else if(stime>=weeks&&stime<months){
            DateFormat df=new SimpleDateFormat(DateUtil.MMddFormat);
            str=df.format(date);
        }else{
            DateFormat df=new SimpleDateFormat(DateUtil.yyyyMMddFormat);
            str=df.format(date);
        }
        return str;
    }

    /**
     * 判断指定时间是否是周末
     * @param date 指定的时间
     * @return true:是周末,false:非周末
     */
    public static boolean isWeeks(Date date){
        boolean isWeek=false;
        isWeek=(getWeek(date)-1==0||getWeek(date)-1==6);
        return isWeek;
    }

    /**
     * 得到今天的最开始时间
     * @return 今天的最开始时间
     */
    public static Date getTodayBeginTime(){
        String beginString= DateUtil.yyyyMMddFormat+" 00:00:00";
        DateFormat df=new SimpleDateFormat(DateUtil.fullFormat);
        Date beginTime=new Date();
        try {
            beginTime=df.parse(beginString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return beginTime;
    }
    /**
     * 得到今天的最开始时间
     * @return 今天的最开始时间
     */
    public static Date getDateBeginTime(Date date){
        String dateString= DateFormatUtils.format(date, "yyyy-MM-dd 00:00:00");
        Date beginTime=null;
        try {
            beginTime= DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return beginTime;
    }

    /**
     * 得到今天的最后结束时间
     * @return 今天的最后时间
     */
    public static Date getTodayEndTime(){
        String endString= DateUtil.yyyyMMddFormat+" 23:59:59";
        DateFormat df=new SimpleDateFormat(DateUtil.fullFormat);
        Date endTime=new Date();
        try {
            endTime=df.parse(endString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }
    /**
     * 得到今天的最后结束时间
     * @return 今天的最后时间
     */
    public static Date getDateEndTime(Date date){
        String dateString=DateFormatUtils.format(date, "yyyy-MM-dd 23:59:59");
        Date endTime=null;
        try {
            endTime=DateUtils.parseDate(dateString, new String[]{"yyyy-MM-dd hh:mm:ss"});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    /**
     * 取得本周的开始时间
     * @return 本周的开始时间
     */
    public static Date getThisWeekBeginTime(){
        Date beginTime=null;
        Calendar cal=Calendar.getInstance();
        int week=getWeek(cal.getTime());
        week=week-1;
        int days=0;
        if(week==0){
            days=6;
        }else{
            days=week-1;
        }
        cal.add(Calendar.DAY_OF_MONTH, -days);
        beginTime=cal.getTime();
        return beginTime;
    }
    /**
     * 指定时间获取周开始时间
     * @return
     */
    public static Date getWeekBeginTime(Date date){
        Date beginTime=null;
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int week=getWeek(cal.getTime());
        week=week-1;
        int days=0;
        if(week==0){
            days=6;
        }else{
            days=week-1;
        }
        cal.add(Calendar.DAY_OF_MONTH, -days);
        beginTime=cal.getTime();
        return beginTime;
    }

    /**
     * 取得本周的开始日期
     * @param format 时间的格式
     * @return 指定格式的本周最开始时间
     */
    public static String getThisWeekBeginTimeString(String format){
        DateFormat df=new SimpleDateFormat(format);
        return df.format(getThisWeekBeginTime());
    }


    /**
     * 取得本周的结束时间
     * @return 本周的结束时间
     */
    public static Date getThisWeekEndTime(){
        Date endTime=null;
        Calendar cal=Calendar.getInstance();
        int week=getWeek(cal.getTime());
        week=week-1;
        int days=0;
        if(week!=0){
            days=7-week;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        endTime=cal.getTime();
        return endTime;
    }
    /**
     * 获取指定时间周的结束时间
     * @return
     */
    public static Date getWeekEndTime(Date date){
        Date endTime=null;
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int week=getWeek(cal.getTime());
        week=week-1;
        int days=0;
        if(week!=0){
            days=7-week;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        endTime=cal.getTime();
        return endTime;
    }

    /**
     * 取得本周的结束日期
     * @param format 时间的格式
     * @return 指定格式的本周结束时间
     */
    public static String getThisWeekEndTimeString(String format){
        DateFormat df=new SimpleDateFormat(format);
        return df.format(getThisWeekEndTime());
    }

    /**
     * 取得两时间相差的天数
     * @param from 第一个时间
     * @param to 第二个时间
     * @return 相差的天数
     */
    public static long getBetweenDays(Date from, Date to){
        long days=0;
        long dayTime=24*60*60*1000;
        long fromTime=from.getTime();
        long toTime=to.getTime();
        long times=Math.abs(fromTime-toTime);
        days=times/dayTime;
        return days;
    }

    /**
     * 取得两时间相差的小时数
     * @param from 第一个时间
     * @param to 第二个时间
     * @return 相差的小时数
     */
    public static long getBetweenHours(Date from,Date to){
        long hours=0;
        long hourTime=60*60*1000;
        long fromTime=from.getTime();
        long toTime=to.getTime();
        long times=Math.abs(fromTime-toTime);
        hours=times/hourTime;
        return hours;
    }

    /**
     *
     * @param endDate
     * @param nowDate
     * @return 相差分钟数
     */
    public static Long getBetweenMin(Date endDate, Date nowDate) {
        long minTime=60*1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少分钟
        long min = diff/minTime;
        return  min;
    }

    /**
     * 取得在指定时间上加减days天后的时间
     * @param date 指定的时间
     * @param days 天数,正为加，负为减
     * @return 在指定时间上加减days天后的时间
     */
    public static Date addDays(Date date,int days){
        Date time=null;
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        time=cal.getTime();
        return time;
    }

    /**
     * 取得在指定时间上加减months月后的时间
     * @param date 指定时间
     * @param months 月数，正为加，负为减
     * @return 在指定时间上加减months月后的时间
     */
    public static Date addMonths(Date date,int months){
        Date time=null;
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MONTH, months);
        time=cal.getTime();
        return time;
    }

    /**
     * 取得在指定时间上加减years年后的时间
     * @param date 指定时间
     * @param years 年数，正为加，负为减
     * @return 在指定时间上加减years年后的时间
     */
    public static Date addYears(Date date,int years){
        Date time=null;
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.YEAR, years);
        time=cal.getTime();
        return time;
    }

    public static long toMillis(String dateStr) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat(yyyyMMddHHmmssSSS);
        Date date = format.parse(dateStr);
        return date.getTime();
    }


    /**
     * 获取某个时刻的毫秒值
     * @param clock
     * @return
     */
    public long getClockTimeMills(Integer clock){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, clock);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取某一天某个时刻的毫秒值
     * @param clock
     * @return
     */
    public long getClockTimeMills(Integer clock, Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, clock);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 字符串是否符合某格式的日期
     * @param str
     * @param pattern
     * @return
     */
    public static  boolean isDate(String str,String pattern){
        if(StringUtils.isBlank(str) || StringUtils.isBlank(pattern) || str.length()!=pattern.length()){
            return false;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            df.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }

    /**
     * 获取某个时间点的整数分钟时的毫秒值
     * @throws ParseException
     */
    public static long getMills4IntegerMinute() throws ParseException{
        SimpleDateFormat sdf = threadLocal.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(yyyyMMddHHmm);
            threadLocal.set(sdf);
        }
        long result_mills = sdf.parse(sdf.format(new Date())).getTime();
        return result_mills;
    }
}