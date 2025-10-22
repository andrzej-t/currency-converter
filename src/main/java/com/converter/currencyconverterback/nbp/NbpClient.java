package com.converter.currencyconverterback.nbp;

import com.converter.currencyconverterback.domain.Currency;
import com.converter.currencyconverterback.domain.Rates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service; // Import the Service annotation
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class NbpClient {
    private final RestClient.Builder restClientBuilder;
    private final String nbpApiBaseUrl;

    public NbpClient(
            RestClient.Builder restClientBuilder,
            @Value("${nbp.api.base-url}") String nbpApiBaseUrl) {
        this.restClientBuilder = restClientBuilder;
        this.nbpApiBaseUrl = nbpApiBaseUrl;
    }

    @Cacheable("currencies")
    public List<Rates> getAllCurrencies() {
        RestClient restClient = restClientBuilder.baseUrl(nbpApiBaseUrl).build();

        try {
            Currency[] boardsResponse = restClient.get()
                    .uri("/tables/c?format=json")
                    .retrieve()
                    .body(Currency[].class);

            return Optional.ofNullable(boardsResponse)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .flatMap(currency -> currency.getRates().stream())
                    .collect(Collectors.toList());

        } catch (RestClientException e) {
            log.error("Error while fetching currencies from NBP API: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
