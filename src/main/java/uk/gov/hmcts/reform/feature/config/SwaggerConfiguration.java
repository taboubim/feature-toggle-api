package uk.gov.hmcts.reform.feature.config;

import org.ff4j.spring.boot.web.api.config.SwaggerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SwaggerConfig.class)
public class SwaggerConfiguration {
}
