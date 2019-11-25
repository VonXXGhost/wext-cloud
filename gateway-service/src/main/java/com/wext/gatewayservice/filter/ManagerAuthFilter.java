package com.wext.gatewayservice.filter;

import com.wext.common.domain.exception.UnauthorizedException;
import com.wext.gatewayservice.client.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ManagerAuthFilter extends AbstractGatewayFilterFactory<ManagerAuthFilter.Config> {

    public ManagerAuthFilter() {
        super(Config.class);
    }

    @Autowired
    private ObjectProvider<AuthService> authService;

    static class Config {
        private List<String> excludePaths;      // 完全跳过

        List<String> getExcludePaths() {
            return excludePaths;
        }

        public void setExcludePaths(List<String> excludePaths) {
            this.excludePaths = excludePaths;
        }
    }

    private static final String MANAGER_ID_KEY = "managerID";

    @Override
    public GatewayFilter apply(ManagerAuthFilter.Config config) {
        return (exchange, chain) -> {
            var builder = exchange.getRequest().mutate();

            var path = exchange.getRequest().getPath().toString();

            // exclude
            if (config.getExcludePaths() != null &&
                    config.getExcludePaths().stream()
                            .anyMatch(rule -> {
                                var r = Pattern.matches(rule, path);
                                if (r) {
                                    log.debug(path + " Match exclude rule: " + rule);
                                }
                                return r;
                            })) {
                return chain.filter(exchange);
            }

            var tokenHead = Optional
                    .ofNullable(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION))
                    .orElseThrow(() -> new UnauthorizedException("Need login."))
                    .get(0);
            var managerID = Optional
                    .ofNullable(Objects.requireNonNull(authService.getIfAvailable()).getManagerIDFromToken(tokenHead))
                    .orElseThrow(() -> new UnauthorizedException("Bad token."));

            // 修改cookies
            var cookies = exchange.getRequest().getCookies();
            String cookiesString = "";
            for (var cs : cookies.values()) {
                cookiesString = cs.stream()
                        .filter(cookie -> !cookie.getName().equals(MANAGER_ID_KEY))
                        .map(Object::toString)
                        .reduce(cookiesString, (res, cookie) -> res + cookie + ";");
            }
            cookiesString += new HttpCookie(MANAGER_ID_KEY, managerID).toString() + ";";
            String finalCookiesString = cookiesString;
            builder.headers(httpHeaders ->
                    httpHeaders.set("Cookie", finalCookiesString));
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }
}
