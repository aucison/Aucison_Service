package com.example.aucison_service.controller;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class SpaForwardingController implements WebMvcConfigurer {

    @Value("classpath:/static/index.html")
    private Resource indexHtml;

    @GetMapping(value = "/**/{path:[^\\.]*}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Resource index(@PathVariable String path) {
        return indexHtml;
    }


    //정적 자원에 대한 요청까지 index.html로 리다이렉트되는 것을 방지한다.
    //이렇게 설정하면 정적 자원에 대한 요청은 실제 파일 경로나 클래스패스 경로로 해석되어 해당 자원을 반환하고,
    //SPA의 라우트와 같이 파일이 아닌 경로에 대한 요청은 컨트롤러로 포워딩되어 처리된다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/index.html")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

}


