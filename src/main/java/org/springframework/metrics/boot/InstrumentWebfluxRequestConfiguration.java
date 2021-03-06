/**
 * Copyright 2017 Pivotal Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.metrics.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.metrics.instrument.MeterRegistry;
import org.springframework.metrics.instrument.TagFormatter;
import org.springframework.metrics.instrument.web.*;

/**
 * Instrument Spring Webflux annotation-based programming model request mappings.
 *
 * @author Jon Schneider
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Configuration
public class InstrumentWebfluxRequestConfiguration {
    @Autowired(required = false)
    WebfluxTagConfigurer tagConfigurer;

    @Bean
    @ConditionalOnMissingBean(WebfluxTagConfigurer.class)
    WebfluxTagConfigurer webfluxTagConfigurer(TagFormatter tagFormatter) {
        if(tagConfigurer != null)
            return tagConfigurer;
        this.tagConfigurer = new WebfluxTagConfigurer(tagFormatter);
        return this.tagConfigurer;
    }

    @Bean
    public MetricsWebFilter webfluxMetrics(MeterRegistry registry, TagFormatter tagFormatter, Environment environment) {
        return new MetricsWebFilter(registry, webfluxTagConfigurer(tagFormatter),
                environment.getProperty("spring.metrics.web.server_requests.name", "http_server_requests"));
    }
}
