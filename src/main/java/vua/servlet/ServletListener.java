package vua.servlet;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import vua.foundation.Bootstrap;
import javax.servlet.ServletContextEvent;

public class ServletListener extends GuiceServletContextListener {

    private Bootstrap bootstrap;
    private String contextPath;
    private Class startClass;

    public ServletListener(Class startClass) {
        this.startClass = startClass;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        contextPath = servletContextEvent.getServletContext().getContextPath();
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        bootstrap.shutdown();
        super.contextDestroyed(servletContextEvent);
    }

    @Override
    protected Injector getInjector() {
        Bootstrap bootstrapLocal = bootstrap;

        if (bootstrapLocal == null) {

            synchronized (this) {

                bootstrapLocal = bootstrap;

                if (bootstrapLocal == null) {
                    bootstrap = createBootstrap(contextPath);
                    bootstrapLocal = bootstrap;
                }
            }
        }

        return bootstrapLocal.getInjector();
    }

    private Bootstrap createBootstrap(String contextPath) {
        Bootstrap bootstrap = new ServletBootstrap(startClass);
        bootstrap.boot();

        return bootstrap;
    }
}
