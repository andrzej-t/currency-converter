package com.converter.currencyconverterback.controller;

import com.converter.currencyconverterback.domain.Rates;
import com.converter.currencyconverterback.nbp.NbpClient;
import com.converter.currencyconverterback.service.CurrencyConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NbpClient nbpClient;

    @MockBean
    private CurrencyConversionService currencyConversionService;

    @Test
    void shouldGetAllCurrencies() throws Exception {
        // given
        List<Rates> mockRates = Arrays.asList(
                new Rates("US Dollar", "USD", new BigDecimal("3.90"), new BigDecimal("4.00")),
                new Rates("Euro", "EUR", new BigDecimal("4.30"), new BigDecimal("4.40"))
        );
        when(nbpClient.getAllCurrencies()).thenReturn(mockRates);

        // when and then
        mockMvc.perform(get("/v1/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("USD"))
                .andExpect(jsonPath("$[0].currency").value("US Dollar"))
                .andExpect(jsonPath("$[1].code").value("EUR"))
                .andExpect(jsonPath("$[1].currency").value("Euro"));
    }

    @Test
    void shouldConvertCurrency() throws Exception {
        // given
        BigDecimal expectedResult = new BigDecimal("25.00");
        when(currencyConversionService.convertCurrency(
                any(BigDecimal.class),
                eq("PLN"),
                eq("USD")
        )).thenReturn(expectedResult);

        // when and then
        mockMvc.perform(get("/v1/result")
                        .param("amount", "100.00")
                        .param("currencyFrom", "PLN")
                        .param("currencyTo", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("25.00"));
    }

    @Test
    void shouldHandleDecimalAmounts() throws Exception {
        // given
        BigDecimal expectedResult = new BigDecimal("24.97");
        when(currencyConversionService.convertCurrency(
                any(BigDecimal.class),
                eq("PLN"),
                eq("USD")
        )).thenReturn(expectedResult);

        // when and then
        mockMvc.perform(get("/v1/result")
                        .param("amount", "99.99")
                        .param("currencyFrom", "PLN")
                        .param("currencyTo", "USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("24.97"));
    }

    @Test
    void shouldReturnBadRequestForInvalidAmount() throws Exception {
        // when and then
        mockMvc.perform(get("/v1/result")
                        .param("amount", "invalid")
                        .param("currencyFrom", "PLN")
                        .param("currencyTo", "USD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCurrencyNotFound() throws Exception {
        // given
        when(currencyConversionService.convertCurrency(
                any(BigDecimal.class),
                eq("XYZ"),
                eq("USD")
        )).thenThrow(new IllegalArgumentException("Currency not found: XYZ"));

        // when and then
        mockMvc.perform(get("/v1/result")
                        .param("amount", "100")
                        .param("currencyFrom", "XYZ")
                        .param("currencyTo", "USD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency not found: XYZ"));
    }
}
