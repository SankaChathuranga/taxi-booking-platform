package com.example.taxi.service;
import com.example.taxi.model.Driver;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SortService {
    public void sortByRatingDesc(List<Driver> d) {
        int n=d.size();
        for(int i=0;i<n-1;i++) for(int j=0;j<n-i-1;j++)
            if(d.get(j).getRating()<d.get(j+1).getRating()) Collections.swap(d,j,j+1);
    }
}