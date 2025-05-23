package com.taxiservice.util;

import com.taxiservice.model.Driver;
import java.util.List;

/**
 * Implementation of Bubble Sort algorithm for sorting drivers
 */
public class BubbleSortImplementation {
    
    /**
     * Sorts a list of drivers by their rating in descending order
     * @param drivers List of drivers to be sorted
     */
    public static void sort(List<Driver> drivers) {
        sort(drivers, SortCriteria.RATING);
    }
    
    /**
     * Sorts a list of drivers by the specified criteria
     * @param drivers List of drivers to be sorted
     * @param criteria The criteria to sort by
     */
    public static void sort(List<Driver> drivers, SortCriteria criteria) {
        if (drivers == null || drivers.size() <= 1) {
            return;
        }

        int n = drivers.size();
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                // Compare adjacent drivers based on criteria
                if (compare(drivers.get(j), drivers.get(j + 1), criteria) > 0) {
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
    
    /**
     * Compares two drivers based on the specified criteria
     * @param driver1 First driver
     * @param driver2 Second driver
     * @param criteria The criteria to compare by
     * @return Negative if driver1 should come first, positive if driver2 should come first
     */
    private static int compare(Driver driver1, Driver driver2, SortCriteria criteria) {
        switch (criteria) {
            case RATING:
                // Sort by rating in descending order
                return Double.compare(driver2.getRating(), driver1.getRating());
            case NAME:
                // Sort by name in ascending order
                return driver1.getFullName().compareTo(driver2.getFullName());
            case REGISTRATION_DATE:
                // Sort by registration date in descending order (newest first)
                return driver2.getRegistrationDate().compareTo(driver1.getRegistrationDate());
            default:
                return 0;
        }
    }
    
    /**
     * Enum representing sorting criteria for drivers
     */
    public enum SortCriteria {
        RATING,            // Sort by driver rating
        NAME,              // Sort by driver name
        REGISTRATION_DATE  // Sort by registration date
    }
}