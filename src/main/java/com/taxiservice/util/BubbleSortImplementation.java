package com.taxiservice.util;

import com.taxiservice.model.Driver;
import java.util.List;

/**
 * Implementation of Bubble Sort algorithm for sorting drivers by rating
 */
public class BubbleSortImplementation {
    
    /**
     * Sorts a list of drivers by their rating in descending order
     * @param drivers List of drivers to be sorted
     */
    public static void sort(List<Driver> drivers) {
        if (drivers == null || drivers.size() <= 1) {
            return;
        }

        int n = drivers.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                // Compare adjacent drivers by rating
                if (drivers.get(j).getRating() < drivers.get(j + 1).getRating()) {
                    // Swap drivers
                    Driver temp = drivers.get(j);
                    drivers.set(j, drivers.get(j + 1));
                    drivers.set(j + 1, temp);
                    swapped = true;
                }
            }

            // If no swapping occurred in this pass, array is sorted
            if (!swapped) {
                break;
            }
        }
    }
} 