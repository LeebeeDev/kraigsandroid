package com.angrydoughnuts.android.alarmclock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

public class AlarmTime implements Parcelable, Comparable<AlarmTime> {
  private int secondsAfterMidnight;
  private Calendar asCalendar;

  // TODO(cgallek) change this to use alarmId and pull the secondsAfterMidnight
  // concept completely into this class.
  public AlarmTime(int secondsAfterMidnight) {
    this.secondsAfterMidnight = secondsAfterMidnight;
    this.asCalendar = Calendar.getInstance();

    int hours = secondsAfterMidnight % 3600;
    int minutes = (secondsAfterMidnight - (hours * 3600)) % 60;
    int seconds = (secondsAfterMidnight- (hours * 3600 + minutes * 60));
    asCalendar.set(Calendar.HOUR_OF_DAY, hours);
    asCalendar.set(Calendar.MINUTE, minutes);
    asCalendar.set(Calendar.SECOND, seconds);
  }

  public AlarmTime(Calendar calendar) {
    this.asCalendar = calendar;
    int hours = asCalendar.get(Calendar.HOUR_OF_DAY) * 3600;
    int minutes = asCalendar.get(Calendar.MINUTE) * 60;
    int seconds = asCalendar.get(Calendar.SECOND);
    this.secondsAfterMidnight = hours + minutes + seconds;
  }

  @Override
  public int compareTo(AlarmTime another) {
    return asCalendar.compareTo(another.asCalendar);
  }

  public String toString() {
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm.ss");
    return formatter.format(asCalendar.getTime());
  }

  public String localizedString(Context context) {
    boolean is24HourFormat = DateFormat.is24HourFormat(context);
    String format = "";
    String second = "";
    if (AlarmClockService.debug(context)) {
      second = ".ss";
    }
    if (is24HourFormat) {
      format = "HH:mm" + second;
    } else {
      format = "h:mm" + second + " aaa";
    }

    SimpleDateFormat formatter = new SimpleDateFormat(format);
    return formatter.format(asCalendar.getTime());
  }

  public int secondsAfterMidnight() {
    return secondsAfterMidnight;
  }

  public long nextLocalOccuranceInMillisUTC() {
    Calendar now = Calendar.getInstance();
    Calendar then = Calendar.getInstance();
    then.setTimeInMillis(asCalendar.getTimeInMillis());

    if (then.before(now)) {
      then.add(Calendar.DATE, 1);
    }
    return then.getTimeInMillis();
  }

  public String nextLocalOccuranceAsString() {
    long now_min = Calendar.getInstance().getTimeInMillis() / 1000 / 60;
    long then_min = nextLocalOccuranceInMillisUTC() / 1000 / 60;
    long difference_minutes = then_min - now_min;
    long days = difference_minutes / (60 * 24);
    long hours = difference_minutes % (60 * 24);
    long minutes = hours % 60;
    hours = hours / 60;

    // TODO(cgallek) extract these strings to strings.xml.
    String value = "";
    if (days == 1) {
      value += days + " day ";
    } else if (days > 1) {
      value += days + " days ";
    }
    if (hours == 1) {
      value += hours + " hour ";
    } else if (hours > 1) {
      value += hours + " hours ";
    }
    if (minutes == 1) {
      value += minutes + " minute";
    } else if (minutes > 1) {
      value += minutes + " minutes";
    }
    return value;
  }

  public static AlarmTime snoozeInMillisUTC(int minutes) {
    Calendar now = Calendar.getInstance();
    now.set(Calendar.SECOND, 0);
    now.add(Calendar.MINUTE, minutes);
    return new AlarmTime(now);
  }

  private AlarmTime(Parcel source) {
    this.secondsAfterMidnight = source.readInt();
    this.asCalendar = (Calendar) source.readSerializable();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(secondsAfterMidnight);
    dest.writeSerializable(asCalendar);
  }

  public static final Parcelable.Creator<AlarmTime> CREATOR =
    new Parcelable.Creator<AlarmTime>() {
      @Override
      public AlarmTime createFromParcel(Parcel source) {
        return new AlarmTime(source);
      }
      @Override
      public AlarmTime[] newArray(int size) {
        return new AlarmTime[size];
      }
    };

  @Override
  public int describeContents() {
    return 0;
  }
}