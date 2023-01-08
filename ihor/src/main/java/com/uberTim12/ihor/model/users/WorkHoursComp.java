package com.uberTim12.ihor.model.users;

import java.util.Comparator;

public class WorkHoursComp implements Comparator<WorkHours> {
    @Override
    public int compare(WorkHours o1, WorkHours o2) {
        if (o1.getStartTime().isAfter(o2.getStartTime()))
            return 1;
        else if (o1.getStartTime().isBefore(o2.getStartTime()))
            return -1;
        else
            return 0;
    }
}
