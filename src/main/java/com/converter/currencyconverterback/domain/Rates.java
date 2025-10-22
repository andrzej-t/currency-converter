package com.converter.currencyconverterback.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rates {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("code")
    private String code;

    @JsonProperty("bid")
    private BigDecimal bid;

    @JsonProperty("ask")
    private BigDecimal ask;
}
