package com.bi.interfaces;

import com.bi.util.Dataset;

public interface IDataSource {
    Dataset getData();

    boolean validateSource();

    void connect();
}
