package org.ars.xml;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author arsen.ibragimov
 *
 * use @PostConstruct, @PreDestroy annotations
 * added '<context:annotation-config />' to beanLifeCycle3.xml
 */
public class BeanLifeCycle3 {

    static Logger log = LogManager.getLogger( BeanLifeCycle3.class);

    static class A {

        String value;

        public A() {
            log.info( "instantiated");
        }

        public String getValue() {
            log.info( "getValue");
            return value;
        }

        public void setValue( String value) {
            log.info( "setValue");
            this.value = value;
        }

        @PostConstruct
        public void init() {
            log.info( "init");
        }

        @PreDestroy
        public void destroy() {
            log.info( "destroy");
        }
    }

    public static void main( String[] args) {

        ClassPathXmlApplicationContext context = null;

        try {
            log.info( "main:start");

            context = new ClassPathXmlApplicationContext( "beanLifeCycle3.xml");

        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            context.close();
            log.info( "main:finish");
        }
    }
}
