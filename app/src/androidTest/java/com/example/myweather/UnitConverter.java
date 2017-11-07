package com.example.myweather;

/**
 * Created by tubaozi on 11/1/17.
 */

public class UnitConverter {
    public static double Kelvin2Celsius(double k) {
        return k-273.15;
    }

    public static double Kelvin2Farenheit(double k)    {return k * 9 / 5 - 459.67;}


}
