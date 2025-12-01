package com.converter.currencyconverterback.nbp;

import com.converter.currencyconverterback.CurrencyConverterApplication;
import com.converter.currencyconverterback.domain.Currency;
import com.converter.currencyconverterback.domain.Rates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CurrencyConverterApplication.class)
@EnableCaching
@SuppressWarnings({"unchecked", "rawtypes"})
class NbpClientTest {

    @MockBean
    private RestClient.Builder restClientBuilder;

    @Autowired
    private NbpClient nbpClient;

    @Autowired
    private CacheManager cacheManager;

    private RestClient mockRestClient;
    private RestClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;
    private RestClient.RequestHeadersSpec mockRequestHeadersSpec;
    private RestClient.ResponseSpec mockResponseSpec;

    @BeforeEach
    void setUp() {
        mockRestClient = mock(RestClient.class);
        mockRequestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        mockRequestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(restClientBuilder.baseUrl(any(String.class))).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(mockRestClient);

        when(mockRestClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(any(String.class))).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        Cache cache = cacheManager.getCache("currencies");
        if (cache != null) {
            cache.clear();
        }
    }

    private Rates createRate(String code, String currencyName, String bid, String ask) {
        return new Rates(currencyName, code, new BigDecimal(bid), new BigDecimal(ask));
    }

    private Currency createCurrency(Rates... rates) {
        Currency currency = new Currency();
        currency.setTable("A");
        currency.setNo("001/A/NBP/2023");
        currency.setEffectiveDate("2023-01-02");
        currency.setRates(List.of(rates));
        return currency;
    }

    @Test
    void getAllCurrencies_shouldReturnRatesList_onSuccessfulApiCall() {
        // Arrange
        Rates rate1 = createRate("USD", "dolar amerykański", "4.00", "4.10");
        Rates rate2 = createRate("EUR", "euro", "4.50", "4.60");

        Currency currency1 = createCurrency(rate1);
        Currency currency2 = createCurrency(rate2);

        Currency[] mockResponse = {currency1, currency2};

        when(mockResponseSpec.body(Currency[].class)).thenReturn(mockResponse);

        // Act
        List<Rates> result = nbpClient.getAllCurrencies();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(rate1));
        assertTrue(result.contains(rate2));

        verify(mockRestClient).get();
        verify(mockRequestHeadersUriSpec).uri("/tables/c?format=json");
        verify(mockRequestHeadersSpec).retrieve();
        verify(mockResponseSpec).body(Currency[].class);
    }

    @Test
    void getAllCurrencies_shouldReturnEmptyList_whenApiReturnsNullBody() {
        // Arrange
        when(mockResponseSpec.body(Currency[].class)).thenReturn(null);

        // Act
        List<Rates> result = nbpClient.getAllCurrencies();

        // Assert
        assertTrue(result.isEmpty());
        verify(mockRestClient).get();
    }

    @Test
    void getAllCurrencies_shouldReturnEmptyList_onRestClientException() {
        // Arrange
        when(mockResponseSpec.body(Currency[].class)).thenThrow(new RestClientException("API Error"));

        // Act
        List<Rates> result = nbpClient.getAllCurrencies();

        // Assert
        assertTrue(result.isEmpty());
        verify(mockRestClient).get();
    }

    @Test
    void getAllCurrencies_shouldUseCache_onSubsequentCalls() {
        // Arrange
        Rates rate1 = createRate("USD", "dolar amerykański", "4.00", "4.10");
        Rates rate2 = createRate("EUR", "euro", "4.50", "4.60");

        Currency currency1 = createCurrency(rate1);
        Currency currency2 = createCurrency(rate2);

        Currency[] mockResponse = {currency1, currency2};

        when(mockResponseSpec.body(Currency[].class)).thenReturn(mockResponse);

        List<Rates> firstResult = nbpClient.getAllCurrencies();

        // Assert
        assertEquals(2, firstResult.size());
        assertTrue(firstResult.contains(rate1));
        assertTrue(firstResult.contains(rate2));

        verify(mockRestClient, times(1)).get();
        verify(mockRequestHeadersUriSpec, times(1)).uri("/tables/c?format=json");
        verify(mockRequestHeadersSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).body(Currency[].class);

        List<Rates> secondResult = nbpClient.getAllCurrencies();

        // Assert
        assertEquals(2, secondResult.size());
        assertTrue(secondResult.contains(rate1));
        assertTrue(secondResult.contains(rate2));

        verify(mockRestClient, times(1)).get();
        verify(mockRequestHeadersUriSpec, times(1)).uri("/tables/c?format=json");
        verify(mockRequestHeadersSpec, times(1)).retrieve();
        verify(mockResponseSpec, times(1)).body(Currency[].class);
    }
}
