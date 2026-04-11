package com.bi.datasources;

import com.bi.interfaces.IDataSource;
import com.bi.util.Dataset;

public class HRModule implements IDataSource {
    private String module;
    private Dataset hrData;
    private String connectionString;

    public HRModule(String module, Dataset hrData, String connectionString) {
        this.module = module;
        this.hrData = hrData;
        this.connectionString = connectionString;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Dataset getHrData() {
        return hrData;
    }

    public void setHrData(Dataset hrData) {
        this.hrData = hrData;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public Dataset exportData() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Dataset getData() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean validateSource() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void connect() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
