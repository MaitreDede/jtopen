///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: AS400JDBCPooledConnection.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

/**
*  The AS400JDBCPooledConnection class represents a connection object
*  that provides hooks for connection pool management.
*
*  The following example creates an AS400JDBCPooledConnection object that can be used to cache JDBC connections.
*
*  <pre><blockquote>
*  // Create a data source for making the connection.
*  AS400JDBCConnectionPoolDataSource dataSource = new AS400JDBCConnectionPoolDataSource("myAS400");
*  datasource.setUser("Mickey Mouse");
*  datasource.setPassword("IAMNORAT");
*
*  // Get a PooledConnection and get the connection handle to the database.
*  AS400JDBCPooledConnection pooledConnection = datasource.getPooledConnection();
*  Connection connection = pooledConnection.getConnection();
*
*  ... work with the connection handle.
*
*  // Close the connection handle to make available for reuse (physical connection not closed).
*  connection.close();
*
*  // Reuse the connection somewhere else.
*  Connection reusedConnection = pooledConnection.getConnection();
*  ... work with the connection handle.
*  reusedConnection.close();
*
*  // Close the physical connection.
*  pooledConnection.close();  
*  </blockquote></pre>
*
*  <p>
*  AS400JDBCPooledConnection objects generate the following events:
*  <UL>
*  <li>javax.sql.ConnectionEvent - The events fired are:</li>
*    <ul>
*       <li>connectionClosed</li>
*       <li>connectionErrorOccurred</li>
*    </ul>
*  </ul>
**/
public class AS400JDBCPooledConnection implements PooledConnection
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

   private Connection connection_;                          // The database connection.
   private AS400JDBCConnectionHandle handle_;               // The handle to the connection.

   private PoolItemProperties properties_;                  // The usage properties.
   private AS400JDBCConnectionEventSupport eventManager_;

   /**
   *  Constructs an AS400JDBCPooledConnection object.
   *  @param connection The physical connection to be pooled.
   *  @exception SQLException If a database error occurs.
   **/
   AS400JDBCPooledConnection(Connection connection) throws SQLException
   {
      if (connection == null) 
         throw new NullPointerException("connection");
      connection_ = connection;

      properties_ = new PoolItemProperties();
      eventManager_ = new AS400JDBCConnectionEventSupport();
   }

   /**
   *  Adds a ConnectionEventListener.
   *  @param listener The listener.
   **/
   public void addConnectionEventListener(ConnectionEventListener listener)
   {
      eventManager_.addConnectionEventListener(listener);
   }

   /**
   *  Closes the physical connection.
   *  @exception SQLException If an error occurs closing the connection.
   **/
   public void close() throws SQLException
   {
      if (connection_.isClosed()) 
         return;
         
      if (handle_ != null) 
         handle_.invalidate();            // notify the handle.
      ((AS400JDBCConnection)connection_).pseudoClose();                    // @A3C

      properties_.clear();                // Reset the usage timers.

      if (Trace.isTraceOn()) 
         Trace.log(Trace.INFORMATION, "Pooled Connection closed.");
   }

   /**
   *  Fire the connection closed event.
   *  @param event The ConnectionEvent.
   **/
   void fireConnectionCloseEvent(ConnectionEvent event)
   {
      returned();                                     // Reset the pooledConnection.
      eventManager_.fireCloseEvent(event);            // Notify the pool.
   }

   /**
   *  Returns the connection handle to the database.
   *  @return The connection handle.
   *  @exception SQLException If a database error occurs. 
   **/
   public Connection getConnection() throws SQLException
   {
      if (Trace.isTraceOn()) 
         Trace.log(Trace.INFORMATION, "AS400PooledConnection.getConnection()");

      if (connection_.isClosed()) 
      {
         Trace.log(Trace.ERROR, "Pooled Connection is invalid.");
         throw new ExtendedIllegalStateException("connection", ExtendedIllegalStateException.OBJECT_MUST_BE_OPEN);
      }
      if (handle_ != null) 
      {
         if (Trace.isTraceOn()) 
            Trace.log(Trace.INFORMATION, "AS400PooledConnection.getConnection().  Closing existing connection handle.");

         handle_.invalidate();
      }

      handle_ = new AS400JDBCConnectionHandle(this, (AS400JDBCConnection)connection_);

      // Start the connection tracking timers.
      setInUse(true);

      return handle_;
   }

   /**
   *  Returns the elapsed time the connection has been idle waiting in the pool.
   *  @return The idle time.
   **/
   public long getInactivityTime()
   {
      return properties_.getInactivityTime();
   }
   
   /**
   *  Returns the elapsed time the connection has been in use.
   *  @return The elapsed time.
   **/
	public long getInUseTime()
	{            
      return properties_.getInUseTime();
	}
	
   /**
   *  Returns the elapsed time the pooled connection has been alive.
   *  @return The elapsed time.
   **/
	public long getLifeSpan()
	{
		return properties_.getLifeSpan();
	}
	
   /**
   *  Returns the number of times the pooled connection has been used.
   *  @return The number of times used.
   **/
   public int getUseCount()
   {
      return properties_.getUseCount();
   }

   /**
   *  Ping the connection to check the status.
   *  @return true if the connection is active; false otherwise.
   **/
   boolean isConnected()
   throws SQLException // @A3A
   {
      AS400JDBCConnection c = (AS400JDBCConnection)connection_;
      return c.getAS400().isConnected(AS400.DATABASE);
   }

   /**
   *  Indicates if the pooled connection is in use.
   *  @return true if the pooled connection is in use; false otherwise.
   **/
   public boolean isInUse()
   {
      return properties_.isInUse();
   }

   /**
   *  Removes a ConnectionEventListener.
   *  @param listener The listener to be removed.
   **/
   public void removeConnectionEventListener(ConnectionEventListener listener)
   {
      eventManager_.removeConnectionEventListener(listener);
   }

   /**
   *  Returns the connection after usage. 
   *  Update the connection timers and invalidate connection handle.
   **/
   void returned()
   {
      if (Trace.isTraceOn()) 
         Trace.log(Trace.INFORMATION, "Pooled Connection is being returned.");

      setInUse(false);              // Reset the timers.

      if (handle_ != null) 
         handle_.invalidate();      // Invalidate the handle.
   }

   /**
   *  Sets the connection timer values based on the active usage state of the connection.
   *  @param inUse true if the connection is currently active; false otherwise.
   **/
   void setInUse(boolean inUse)
   {
      properties_.setInUse(inUse);
   }
}
