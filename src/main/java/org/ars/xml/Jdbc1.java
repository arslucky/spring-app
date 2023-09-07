package org.ars.xml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author arsen.ibragimov
 *
 * Spring JDBC Example - Bean Configuration
 */
public class Jdbc1 {

    static Logger log = LogManager.getLogger( Jdbc1.class);

    static interface CounterDAO {
        Counter getById( int id);

        int update( int id, int oldCount, int newCount);
    }

    static class CounterDAOImpl implements CounterDAO {

        private DataSource dataSource;

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource( DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Counter getById( int id) {
            String query = "select id, counter from test where id = ?";
            Counter counter = null;
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = dataSource.getConnection();
                ps = con.prepareStatement( query);
                ps.setInt( 1, id);
                rs = ps.executeQuery();
                if( rs.next()) {
                    counter = new Counter();
                    counter.setId( rs.getInt( "id"));
                    counter.setCounter( rs.getInt( "counter"));
                    // log.info( "Counter Found::{}", counter);
                } else {
                    // log.warn( "No counter found with id={}", id);
                }
            } catch( SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    rs.close();
                    ps.close();
                    con.close();
                } catch( SQLException e) {
                    e.printStackTrace();
                }
            }
            return counter;
        }

        @Override
        public int update( int id, int oldCount, int newCount) {
            String query = "update test set counter=? where id=? and counter=?";
            Connection con = null;
            PreparedStatement ps = null;
            int res = 0;
            try {
                con = dataSource.getConnection();
                ps = con.prepareStatement( query);
                ps.setInt( 1, newCount);
                ps.setInt( 2, id);
                ps.setInt( 3, oldCount);
                res = ps.executeUpdate();
            } catch( SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    ps.close();
                    con.close();
                } catch( SQLException e) {
                    e.printStackTrace();
                }
            }
            return res;
        }
    }

    static class Counter {
        private int id;
        private int counter;

        public long getId() {
            return id;
        }

        public void setId( int id) {
            this.id = id;
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter( int counter) {
            this.counter = counter;
        }

        @Override
        public String toString() {
            return "Counter [id=" + id + ", counter=" + counter + "]";
        }
    }

    public static void main( String[] args) {

        ClassPathXmlApplicationContext ctx = null;
        try {
            log.info( "main:start");

            ctx = new ClassPathXmlApplicationContext( "jdbc1.xml");
            final CounterDAO counterDao = ctx.getBean( CounterDAO.class);

            Runnable run = () -> {
                int updatedCount = 0;
                for( int i = 0; i < 10; i++) {
                    int counter = counterDao.getById( 1).getCounter();
                    updatedCount += counterDao.update( 1, counter, ++counter);
                }
                log.info( "updated:{}", updatedCount);
            };

            Thread thread1 = new Thread( run);
            Thread thread2 = new Thread( run);

            thread1.start();
            thread2.start();

        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            ctx.close();
            log.info( "main:finish");
        }
    }
}
