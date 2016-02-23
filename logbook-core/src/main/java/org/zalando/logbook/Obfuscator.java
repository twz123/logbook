package org.zalando.logbook;

/*
 * #%L
 * Logbook
 * %%
 * Copyright (C) 2015 Zalando SE
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Obfuscates {@code Strings}.
 */
@FunctionalInterface
public interface Obfuscator {

    /**
     * Returns the obfuscated representation for the given {@code value}.
     */
    String obfuscate(final String value);

    static Obfuscator none() {
        return value -> value;
    }

    /**
     * Returns an {@code Obfuscator} that replaces all values with the given {@code replacement}.
     */
    static Obfuscator replacement(final String replacement) {
        return value -> replacement;
    }

    /**
     * Returns a {@code KeyedObfuscator} that applies this {@code Obfuscator} to the values of any key-value pair.
     */
    default KeyedObfuscator forAnyKey() {
        return (key, value) -> obfuscate(value);
    }

    /**
     * Returns a {@code KeyedObfuscator} that applies this {@code Obfuscator} to
     * the values of key-value pairs whose keys match {@code keyPredicate}.
     *
     * @throws NullPointerException if {@code keyPredicate} is {@code null}
     */
    default KeyedObfuscator forKeys(final Predicate<String> keyPredicate) {
        requireNonNull(keyPredicate);
        return (key, value) -> keyPredicate.test(key) ? obfuscate(value) : value;
    }

    /**
     * Returns a {@code KeyedObfuscator} that applies this {@code Obfuscator} to
     * the values of key-value pairs that match {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    default KeyedObfuscator forPairs(final BiPredicate<String, String> predicate) {
        requireNonNull(predicate);
        return (key, value) -> predicate.test(key, value) ? obfuscate(value) : value;
    }

    static Obfuscator compound(final Obfuscator... obfuscators) {
        return compound(asList(obfuscators));
    }

    static Obfuscator compound(final Iterable<Obfuscator> obfuscators) {
        return StreamSupport.stream(obfuscators.spliterator(), false)
                .reduce(none(), (left, right) ->
                        (value) ->
                                right.obfuscate(left.obfuscate(value)));
    }

}
