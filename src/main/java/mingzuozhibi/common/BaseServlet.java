package mingzuozhibi.common;

import mingzuozhibi.common.model.Result;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseServlet extends BaseResponse implements Servlet {

    public abstract void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        try {
            doService(request, response);
        } catch (Exception e) {
            responseError(response, "路由转发遇到错误: %s", Result.formatErrorCause(e));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

}
