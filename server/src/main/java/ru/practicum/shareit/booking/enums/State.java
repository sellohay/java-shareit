package ru.practicum.shareit.booking.enums;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static boolean isValidState(String state) {
        if (state == null) {
            return false;
        }
        try {
            State.valueOf(state);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
