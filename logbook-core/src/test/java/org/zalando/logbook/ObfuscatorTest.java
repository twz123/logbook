package org.zalando.logbook;

/*
 * #%L
 * Logbook: Core
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import org.junit.Test;

public final class ObfuscatorTest {

    @Test
    public void noneShouldDefaultToNoOp() {
        final Obfuscator unit = Obfuscator.none();

        assertThat(unit.obfuscate("value"), is(equalTo("value")));
    }

    @Test
    public void forAnyKeyShouldObfuscateAllKeys() {
        final KeyedObfuscator unit = Obfuscator.replacement("foo").forAnyKey();

        assertThat(unit.obfuscate("press", "any key"), is(equalTo("foo")));
    }

    @Test
    public void compoundShouldObfuscateInOrder() {
        final Obfuscator unit = Obfuscator.compound(value -> "1 " + value, value -> "2 " + value);

        assertThat(unit.obfuscate("0"), is(equalTo("2 1 0")));
    }

}
