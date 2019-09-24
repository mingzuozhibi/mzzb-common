package mingzuozhibi.common.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnMissingClass({"mingzuozhibi.gateway.MzzbGatewayApplication"})
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private SecurityFilter securityFilter = new SecurityFilter();

    private SecurityHandler securityHandler = new SecurityHandler();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

            .authorizeRequests()
            .antMatchers(HttpMethod.GET).permitAll()
            .antMatchers("/api/**").hasRole("Login")

            .and().anonymous()
            .principal("Guest")
            .authorities("ROLE_Guest")

            .and().exceptionHandling()
            .accessDeniedHandler(securityHandler)
            .authenticationEntryPoint(securityHandler)

            .and().csrf().disable()

            .addFilterBefore(securityFilter, ExceptionTranslationFilter.class);

    }

}
