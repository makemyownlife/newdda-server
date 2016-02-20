package com.elong.pb.newdda.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtil {

    public static Set<? extends Object> intersectSet(Set<? extends Object> orig, Set<? extends Object> intersect) {
        if (orig == null) {
            return intersect;
        }
        if (intersect == null || orig.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Object> set = new HashSet<Object>(orig.size());
        for (Object p : orig) {
            if (intersect.contains(p))
                set.add(p);
        }
        return set;
    }

}
