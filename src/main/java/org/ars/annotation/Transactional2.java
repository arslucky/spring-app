package org.ars.annotation;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author arsen.ibragimov
 *
 * 1 - not saved
 * 2 - not saved
 * 3 - saved
 * 4 - not saved
 */
public class Transactional2 {
    static Logger log = LogManager.getLogger( Transactional2.class);

    static class Account {
        long id;
        String accountNumber;

        public Account( String accountNumber) {
            super();
            this.accountNumber = accountNumber;
        }

        public long getId() {
            return id;
        }

        public void setId( long id) {
            this.id = id;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber( String accountNumber) {
            this.accountNumber = accountNumber;
        }
    }

    static interface AccountDAO {
        void create( Account account);
    }

    static class AccountDAOImpl implements AccountDAO {

        DataSource dataSource;

        public void setDataSource( DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void create( Account account) {
            String insert = "insert into account(account_number) values(?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource);
            jdbcTemplate.update( insert, new Object[] { account.getAccountNumber() });
        }
    }

    static interface AccountManager {
        void createAccount1( Account account1, Account account2);

        void createAccount2( Account account3, Account account4);
    }

    static class AccountManagerImpl implements AccountManager {

        AccountDAO accountDAO;

        public void setAccountDAO( AccountDAO accountDAO) {
            this.accountDAO = accountDAO;
        }

        @Override
        @Transactional
        public void createAccount1( Account account1, Account account2) {
            accountDAO.create( account1);
            if( true) {
                throw new RuntimeException( "test");
            }
            accountDAO.create( account2);
        }

        @Override
        public void createAccount2( Account account3, Account account4) {
            this.createAccount1( account3, account4);
        }
    }

    public static void main( String[] args) {

        ClassPathXmlApplicationContext ctx = null;
        try {
            log.info( "main:start");

            ctx = new ClassPathXmlApplicationContext( "transactional2.xml");
            AccountManager accountManager = ctx.getBean( "accountManager", AccountManagerImpl.class);
            Account account1 = new Account( "1");
            Account account2 = new Account( "2");

            try {
                accountManager.createAccount1( account1, account2);
            } catch( Exception e) {
                log.error( e.getMessage(), e);
            }

            Account account3 = new Account( "3");
            Account account4 = new Account( "4");

            try {
                accountManager.createAccount2( account3, account4);
            } catch( Exception e) {
                log.error( e.getMessage(), e);
            }

        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            ctx.close();
            log.info( "main:finish");
        }
    }
}
