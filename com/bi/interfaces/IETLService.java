package com.bi.interfaces;

import com.bi.util.Dataset;

public interface IETLService {
    Dataset extract(Dataset rawData);

    Dataset transform(Dataset data);

    void load(Dataset data);
}
