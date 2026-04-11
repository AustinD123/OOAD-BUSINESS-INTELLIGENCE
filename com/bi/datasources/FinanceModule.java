package com.bi.datasources;

import com.bi.interfaces.IDataSource;
import com.bi.util.Dataset;

public class FinanceModule implements IDataSource {
    private String module;
    private Dataset financeData;

    public FinanceModule(String module, Dataset financeData) {
        this.module = module;
        this.financeData = financeData;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Dataset getFinanceData() {
        return financeData;
    }

    public void setFinanceData(Dataset financeData) {
        this.financeData = financeData;
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
