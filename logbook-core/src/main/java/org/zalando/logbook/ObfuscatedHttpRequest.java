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

import com.google.common.collect.Multimap;

import java.io.IOException;

import static com.google.common.collect.Multimaps.transformEntries;

final class ObfuscatedHttpRequest extends ForwardingHttpRequest {

    private final HttpRequest request;
    private final KeyedObfuscator headerObfuscator;
    private final Obfuscator requestUriObfuscator;
    private final BodyObfuscator bodyObfuscator;

    ObfuscatedHttpRequest(final HttpRequest request, final KeyedObfuscator headerObfuscator,
            final Obfuscator requestUriObfuscator, final BodyObfuscator bodyObfuscator) {
        this.request = request;
        this.headerObfuscator = headerObfuscator;
        this.requestUriObfuscator = requestUriObfuscator;
        this.bodyObfuscator = bodyObfuscator;
    }

    @Override
    protected HttpRequest delegate() {
        return request;
    }

    @Override
    public String getRequestUri() {
        return requestUriObfuscator.obfuscate(super.getRequestUri());
    }

    @Override
    public Multimap<String, String> getHeaders() {
        return obfuscate(delegate().getHeaders(), headerObfuscator);
    }

    private Multimap<String, String> obfuscate(final Multimap<String, String> values, final KeyedObfuscator obfuscator) {
        return transformEntries(values, obfuscator::obfuscate);
    }

    @Override
    public byte[] getBody() throws IOException {
        return getBodyAsString().getBytes(getCharset());
    }

    @Override
    public String getBodyAsString() throws IOException {
        return bodyObfuscator.obfuscate(getContentType(), request.getBodyAsString());
    }

}
