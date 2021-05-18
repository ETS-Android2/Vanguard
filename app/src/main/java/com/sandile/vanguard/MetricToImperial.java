package com.sandile.vanguard;

import java.text.DecimalFormat;

public class MetricToImperial {

    public Double convertToImperialWeight(Double inMetricWeight){
        return inMetricWeight * 2.20462262185;
    }

    public Double convertToImperialLength(Double inMetricLength){
        return inMetricLength / 2.54;
    }

    public Double convertToMetricWeight(Double inImperialWeight){
        return inImperialWeight / 2.20462262185;
    }

    public Double convertToMetricLength(Double inImperialLength){
        return inImperialLength * 2.54;
    }

    public Double toTwoDecimalPlaces(Double inToConvert){
        return Math.round(inToConvert * 100.0) / 100.0;
    }
}
