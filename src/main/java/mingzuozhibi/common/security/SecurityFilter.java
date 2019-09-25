package mingzuozhibi.common.security;

import mingzuozhibi.common.BaseFilter;
import mingzuozhibi.common.model.Content;
import mingzuozhibi.common.spider.SpiderJsoup;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityFilter extends BaseFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String username = request.getHeader("X-USERNAME");
        if (StringUtils.hasText(username)) {
            String url = "http://localhost:9999/userDetails/" + username;
            Content content = Content.parse(SpiderJsoup.waitRequest(url));
            if (!content.isSuccess()) {
                responseError(response, "用户服务不可用");
                return;
            }
            UserDetails userDetails = content.parseData(SimpleUserDetails.class);
            Authentication authentication = new SimpleAuthentication(userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

}
