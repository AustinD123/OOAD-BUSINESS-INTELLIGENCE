package com.bi.interfaces;

import com.bi.models.KPI;
import com.bi.util.TargetSet;
import java.util.List;

public interface IKPIService {
    void calculateKPI(List<KPI> metrics, TargetSet targets);

    void evaluateTarget();
}
