package com.magicdroid.magiclocationlib.location;

import java.io.Serializable;

/**
 * Created by sanidhya on 2/5/17.
 */

public class MbLocationError implements Serializable {
    public int errorCode;
    public String message;

    public MbLocationError(int errorCode, String message) {

        this.errorCode = errorCode;
        this.message = message;
    }
}
