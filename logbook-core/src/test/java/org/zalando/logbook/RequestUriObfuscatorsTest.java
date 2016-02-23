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

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class RequestUriObfuscatorsTest {

    private final Obfuscator unit = RequestUriObfuscators.obfuscateQueryString(
            Obfuscator.replacement("unknown").forKeys("password"::equalsIgnoreCase));

    @Test
    public void shouldNotObfuscateEmptyQueryString() {
        final String uri = "http://localhost/";

        assertThat(unit.obfuscate(uri), is(uri));
    }

    @Test
    public void shouldObfuscatePasswordButNotLimitParameter() {
        final String uri = "http://localhost/?password=s3cr3t&limit=1";

        assertThat(unit.obfuscate(uri), containsString("?password=unknown&"));
    }

    @Test
    public void shouldNotObfuscateLimitParameter() {
        final String uri = "http://localhost/?password=s3cr3t&limit=1";

        assertThat(unit.obfuscate(uri), endsWith("&limit=1"));
    }

    @Test
    public void shouldObfuscateInvalidQueryParameters() {
        final String invalidUri = "http://localhost/vulnerable.cgi?password=.|.%2F.|.%2F.|.%2F.|.%2F.|.%2F.|.%2Fetc%2Fpasswd#fragment";

        assertThat(unit.obfuscate(invalidUri), endsWith("/vulnerable.cgi?password=unknown#fragment"));
    }

    @Test
    public void shouldNotFailOnInvalidPathsWithEmptyQueryString() {
        final String invalidUri = "http://localhost/unterminated_percent_%F?";

        assertThat(unit.obfuscate(invalidUri), endsWith("/unterminated_percent_%F?"));
    }

    @Test
    public void shouldNotFailOnInvalidPathsWithQueryStringOnly() {
        final String invalidUri = "/unterminated_percent_%F?q";

        assertThat(unit.obfuscate(invalidUri), endsWith("/unterminated_percent_%F?q"));
    }

    @Test
    public void shouldNotFailOnInvalidPathsWithFragmentOnly() {
        final String invalidUri = "http://localhost/unterminated_percent_%F#";

        assertThat(unit.obfuscate(invalidUri), endsWith("/unterminated_percent_%F#"));
    }

}
