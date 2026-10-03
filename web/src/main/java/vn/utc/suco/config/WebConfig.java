package vn.utc.suco.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Đăng ký AuthInterceptor cho các trang cần đăng nhập (/login và /logout không bị chặn), và
 * ánh xạ thư mục lưu ảnh đính kèm sự cố ra đường dẫn URL /uploads/** (xem LuuAnh).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final String uploadDir;

    public WebConfig(AuthInterceptor authInterceptor, @Value("${app.upload-dir:uploads}") String uploadDir) {
        this.authInterceptor = authInterceptor;
        this.uploadDir = uploadDir;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/user/**", "/admin/**", "/kinh-nghiem/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String thuMuc = Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**").addResourceLocations(thuMuc);
    }
}
