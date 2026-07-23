package com.savbill.radius.aaa.server;

import com.savbill.radius.aaa.db.DataSource;
import com.savbill.radius.kafka.message.NasUpdateMessage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@EnableAsync
public class CustomerPortAsyncUtility {


    private static final Logger log = LoggerFactory.getLogger(CustomerPortAsyncUtility.class);
    private static final String SQL_EXCEPTION = "SQLException";

    static Executor custPortProcessExecutor = new ThreadPoolExecutor(10, 50, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(), new ThreadFactoryBuilder().setNameFormat("custPortProcessExecutor-%d").build());


    @Async
    public void nasportUpdate(String port,int custid) {
        custPortProcessExecutor.execute(new custPortProcessExecutor(port,custid));
    }
}


class custPortProcessExecutor implements Runnable{


    int custid=0;
    String port=null;

    custPortProcessExecutor(String port,int custid){
        this.custid=custid;
        this.port=port;
    }

    public void run() {
        Connection conn = null;
        PreparedStatement  stmt = null;
        try {
            Timestamp currentDate = (new Timestamp(new Date().getTime()));
            conn = DataSource.getConnection();
            stmt=conn.prepareStatement("update tblcustomers set nas_port=? where custid=?");
            stmt.setString(1,port);
            stmt.setInt(2,custid);
            stmt.executeUpdate();
            try {
                //		System.out.println("Sync NAS Port:"+port+":Cust:"+custid+":");
                NasUpdateMessage nasupdateMessage=new NasUpdateMessage(String.valueOf(custid),port,null);
                RadiusUtility radUtility=new RadiusUtility();
                radUtility.SendUpdateNASIPInfo(nasupdateMessage);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        } 
        catch(SQLException e) {
            e.printStackTrace();
        } 
        finally {
            try { if (stmt != null) stmt.close(); } catch(Exception e) {e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch(Exception e) {e.printStackTrace();}
        }

    }
}
