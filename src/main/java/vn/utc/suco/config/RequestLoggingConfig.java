package vn.utc.suco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Ghi log cho từng HTTP request (phương thức, đường dẫn, client) ở mức DEBUG,
 * đọc được qua console hoặc "docker logs -f suco-app".
 * Không đọc body/payload để không ảnh hưởng upload ảnh (multipart),
 * không log header để tránh lộ Cookie/Session vào log.
 */
@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(false);
        filter.setIncludePayload(false);
        return filter;
    }
}
