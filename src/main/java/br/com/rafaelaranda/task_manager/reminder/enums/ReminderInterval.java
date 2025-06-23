package br.com.rafaelaranda.task_manager.reminder.enums;

public enum ReminderInterval {
    DAILY(1440),
    HOURLY(60),
    EVERY_30_MINUTES(30),
    EVERY_15_MINUTES(15),
    EVERY_5_MINUTES(5);

    private final int minutes;

    ReminderInterval(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }
}
