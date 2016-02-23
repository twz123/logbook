package org.zalando.logbook;

/*
 * #%L
 * Logbook: Core
 * %%
 * Copyright (C) 2015 - 2016 Zalando SE
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

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;

public final class RequestUriObfuscators {

    // TODO Handle encoding/decoding of URL escape sequences in a way that the
    // parameterObfuscator sees decoded key-value pairs, but the obfuscated
    // request-target uses the original representation
    public static Obfuscator obfuscateQueryString(final KeyedObfuscator parameterObfuscator) {
        requireNonNull(parameterObfuscator);

        return requestTarget ->
                parseUri(requestTarget).map(uri -> obfuscateUri(uri, parameterObfuscator))
                                       .orElseGet(() -> lenientObfuscateUri(requestTarget, parameterObfuscator));
    }

    private static String obfuscateUri(final URI uri, final KeyedObfuscator parameterObfuscator) {
        final QueryParameters parameters = QueryParameters.parse(uri.getQuery());

        if (parameters.isEmpty()) {
            return uri.toASCIIString();
        }

        final String queryString = parameters.obfuscate(parameterObfuscator).toString();

        return createUri(uri, queryString).toASCIIString();
    }

    private static String lenientObfuscateUri(final String requestTarget, final KeyedObfuscator parameterObfuscator) {
        final int startOfQuery = requestTarget.indexOf('?');

        if (startOfQuery < 0) {
            return requestTarget;
        }

        final int startOfFragment = requestTarget.indexOf('#', startOfQuery);
        final String query = startOfFragment < 0 ? requestTarget.substring(startOfQuery + 1)
                                                 : requestTarget.substring(startOfQuery + 1, startOfFragment);

        final QueryParameters parameters = QueryParameters.parse(query);

        if (parameters.isEmpty()) {
            return requestTarget;
        }

        final String queryString = parameters.obfuscate(parameterObfuscator).toString();
        return startOfFragment < 0
            ? requestTarget.substring(0, startOfQuery + 1) + queryString
            : requestTarget.substring(0, startOfQuery + 1) + queryString + requestTarget.substring(startOfFragment);
    }

    private static Optional<URI> parseUri(final String uri) {
        try {
            return Optional.of(new URI(uri));
        } catch (final URISyntaxException invalid) {
            return Optional.empty();
        }
    }

    @VisibleForTesting
    @SuppressWarnings("ConstantConditions")
    static URI createUri(@Nullable final URI uri, final String queryString) {
        try {
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), queryString, uri.getFragment());
        } catch (@Hack("Just so we can trick the code coverage") @OhNoYouDidnt final Exception e) {
            throw new AssertionError(e);
        }
    }

    private RequestUriObfuscators() {
        throw new AssertionError("No instances for you!");
    }
}
