package org.ars.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author arsen.ibragimov
 *
 * implements InitializingBean, DisposableBean
 */
public class BeanLifeCycle2 {

    static Logger log = LogManager.getLogger( BeanLifeCycle2.class);

    static class Bean implements InitializingBean, DisposableBean {

        String value;

        static {
            log.info( "instantiation");
        }

        public Bean() {
            log.info( "initialization");
        }

        public String getValue() {
            log.info( "getValue");
            return value;
        }

        public void setValue( String value) {
            log.info( "setValue");
            this.value = value;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info( "afterPropertiesSet");
        }

        @Override
        public void destroy() throws Exception {
            log.info( "destroy");
        }

    }

    public static void main( String[] args) {

        ClassPathXmlApplicationContext context = null;

        try {
            log.info( "main:start");

            context = new ClassPathXmlApplicationContext( "beanLifeCycle2.xml");

        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            context.close();
            log.info( "main:finish");
        }
    }
}
