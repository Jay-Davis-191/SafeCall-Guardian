package com.example.fakecaller2;

/**
 * This class is used for setting and retrieving the integer value of the phone number.
 */
public class Phone {
    private int num;

    public void setNum(int phone_num) {
        num = phone_num;
    }

    public String getNum() {
        return Integer.toString(num);
    }
}
