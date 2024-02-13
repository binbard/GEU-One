package com.binbard.geu.one.models

enum class FetchStatus {
    NA,
    SUCCESS,
    FAILED,
    NO_NEW_DATA_FOUND,
    NEW_DATA_FOUND,
    DONE,
}

enum class StatusCode{
    NA,
    SUCCESS,
    FAILED,
    DONE,
}

enum class LoginStatus {
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT,
    PREV_LOGGED_IN,
    PREV_LOGGED_OUT,
    LOGGED_IN,
    NOT_LOGGED_IN,
    RESET_MATCH,
    RESET_NOTMATCH,
    CHANGE_PASSWORD_SUCCESS,
    CHANGE_PASSWORD_FAILED,
    CHANGE_PASSWORD_EXPIRED,
    UNKNOWN,
}