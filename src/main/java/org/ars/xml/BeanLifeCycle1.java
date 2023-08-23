package org.ars.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author arsen.ibragimov
 *
 * Set init-method="init" destroy-method="destroy" properties in XML file
 */
public class BeanLifeCycle1 {

    static Logger log = LogManager.getLogger( BeanLifeCycle1.class);

    static class Bean {

        String value;

        public Bean() {
            log.info( "instantiated");
        }

        void init() {
            log.info( "init");
        }

        void destroy() {
            log.info( "destroy");
        }

        public String getValue() {
            log.info( "getValue");
            return value;
        }

        public void setValue( String value) {
            log.info( "setValue");
            this.value = value;
        }

    }

    public static void main( String[] args) {

        ClassPathXmlApplicationContext context = null;

        try {
            log.info( "main:start");

            context = new ClassPathXmlApplicationContext( "beanLifeCycle1.xml");

            // Bean bean = (Bean) context.getBean( "bean");

        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            context.close();
            log.info( "main:finish");
        }
    }
}
