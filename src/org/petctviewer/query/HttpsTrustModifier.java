

/**
 * Orthanc - A Lightweight, RESTful DICOM Store
 * Copyright (C) 2012-2016 Sebastien Jodogne, Medical Physics
 * Department, University Hospital of Liege, Belgium
 * Copyright (C) 2017 Osimis, Belgium
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.petctviewer.query;

import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;

public class HttpsTrustModifier 
{
  private static final AlwaysTrustHostname trustHostname_ = new AlwaysTrustHostname();
  private static SSLSocketFactory trustSocket_;

  private static synchronized SSLSocketFactory GetAlwaysTrustSocket(HttpsURLConnection httpsConnection)
    throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException 
  {
    // Singleton pattern to create one single trust manager
    if (trustSocket_ == null) 
    {
      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(null, new TrustManager[] { new AlwaysTrustManager() }, null);
      trustSocket_ = ctx.getSocketFactory();
    }

    return trustSocket_;
  }


  private static final class AlwaysTrustHostname implements HostnameVerifier
  {
    public boolean verify(String hostname, SSLSession session)
    {
      return true;
    }
  }


  private static class AlwaysTrustManager implements X509TrustManager 
  {
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
    {
    }

    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
    {
    }

    public X509Certificate[] getAcceptedIssuers()
    { 
      return null; 
    }
  }


  public static void Trust(URLConnection conn)
    throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException 
  {
    if (conn instanceof HttpsURLConnection) 
    {
      HttpsURLConnection httpsConnection = (HttpsURLConnection) conn;
      httpsConnection.setSSLSocketFactory(GetAlwaysTrustSocket(httpsConnection));
      httpsConnection.setHostnameVerifier(trustHostname_);
    }
  }
}