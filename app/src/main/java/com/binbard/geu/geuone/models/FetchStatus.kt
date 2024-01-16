package com.binbard.geu.geuone.models

enum class FetchStatus {
    NOT_FETCHED,
    LOCAL_FETCHED,
    REMOTE_FETCHED,
    FULL_FETCHED,
}

enum class StatusCode{
    NA,
    SUCCESS,
    FAILED,
    NO_INTERNET,
    IO_ERROR,
    UNKNOWN_ERROR,
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
    UNKNOWN,
}