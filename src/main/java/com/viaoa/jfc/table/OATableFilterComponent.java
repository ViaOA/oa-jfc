package com.viaoa.jfc.table;

import com.viaoa.filter.OAFilter;

public interface OATableFilterComponent extends OATableComponent, OAFilter {
    void reset();
    boolean isBeingUsed();

}
