package org.ars.annotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author arsen.ibragimov
 *
 */
public class Singleton1 {

    static Logger log = LogManager.getLogger( Singleton1.class);

    static class A {
        @Autowired
        private B b;
        @Autowired
        private B b1;

        public B getB() {
            return b;
        }

        public B getB1() {
            return b1;
        }
    }

    static class B {
    }

    @Configuration
    public static class AppConfig {

        @Bean
        public A a() {
            return new A();
        }

        @Bean
        public B b() {
            return new B();
        }
    }

    public static void main( String[] args) {
        try {
            log.info( "main:start");

            ConfigurableApplicationContext context = new AnnotationConfigApplicationContext( AppConfig.class);

            A a = (A) context.getBean( "a");

            log.info( "---- same objects for each call ----");
            log.info( "a.getB(): {}", a.getB());
            log.info( "a.getB(): {}", a.getB());
            log.info( "a.getB1(): {}", a.getB1());
            log.info( "a.getB1(): {}", a.getB1());
            log.info( "getBean(b): {}", context.getBean( "b"));
            log.info( "getBean(b): {}", context.getBean( "b"));

            context.close();
        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            log.info( "main:finish");
        }
    }
}
