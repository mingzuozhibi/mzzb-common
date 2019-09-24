package mingzuozhibi.common.security;

import mingzuozhibi.common.model.Content;
import mingzuozhibi.common.spider.SpiderJsoup;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String username = request.getHeader("X-USERNAME");
        if (StringUtils.hasText(username)) {
            checkAuthentication(username);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void checkAuthentication(String username) {
        String url = "http://localhost:9999/userDetails/" + username;
        Content content = Content.parse(SpiderJsoup.waitRequest(url));
        if (content.isSuccess()) {
            UserDetails userDetails = content.parseData(SimpleUserDetails.class);
            Authentication authentication = new SimpleAuthentication(userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

}
