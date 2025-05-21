package com.taxibooking.util;

import com.taxibooking.model.AbstractDriver;
import java.util.List;

public class DriverSorter {
    
    public static void sortByRating(List<AbstractDriver> drivers) {
        int n = drivers.size();
        boolean swapped;
        
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                if (drivers.get(j).getRating() < drivers.get(j + 1).getRating()) {
                    // Swap drivers
                    AbstractDriver temp = drivers.get(j);
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
    
    public static void sortByAvailabilityAndRating(List<AbstractDriver> drivers) {
        int n = drivers.size();
        boolean swapped;
        
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                AbstractDriver current = drivers.get(j);
                AbstractDriver next = drivers.get(j + 1);
                
                // First sort by availability (available drivers first)
                if (current.isAvailable() != next.isAvailable()) {
                    if (!current.isAvailable() && next.isAvailable()) {
                        // Swap if current is not available but next is
                        drivers.set(j, next);
                        drivers.set(j + 1, current);
                        swapped = true;
                    }
                }
                // If availability is the same, sort by rating
                else if (current.getRating() < next.getRating()) {
                    drivers.set(j, next);
                    drivers.set(j + 1, current);
                    swapped = true;
                }
            }
            
            if (!swapped) {
                break;
            }
        }
    }
} 