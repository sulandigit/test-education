/*
 * 注意：由于Knife4j 4.x版本变更，此Swagger聚合配置需要重新实现
 * 建议使用各服务独立的API文档，或者按新的Knife4j 4.x方式配置网关聚合
 * 临时注释掉此配置以避免编译错误
 */
package com.roncoo.education.gateway.swagger;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.cloud.gateway.config.GatewayProperties;
// import org.springframework.cloud.gateway.route.RouteLocator;
// import org.springframework.context.annotation.Primary;
// import org.springframework.stereotype.Component;
// import springfox.documentation.swagger.web.SwaggerResource;
// import springfox.documentation.swagger.web.SwaggerResourcesProvider;

// import java.util.ArrayList;
// import java.util.List;

/**
 * @author fengyw
 * 
 * 该配置类已临时注释，需要按照Knife4j 4.x的方式重新实现网关聚合文档
 */
// @Slf4j
// @Component
// @Primary
// @AllArgsConstructor
// public class SwaggerResourceConfig implements SwaggerResourcesProvider {

//     private static final String LOCATION = "/v3/api-docs";
//     private static final String VERSION = "3.0";

//     private final RouteLocator routeLocator;
//     private final GatewayProperties gatewayProperties;

//     @Override
//     public List<SwaggerResource> get() {
//         List<SwaggerResource> resources = new ArrayList<>();
//         List<String> routes = new ArrayList<>();
//         routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
//         gatewayProperties.getRoutes().stream()
//                 .filter(routeDefinition -> routes.contains(routeDefinition.getId()))
//                 .forEach(route -> route.getPredicates().forEach(
//                         predicateDefinition -> resources.add(swaggerResource(route.getId(), LOCATION))
//                 ));
//         return resources;
//     }

//     private SwaggerResource swaggerResource(String name, String location) {
//         SwaggerResource swaggerResource = new SwaggerResource();
//         swaggerResource.setName(name);
//         swaggerResource.setLocation("/" + name + location);
//         swaggerResource.setSwaggerVersion(VERSION);
//         return swaggerResource;
//     }
// }
