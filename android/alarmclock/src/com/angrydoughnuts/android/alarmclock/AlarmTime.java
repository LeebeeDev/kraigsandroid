package com.angrydoughnuts.android.alarmclock;

import java.util.Calendar;

public class AlarmTime {
  private int secondsAfterMidnight;
  private Calendar asCalendar;

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

  public String toString() {
    return String.format("%02d", asCalendar.get(Calendar.HOUR_OF_DAY)) + ":" 
    + String.format("%02d", asCalendar.get(Calendar.MINUTE)) + ":" 
    + String.format("%02d", asCalendar.get(Calendar.SECOND));
  }

  public int secondsAfterMidnight() {
    return secondsAfterMidnight;
  }

  public long nextLocalOccuranceInMillisUTC() {
    Calendar now = Calendar.getInstance();
    // TODO(cgallek): It might be a bad idea to modify asCalendar...
    if (asCalendar.before(now)) {
      asCalendar.add(Calendar.DATE, 1);
    }
    return asCalendar.getTimeInMillis();
  }

  public static AlarmTime snoozeInMillisUTC(int minutes) {
    Calendar now = Calendar.getInstance();
    now.set(Calendar.SECOND, 0);
    now.add(Calendar.MINUTE, minutes);
    return new AlarmTime(now);
  }
}
