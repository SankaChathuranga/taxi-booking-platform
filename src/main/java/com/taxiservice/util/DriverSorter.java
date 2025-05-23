package com.taxiservice.util;

import com.taxiservice.model.Driver;
import java.util.List;

public class DriverSorter {
    
    public static void sortByRating(List<Driver> drivers, boolean ascending) {
        if (drivers == null || drivers.size() <= 1) {
            return;
        }
        
        int n = drivers.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Driver current = drivers.get(j);
                Driver next = drivers.get(j + 1);
                
                boolean shouldSwap = ascending ? 
                    current.getRating() > next.getRating() : 
                    current.getRating() < next.getRating();
                
                if (shouldSwap) {
                    // Swap drivers
                    drivers.set(j, next);
                    drivers.set(j + 1, current);
                }
            }
        }
    }
}
