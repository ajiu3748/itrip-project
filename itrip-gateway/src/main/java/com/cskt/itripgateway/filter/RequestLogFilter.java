package com.cskt.itripgateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author mo
 * @Description: 日志记录过滤器
 * @date 2020-09-23 12:32
 */
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {


    /**
     * 日志信息
     */
    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String requestPath = exchange.getRequest().getURI().getPath();
        ServerRequest serverRequest = new DefaultServerRequest(exchange);
        HttpMethod method = exchange.getRequest().getMethod();
        //判断，如果请求路径为支付宝回调则不做任何处理，直接进入下一个过滤器
        if ("/trade/api/notify".equals(requestPath) || "/trade/api/return".equals(requestPath)) {
            logger.info("支付宝回调请求：{}，参数：{}",requestPath);
            return chain.filter(exchange);
        }
        //如果请求路径为upload,也就是上传的时候，不做任何处理，直接进入下一个过滤器
        if ("/biz/api/comment/upload".equals(requestPath)) {
            return chain.filter(exchange);
        }
        //如果是get请求，则只处理请求参数
        if (method.matches("GET")) {
            //拼接Get请求参数
            StringBuffer stringBuffer = new StringBuffer();
            MultiValueMap<String, String> queryParams = serverRequest.queryParams();
            queryParams.keySet().forEach(key -> stringBuffer
                    .append(key).append("=")
                    .append(queryParams.get(key)).append(","));
            //打印
            logger.info("request:method=[ GET ];path=[{}];param=[{}]", requestPath, stringBuffer.toString());

            //重写原始响应
            BodyHandlerServerHttpResponseDecorator responseDecorator = new BodyHandlerServerHttpResponseDecorator(
                    initBodyHandler(exchange, startTime), exchange.getResponse());

            //get请求不需要处理，直接调用余下的过滤器链
            return chain.filter(exchange.mutate().request(exchange.getRequest()).response(responseDecorator).build());
        }

        return serverRequest.bodyToMono(String.class).flatMap(reqBody -> {
            //重写原始请求
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    return httpHeaders;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                    //打印原始请求日志
                    logger.info("request:method=[ POST ];path=[{}];body=[{}]", requestPath, reqBody);
                    return Flux.just(reqBody).map(bx -> exchange.getResponse().bufferFactory().wrap(bx.getBytes()));
                }
            };
            //重写原始响应
            BodyHandlerServerHttpResponseDecorator responseDecorator = new BodyHandlerServerHttpResponseDecorator(
                    initBodyHandler(exchange, startTime), exchange.getResponse());

            return chain.filter(exchange.mutate().request(decorator).response(responseDecorator).build());
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 响应body处理，添加响应的打印
     *
     * @param exchange
     * @param startTime
     * @return
     */
    protected BodyHandlerFunction initBodyHandler(ServerWebExchange exchange, long startTime) {
        return (resp, body) -> {
            //拦截
            //ContentType 类型转换
            MediaType originalResponseContentType = MediaType
                    .parseMediaType(
                            exchange
                                    .getAttribute(
                                            ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(originalResponseContentType);
            DefaultClientResponseAdapter clientResponseAdapter = new DefaultClientResponseAdapter(body, httpHeaders);
            Mono<String> bodyMono = clientResponseAdapter.bodyToMono(String.class);
            return bodyMono.flatMap((respBody) -> {
                //打印返回响应日志
                logger.info("response:ct=[{}],status=[{}],body=[{}]",
                        System.currentTimeMillis() - startTime, resp.getStatusCode(), respBody);
                return resp.writeWith(Flux.just(respBody).map(bx -> resp.bufferFactory().wrap(bx.getBytes())));
            }).then();
        };
    }
}
