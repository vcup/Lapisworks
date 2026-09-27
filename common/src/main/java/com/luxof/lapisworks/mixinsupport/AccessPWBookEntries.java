package com.luxof.lapisworks.mixinsupport;

import dev.tizu.hexcessible.entries.BookEntries;

import java.util.List;

public interface AccessPWBookEntries {
    public List<BookEntries.Entry> getEntriesOfPWShapePattern(String genericId);
}
