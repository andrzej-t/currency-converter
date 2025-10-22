package com.converter.currencyconverterback.service;

import com.converter.currencyconverterback.domain.Rates;
import com.converter.currencyconverterback.nbp.NbpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceTest {

    @Mock
    private NbpClient nbpClient;

    @InjectMocks
    private CurrencyConversionService service;

    private List<Rates> mockRates;

    @BeforeEach
    void setUp() {
        mockRates = Arrays.asList(
                new Rates("US Dollar", "USD", new BigDecimal("3.90"), new BigDecimal("4.00")),
                new Rates("Euro", "EUR", new BigDecimal("4.30"), new BigDecimal("4.40")),
                new Rates("British Pound", "GBP", new BigDecimal("5.00"), new BigDecimal("5.10"))
        );
    }

    @Test
    void shouldConvertFromPLNToForeignCurrency() {
        // given
        when(nbpClient.getAllCurrencies()).thenReturn(mockRates);
        BigDecimal amount = new BigDecimal("100.00");

        // when
        BigDecimal result = service.convertCurrency(amount, "PLN", "USD");

        // then
        // Normalize a result for robust BigDecimal comparison in currency tests
        assertEquals(new BigDecimal("25.00"), result.setScale(2, RoundingMode.HALF_UP)); // 100 / 4.00 = 25.00
        verify(nbpClient, times(1)).getAllCurrencies();
    }

    @Test
    void shouldConvertFromForeignCurrencyToPLN() {
        // given
        when(nbpClient.getAllCurrencies()).thenReturn(mockRates);
        BigDecimal amount = new BigDecimal("100.00");

        // when
        BigDecimal result = service.convertCurrency(amount, "USD", "PLN");

        // then
        // Normalize a result for robust BigDecimal comparison in currency tests
        assertEquals(new BigDecimal("390.00"), result.setScale(2, RoundingMode.HALF_UP)); // 100 * 3.90 = 390.00
        verify(nbpClient, times(1)).getAllCurrencies();
    }

    @Test
    void shouldConvertBetweenTwoForeignCurrencies() {
        // given
        when(nbpClient.getAllCurrencies()).thenReturn(mockRates);
        BigDecimal amount = new BigDecimal("100.00");

        // when
        BigDecimal result = service.convertCurrency(amount, "USD", "EUR");

        // then
        // (100 * 3.90) / 4.40 = 88.6363..., rounded to 88.64
        // Normalize result for robust BigDecimal comparison in currency tests
        assertEquals(new BigDecimal("88.64"), result.setScale(2, RoundingMode.HALF_UP));
        verify(nbpClient, times(2)).getAllCurrencies();
    }

    @Test
    void shouldConvertPLNToPLN() {
        // given
        BigDecimal amount = new BigDecimal("100.00");

        // when
        BigDecimal result = service.convertCurrency(amount, "PLN", "PLN");

        // then
        // Normalize a result for robust BigDecimal comparison in currency tests
        assertEquals(new BigDecimal("100.00"), result.setScale(2, RoundingMode.HALF_UP));
        verify(nbpClient, never()).getAllCurrencies();
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        // when and then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.convertCurrency(null, "USD", "EUR")
        );
        assertEquals("Amount must be a positive number", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        // when and then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.convertCurrency(new BigDecimal("-10"), "USD", "EUR")
        );
        assertEquals("Amount must be a positive number", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyFromIsNull() {
        // when and then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.convertCurrency(new BigDecimal("100"), null, "EUR")
        );
        assertEquals("Currency codes cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyToIsNull() {
        // when and then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.convertCurrency(new BigDecimal("100"), "USD", null)
        );
        assertEquals("Currency codes cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyNotFound() {
        // given
        when(nbpClient.getAllCurrencies()).thenReturn(mockRates);

        // when and then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.convertCurrency(new BigDecimal("100"), "XYZ", "PLN")
        );
        assertEquals("Currency not found: XYZ", exception.getMessage());
    }

    @Test
    void shouldGetAllAvailableCurrencies() {
        // given
        when(nbpClient.getAllCurrencies()).thenReturn(mockRates);

        // when
        List<Rates> result = service.getAllAvailableCurrencies();

        // then
        assertEquals(3, result.size());
        assertEquals("USD", result.get(0).getCode());
        verify(nbpClient, times(1)).getAllCurrencies();
    }

    @Test
    void shouldHandleDecimalAmounts() {
        // given
        when(nbpClient.getAllCurrencies()).thenReturn(mockRates);
        BigDecimal amount = new BigDecimal("99.99");

        // when
        BigDecimal result = service.convertCurrency(amount, "PLN", "USD");

        // then
        // 99.99 / 4.00 = 24.9975, rounded to 25.00
        // Normalize result for robust BigDecimal comparison in currency tests
        assertEquals(new BigDecimal("25.00"), result.setScale(2, RoundingMode.HALF_UP));
        verify(nbpClient, times(1)).getAllCurrencies();
    }
}
