package com.q276240802.infomask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class InfoMaskFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (ifFilterPath(requestURI)){
            log.debug("into InfoMaskFilter");
            ResponseWrapper responseWrapper = new ResponseWrapper(response);
            filterChain.doFilter(request,responseWrapper);
            byte[] content = responseWrapper.getContent();
            log.debug("before mask:"+new String(content,"UTF-8"));
            byte[] handleResult = InfoMaskHandler.handleResult(content);
            log.debug("after mask" + new String(handleResult, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(handleResult);
            outputStream.flush();
        }else {
            filterChain.doFilter(request,response);
        }
    }

    /**
     * 判断是否拦截该地址
     */
    private boolean ifFilterPath(String requestURI){
        MaskInfoEntity maskInfoEntity = InfoMaskHandler.getMaskInfoEntity();
        List<String> urls = maskInfoEntity.getUrl();
        if (urls==null||urls.size()==0){
            return false;
        }
        for (String url : urls) {
            //将url中的通配符 * 转换成正则表达式
            String regex = url.replaceAll("\\*", "\\.\\*");
            if (requestURI.matches(regex)){
                return true;
            }
        }
        return false;
    }
}
