package com.bi.datasources;

import com.bi.interfaces.IDataSource;
import com.bi.util.Dataset;

public class SalesModule implements IDataSource {
    private String module;
    private Dataset salesData;

    public SalesModule(String module, Dataset salesData) {
        this.module = module;
        this.salesData = salesData;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Dataset getSalesData() {
        return salesData;
    }

    public void setSalesData(Dataset salesData) {
        this.salesData = salesData;
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
