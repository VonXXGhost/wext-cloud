package com.wext.gatewayservice.filter;


import com.wext.common.domain.exception.UnauthorizedException;
import com.wext.gatewayservice.client.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * 验证请求的token存在与有效性，并添加用户id到header中
 * token无效时返回403
 */
@Component
@Slf4j
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Autowired
    private ObjectProvider<AuthService> authService;

    private static final String USERID_HEADER = "X-data-userID";

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var builder = exchange.getRequest().mutate();
            builder.headers(httpHeaders -> httpHeaders.remove(USERID_HEADER));  // 防止伪装攻击

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

            // allowEmptyToken
            var allowEmpty = false;
            if (config.getAllowEmptyToken() != null &&
                    config.getAllowEmptyToken().stream()
                            .anyMatch(rule -> {
                                var r = Pattern.matches(rule, path);
                                if (r) {
                                    log.debug(path + " Match allowEmptyToken rule: " + rule);
                                }
                                return r;
                            })) {
                allowEmpty = true;
            }

            try {
                boolean finalAllowEmpty = allowEmpty;
                var tokenHead = Optional
                        .ofNullable(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION))
                        .orElseThrow(() -> {
                            if (finalAllowEmpty) {
                                return new PassException(); // 允许不带token的请求通过
                            } else {
                                return new UnauthorizedException("Need login.");
                            }
                        })
                        .get(0);

                var userID = Optional
                        .ofNullable(Objects.requireNonNull(authService.getIfAvailable()).getUSerIDFromToken(tokenHead))
                        .orElseThrow(() -> new UnauthorizedException("Bad token."));

                builder.header(USERID_HEADER, userID);
                return chain.filter(exchange.mutate().request(builder.build()).build());

//            } catch (AuthorityLimitException e) {
//
//                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//                // write response body
//                var bufferFactor = exchange.getResponse().bufferFactory();
//                var dataMono = bufferFactor.wrap(
//                        JSON.toJSONBytes(BaseResponse.failResponse(e.getMessage()))
//                );
//                exchange.getResponse().getHeaders()
//                        .set("Content-Type", "application/json;charset=UTF-8");
//                return exchange.getResponse().writeWith(
//                        Mono.just(dataMono)
//                );
            } catch (PassException e) {
                return chain.filter(exchange);
            }
        };
    }

    static class Config {
        private List<String> excludePaths;      // 完全跳过
        private List<String> allowEmptyToken; // 允许不带有token，带有时仍然处理token信息

        List<String> getExcludePaths() {
            return excludePaths;
        }

        public void setExcludePaths(List<String> excludePaths) {
            this.excludePaths = excludePaths;
        }

        List<String> getAllowEmptyToken() {
            return allowEmptyToken;
        }

        public void setAllowEmptyToken(List<String> allowEmptyToken) {
            this.allowEmptyToken = allowEmptyToken;
        }
    }

    private static class PassException extends RuntimeException {
    }
}
