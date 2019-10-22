package com.wext.gatewayservice.handler;

import com.wext.common.domain.BaseResponse;
import com.wext.common.domain.exception.AuthorityLimitException;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.common.domain.exception.UserTargetException;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static Map<Class<? extends Throwable>, Integer> exceptionCodes;

    static {
        exceptionCodes = new HashMap<>();
        exceptionCodes.put(AuthorityLimitException.class, 403);
        exceptionCodes.put(NonExistentException.class, 404);
        exceptionCodes.put(InvalidOperationException.class, 400);
        exceptionCodes.put(UserTargetException.class, 404);
    }

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  ResourceProperties resourceProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse
        );
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        var excep = getError(request);
        var excepAttrs = getErrorAttributes(request, false);
        return ServerResponse.status(
                exceptionCodes.getOrDefault(excep.getClass(),
                        (Integer) excepAttrs.get("status"))
        )
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(
                        BodyInserters.fromObject(
                                BaseResponse.failResponse(excep.getMessage())
                        )
                );
    }

}
