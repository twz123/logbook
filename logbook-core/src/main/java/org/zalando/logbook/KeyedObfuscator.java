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

/**
 * Obfuscates values of key-value pairs.
 */
@FunctionalInterface
public interface KeyedObfuscator {

    /**
     * Returns the obfuscated value for the given key-value pair.
     */
    String obfuscate(final String key, final String value);

    static KeyedObfuscator none() {
        return (key, value) -> value;
    }

    static KeyedObfuscator compound(final KeyedObfuscator... obfuscators) {
        return compound(asList(obfuscators));
    }

    static KeyedObfuscator compound(final Iterable<KeyedObfuscator> obfuscators) {
        return StreamSupport.stream(obfuscators.spliterator(), false)
                .reduce(none(), (left, right) ->
                        (key, value) ->
                                left.obfuscate(key, right.obfuscate(key, value)));
    }

    static KeyedObfuscator authorization() {
        return Obfuscator.replacement("XXX").forKeys("Authorization"::equalsIgnoreCase);
    }

}
