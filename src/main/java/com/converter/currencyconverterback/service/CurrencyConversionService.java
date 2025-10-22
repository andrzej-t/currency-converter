package com.converter.currencyconverterback.service;

import com.converter.currencyconverterback.domain.Rates;
import com.converter.currencyconverterback.nbp.NbpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionService {

    private static final String PLN = "PLN";
    private static final int DECIMAL_SCALE = 2;
    
    private final NbpClient nbpClient;

    public List<Rates> getAllAvailableCurrencies() {
        return nbpClient.getAllCurrencies();
    }

    public BigDecimal convertCurrency(BigDecimal amount, String currencyFrom, String currencyTo) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be a positive number");
        }
        
        if (currencyFrom == null || currencyTo == null) {
            throw new IllegalArgumentException("Currency codes cannot be null");
        }

        log.info("Converting {} {} to {}", amount, currencyFrom, currencyTo);

        BigDecimal rateFrom = getExchangeRate(currencyFrom, true);
        BigDecimal rateTo = getExchangeRate(currencyTo, false);

        BigDecimal result = amount.multiply(rateFrom).divide(rateTo, DECIMAL_SCALE, RoundingMode.HALF_UP);
        
        log.info("Conversion result: {}", result);
        return result;
    }

    private BigDecimal getExchangeRate(String currencyCode, boolean isBid) {
        if (PLN.equals(currencyCode)) {
            return BigDecimal.ONE;
        }

        Optional<Rates> rateOptional = nbpClient.getAllCurrencies().stream()
                .filter(rates -> rates.getCode().equals(currencyCode))
                .findFirst();

        if (rateOptional.isEmpty()) {
            throw new IllegalArgumentException("Currency not found: " + currencyCode);
        }

        Rates rate = rateOptional.get();
        return isBid ? rate.getBid() : rate.getAsk();
    }
}
