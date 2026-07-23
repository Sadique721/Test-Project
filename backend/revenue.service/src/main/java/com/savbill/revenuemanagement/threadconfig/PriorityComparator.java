package com.savbill.revenuemanagement.threadconfig;

import com.savbill.revenuemanagement.tasks.CustomMainThread;

import java.util.Comparator;

public class PriorityComparator implements Comparator<Runnable> {

    @Override
    public int compare(Runnable r1, Runnable r2) {
        CustomMainThread s1 = (CustomMainThread) r1;
        CustomMainThread s2 = (CustomMainThread) r2;

        if (s1.getPriority() < s2.getPriority())
            return 1;
        else if (s1.getPriority() > s2.getPriority())
            return -1;
        return 0;
    }
}
