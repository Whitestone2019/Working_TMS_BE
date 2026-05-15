package whitestone.trainee_management.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import java.io.IOException;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE) // Forces this to execute BEFORE any security filters
public class WebCorsConfig implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String origin = request.getHeader("Origin");

        // Dynamically validate and echo back trusted origins to allow credentials securely
        if ("http://192.168.0.21:9779".equals(origin) || 
            "http://localhost:9779".equals(origin) ||
            "http://localhost:3000".equals(origin)|| 
            "https://rocket.new".equals(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, Remember-Me");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // CRITICAL: Catch the browser's preflight probe right here and return HTTP 200 OK immediately
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res); // Let POST, GET, PUT requests continue to your controller
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
