package org.jsystray;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SplitUtilsTest {

    @Test
    void split() {
        // ACT
        var res = SplitUtils.split("a b c");

        // ASSERT
        assertArrayEquals(new String[]{"a", "b", "c"}, res);
    }

    private static Stream<Arguments> provideSplitString() {
        return Stream.of(
                Arguments.of("a b c", array("a", "b", "c")),
                Arguments.of("", array()),
                Arguments.of("  ", array()),
                Arguments.of("a  b     c", array("a", "b", "c")),
                Arguments.of("a \"b c\"", array("a", "\"b c\""))
        );
    }

    @ParameterizedTest
    @MethodSource("provideSplitString")
    void splitString(String s,String[] tab) {
        // ACT
        var res = SplitUtils.split(s);

        // ASSERT
        assertArrayEquals(tab, res);
    }

    // méthodes utilitaires

    private static String[] array(String... s) {
        return s;
    }
}