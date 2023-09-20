package org.ars.annotation;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author arsen.ibragimov
 * creates 2 entities in separate transactions
 */
public class TransactionNew1 {
    static Logger log = LogManager.getLogger( TransactionNew1.class);

    static class Account {
        long id;
        String accountNumber;

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

    /****************************************/
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

    /****************************************/
    static interface AccountManager {
        void createAccount( Account account);

        void createAccount2( Account account);
    }

    static class AccountManagerImpl implements AccountManager {

        AccountDAO accountDAO;

        AccountManager accountManager;

        public void setAccountDAO( AccountDAO accountDAO) {
            this.accountDAO = accountDAO;
        }

        public void setAccountManager( AccountManager accountManager) {
            this.accountManager = accountManager;
        }

        @Override
        @Transactional( propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
        public void createAccount( Account account) {
            TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
            log.info( "transactionStatus:{}, status:{}", transactionStatus, transactionStatus.isNewTransaction());
            accountDAO.create( account);

            account = new Account();
            account.setAccountNumber( String.valueOf( (int) (10000 * Math.random())));
            createAccount2( account); // run in the same transaction even if method is marked as REQUIRES_NEW

            account = new Account();
            account.setAccountNumber( String.valueOf( (int) (10000 * Math.random())));
            accountManager.createAccount2( account); // run in the new transaction as method is marked as REQUIRES_NEW
        }

        @Override
        @Transactional( propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
        public void createAccount2( Account account) {
            TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
            log.info( "transactionStatus:{}, status:{}", transactionStatus, transactionStatus.isNewTransaction());
            accountDAO.create( account);
        }
    }

    /****************************************/
    public static void main( String[] args) {

        ClassPathXmlApplicationContext ctx = null;
        try {
            log.info( "main:start");

            ctx = new ClassPathXmlApplicationContext( "transactionNew1.xml");
            AccountManager accountManager = ctx.getBean( "accountManager", AccountManager.class);
            Account account = new Account();
            account.setAccountNumber( String.valueOf( (int) (10000 * Math.random())));
            accountManager.createAccount( account);

            Account account2 = new Account();
            account2.setAccountNumber( String.valueOf( (int) (10000 * Math.random())));
            accountManager.createAccount2( account2);

        } catch( Exception e) {
            log.error( e.getMessage(), e);
        } finally {
            ctx.close();
            log.info( "main:finish");
        }
    }
}
