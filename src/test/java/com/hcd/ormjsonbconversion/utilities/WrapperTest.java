package com.hcd.ormjsonbconversion.utilities;

import com.asentinel.common.lang.ExceptionWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class WrapperTest {

    private final List<TwoDigitsInteger> numbers = List.of(new TwoDigitsInteger(10),
            new TwoDigitsInteger(25),
            new TwoDigitsInteger(37));

    @Test
    void sumExpression_ExceptionWrapper() {
        String result = numbers.stream()
                .map(ExceptionWrapper.apply(TwoDigitsInteger::getValue))
                .map(String::valueOf)
                .collect(Collectors.joining("+"));

        Assertions.assertEquals("10+25+37", result);
    }

    @Test
    void sumExpression() {
        StringJoiner joiner = new StringJoiner("+");
        for (TwoDigitsInteger number : numbers) {
            try {
                joiner.add(String.valueOf(number.getValue()));
            } catch (NotSetException e) {
                throw new RuntimeException(e);
            }
        }
        String result = joiner.toString();

        Assertions.assertEquals("10+25+37", result);
    }

    @Test
    void count() {
        long count = IntStream.range(0, 150)
                .mapToObj(TwoDigitsInteger::new)
                .filter(ExceptionWrapper.test(TwoDigitsInteger::isValid))
                .count();

        Assertions.assertEquals(90, count);
    }
}
