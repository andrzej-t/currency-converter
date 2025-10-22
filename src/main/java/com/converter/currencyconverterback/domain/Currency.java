package com.converter.currencyconverterback.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Currency {

    @JsonProperty("table")
    private String table;

    @JsonProperty("no")
    private String no;

    @JsonProperty("tradingDate")
    private String tradingDate;

    @JsonProperty("effectiveDate")
    private String effectiveDate;

    @JsonProperty("rates")
    private List<Rates> rates;
}
