package com.example.taxiapp5.Service;

import com.example.taxiapp5.model.Driver;

public class DriverSorter {

    // Bubble sort drivers by rating in descending order
    public static Driver[] sortDriversByRating(Driver[] drivers) {
        int n = drivers.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (drivers[j].getRating() < drivers[j + 1].getRating()) {
                    // Swap
                    Driver temp = drivers[j];
                    drivers[j] = drivers[j + 1];
                    drivers[j + 1] = temp;
                }
            }
        }
        return drivers;
    }
}