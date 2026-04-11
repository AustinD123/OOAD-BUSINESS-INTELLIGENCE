package com.bi.interfaces;

import com.bi.util.Dataset;
import java.util.List;

public interface IDataRepository {
    void store(Dataset data);

    Dataset retrieve(String id);

    List<Dataset> index();
}
