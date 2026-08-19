package com.finance.moneyowl.utils;

import com.finance.moneyowl.service.interfaces.assetWiseExtractor.FiTypeExtractor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FiTypeExtractorRegistry {

    private final Map<String, FiTypeExtractor> extractorsByFiType;

    public FiTypeExtractorRegistry(List<FiTypeExtractor> extractors) {
        this.extractorsByFiType = extractors.stream()
                .collect(Collectors.toUnmodifiableMap(e -> e.getFiType().toLowerCase(), Function.identity()));
    }

    public Optional<FiTypeExtractor> get(String fiType) {
        return Optional.ofNullable(extractorsByFiType.get(fiType.toLowerCase()));
    }
}
