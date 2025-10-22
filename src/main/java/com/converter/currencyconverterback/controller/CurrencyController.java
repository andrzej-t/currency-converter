package com.converter.currencyconverterback.controller;

import com.converter.currencyconverterback.domain.Rates;
import com.converter.currencyconverterback.nbp.NbpClient;
import com.converter.currencyconverterback.service.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CurrencyController {

    private final NbpClient nbpClient;
    private final CurrencyConversionService currencyConversionService;

    @GetMapping("/currencies")
    public ResponseEntity<List<Rates>> getCurrencies() {
        List<Rates> currencies = nbpClient.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/result")
    public ResponseEntity<BigDecimal> showResult(
            @RequestParam BigDecimal amount,
            @RequestParam String currencyFrom,
            @RequestParam String currencyTo) {

        BigDecimal result = currencyConversionService.convertCurrency(amount, currencyFrom, currencyTo);
        return ResponseEntity.ok(result);
    }
}
