package com.sandile.vanguard;

import java.text.DecimalFormat;

public class MetricToImperial {

    public Double convertToImperialLength(Double inMetricLength){
        return inMetricLength / 2.54;
    }

    public Double convertToMetricLength(Double inImperialLength){
        return inImperialLength * 2.54;
    }

    public Double toTwoDecimalPlaces(Double inToConvert){
        return Math.round(inToConvert * 100.0) / 100.0;
    }
}
