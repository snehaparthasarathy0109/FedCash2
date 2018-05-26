// IMyAidlInterface.aidl
package com.example.whisk.CommonPack;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
        void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                double aDouble, String aString);
        int[] monthlyCash(int year);
        int[] dailyCash(int day,int month,int year,int num);
        int yearlyAvg(int cash);
}
