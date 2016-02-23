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

final class Obfuscation {

    private final KeyedObfuscator headerObfuscator;
    private final Obfuscator requestUriObfuscator;
    private final BodyObfuscator bodyObfuscator;

    Obfuscation(final KeyedObfuscator headerObfuscator, final Obfuscator requestUriObfuscator,
            final BodyObfuscator bodyObfuscator) {
        this.headerObfuscator = headerObfuscator;
        this.requestUriObfuscator = requestUriObfuscator;
        this.bodyObfuscator = bodyObfuscator;
    }

    HttpRequest obfuscate(final HttpRequest request) {
        return new ObfuscatedHttpRequest(request, headerObfuscator, requestUriObfuscator, bodyObfuscator);
    }

    HttpResponse obfuscate(final HttpResponse response) {
        return new ObfuscatedHttpResponse(response, headerObfuscator, bodyObfuscator);
    }

}
