///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: PxConnectTunnelReqCV.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

//
// Tunneling -- HTTP is stateless so a mechanism is required to identify an
// instance of the client to the server.  The mechanism is the client ID.
// An eight byte ID is appended to the end of data stream if connected
// to the server via tunneling.  The ID is generated by the server and returned
// to the client with the connect reply.  This class is the connect via tunnel
// request.  In addition to doing normal connect processing the server will
// create a client ID.
//

/**
The PxConnectTunnelReqCV class represents the
client view of a connect via HTTP tunnel request.
**/
class PxConnectTunnelReqCV
extends PxReqCV
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";




/**
Constructs a PxConnectTunnelReqCV object.
**/
    public PxConnectTunnelReqCV ()
    {
        super (ProxyConstants.DS_CONNECT_TUNNEL_REQ);
    }

}
