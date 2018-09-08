package de.medys.ldap.https;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import de.medys.ldap.ssl.SSLSocketFactoryEx;
import de.medys.ldap.utils.InstallCert;


public class MedysLdapHttpsConnect {
	
	private boolean isConnected;
	
	private HttpsURLConnection serverConn;
	
	private InstallCert instCert;
	
	/**
	 * Erzeugt eine neue Instanz von {@link MedysLdapHttpsConnect} 
	 * und versucht sich mit den gegebene Parametern an dem Https-Server 
	 * anzumelden.
	 * 
	 * @param baseUrl URL oder URI zum Https-Server
	 * @param serverPort Server-Portnummer
	 * @param resourcePath REST-Pfadangabe zur jeweiligen REST-Anwendung des Servers
	 * @param requestMethod Methode an der resourcePath, die angesprochen werden soll
	 * @param userAuthentificationName Benutzername bei der Anmeldung
	 * @param userAuthentificationPwd Passwort des Benutzers
	 */
	public MedysLdapHttpsConnect(
			String baseUrl,
			String serverPort,
			String resourcePath,
			String requestMethod,
			String userAuthentificationName,
			String userAuthentificationPwd)
	{		
		isConnected = false;
		
		if(requestMethod.toLowerCase().compareTo("get") == 0)
		{
			doGetRequest(
				baseUrl, 
				serverPort, 
				resourcePath, 
				"accept", "application/zip");
		}
		else
		{
			doConnect(baseUrl, serverPort, resourcePath);
		}
	}
	
	private void doGetRequest(
					String baseUrl, 
					String serverPort, 
					String resourcePath, 
					String requestKeyWord,
					String requestKeyValue)
	{
		doConnect(baseUrl, serverPort, resourcePath);
		
		if(isConnectionEstablished())
		{
			try
			{
				serverConn.setReadTimeout(60 * 5); // Timeout in 5 Minute
				
				// nur wenn man auch eine QUERY-String als Anhang hat ( also was mit ?<var>=<suchbegriff)
				//
				// sollte man die Accept-Charset Verbindungs-Property setzen
				//
//				
//				Charset charset = Charset.forName("utf-8");
//				
//				String charsetName = charset.name();
//				
//				if(charsetName.toLowerCase().compareTo("utf-8") == 0)
//				{
//					serverConn.setRequestProperty("Accept-Charset", charsetName);
//				}
				
				serverConn.setRequestProperty(requestKeyWord, requestKeyValue);
				
				// sendet die HTTPS-Verbindung einen Status bereit/OK = 200
				//
				String response = serverConn.getURL().toExternalForm() + "";
				
				System.out.println(response);
				
				response = serverConn.getResponseMessage();
				
				if(serverConn.getResponseCode() == HttpsURLConnection.HTTP_OK)
				{
					BufferedInputStream bis = new BufferedInputStream(serverConn.getInputStream());

					FileWriter fw = new FileWriter("accounts.xml");
					
					BufferedWriter bw = new BufferedWriter(fw);
					
					bis.close();
					bw.close();
				}
			}
			catch(ProtocolException protocolExep)
			{
				System.out.println("PROTOCOLL");
				System.out.println(
						"Exception-Typ: ProtocoException\n\n"
						+ "im Konstruktor MedysLdapHttpsConnect\n\n"
						+ protocolExep.getMessage());
			}
			catch(IOException ioexep)
			{
				System.out.println("TEST");
				System.out.println(
						"Exception-Typ: IOException\n\n"
						+ "im Konstruktor MedysLdapHttpsConnect\n\n"
						+ ioexep.getMessage());
			}
		}
	}
	
	/**
	 * Verbindet sich mit einem Server &uuml;ber die &uuml;bergebenen Parameter
	 * 
	 * @param baseUrl
	 * 			URL zum Ldap-Server
	 * 
	 * @param serverPort
	 * 			Port des Ldap-Servers
	 */
	protected void doConnect(String baseUrl, String serverPort)
	{
		doConnect(baseUrl, serverPort, null);
	}
	
	private void doConnect(
			String baseUrl, 
			String serverPort, 
			String resourcePath)
	{
		try 
		{
			int port = Integer.parseInt(serverPort);
			
			String tempBaseUrlForCert = baseUrl;
			
			baseUrl = formatiereURLfuerRESTRequest(baseUrl, port, resourcePath);
							
			try
			{
				URL url = new URL(baseUrl);
				
				System.setProperty("https.protocols", "TLSv1.2");
				
				if(hasReceivedCertificateFrom(tempBaseUrlForCert, port + ""))
				{
					
				}
				serverConn = (HttpsURLConnection) url.openConnection();
				
				if(serverConn != null)
				{
					SSLSocketFactoryEx factory = null;
					
					try 
					{
						factory = new SSLSocketFactoryEx();
						
						if(factory != null)
						{
							serverConn.setSSLSocketFactory(factory);
						}
						isConnected = true;
					}
					catch (KeyManagementException keyMngExep) 
					{
						System.out.println("Key Management");
						System.out.println(
								"Exception-Typ: KeyManagementException\n\n"
								+ "in MedysLdapHttpsConnect.doConnect(String,String,String)\n\n"
								+ keyMngExep.getMessage());
					}
					catch (NoSuchAlgorithmException noAlgol) 
					{
						System.out.println("Key Management");
						System.out.println(
								"Exception-Typ: NoSuchAlgorithm\n\n"
								+ "in MedysLdapHttpsConnect.doConnect(String,String,String)\n\n"
								+ noAlgol.getMessage());
					}
				}
			}
			catch(MalformedURLException wrongUrlExcep)
			{
				System.out.println("malformed");
				System.out.println(
						"Exception-Typ: MalformedURLException\n\n"
						+ "in MedysLdapHttpsConnect.doConnect(String,String,String)\n\n"
						+ wrongUrlExcep.getMessage());
			}
			catch(IOException ioexep)
			{
				System.out.println("IO");
				System.out.println(
						"Exception-Typ: IOException\n\n"
						+ "in MedysLdapHttpsConnect.doConnect(String,String,String)\n\n"
						+ ioexep.getMessage());
			}
		}
		catch(NumberFormatException wrongFormat)
		{
			System.out.println("kein server-port angegeben\n\n"
					+ wrongFormat.getMessage());
		}
	}
	
//	private String getProtocoll(String url)
//	{
//		String result = null;
//		
//		for(int i = 0; i < url.length(); i++)
//		{
//			char c = url.charAt(i);
//			
//			result += c;
//			
//			if(result.toLowerCase().contains("://"))
//			{
//				result = result.substring("://".length(), result.length());
//				
//				break;
//			}
//		}
//		return result; 
//	}
	
	private String formatiereURLfuerRESTRequest( 
							String baseUrl, 
							int port, 
							String resourcePath)
	{		
		String url = baseUrl;
		
		if(port > 0)
		{
			if(isValideHttpsURL(url))
			{
				if(isValiderResourcePath(resourcePath))
				{
					if(url.contains(resourcePath))
					{
						// entfernen
						//
						url.replace(resourcePath, " ");
						
						// mit Leerzeichen ersetzt, Leerzeichen entfernen
						//
						url.trim();
					}
					if(url.contains("" + port))
					{
						// entfernen
						//
						url.replace("" + port, " ");
						
						url.trim();
					}
					if(url.endsWith(":"))
					{
						// entfernen
						//
						url.replace(":", " ");
						
						// mit Leerzeichen ersetzt, Leerzeichen entfernen
						//
						url.trim();
					}
					
					// weitere Prüfungen..
					
					if(url.endsWith("/"))
					{
						url = url.substring(0, url.length()-1);
					}
					if(!url.endsWith("/") && resourcePath.startsWith("/"))
					{	
						url = url + ":" + port + resourcePath;
					}
					if(!url.endsWith("/") && !resourcePath.startsWith("/"))
					{
						url = url + ":" + port + "/" + resourcePath;
					}
				}
			}
		}
		
		return url;
	}
	
	/*
	 * Prüft, ob das Server-zertifikat in den KeyStore des JDK gespeichert wurde (erhalten) oder nicht
	 * 
	 * @param horstUrl die URL zum WebServer
	 * @param serverPort der Port des WebServers
	 * 
	 * @return TRUE wenn Zertifikat in den KeyStore geladen wurde, sonst FALSE
	 */
	private boolean hasReceivedCertificateFrom(String horstUrl, String serverPort)
	{	
		instCert = new InstallCert(horstUrl, serverPort);
		
		// lief alles gut, so sollte zumindestens die Return-methode erreichbar sein
		//
		return instCert.getServerHandshakeAccomplished();
	}
	
	
	private boolean isConnectionEstablished()
	{
		return isConnected;
	}
	
	private boolean isValideHttpsURL(String url) 
	{
		return url != null ? url.startsWith("https://") ? true : false : false;
	}
	
	private boolean isValiderResourcePath(String resourcePath)
	{
		boolean status = false;
		
		if(resourcePath != null)
		{
			// keine Basis-URL-Angabe und nicht leer
			//
			if(!resourcePath.contains("://") && (resourcePath.length() > 0))
			{
				// eine Überprüfung ob eine Pfadangabe mit Unterordner-Stuktur
				// wie "ordner/ordner/ordner" vorliegt, ist hier nicht notwendig
				
				// die java.net.MalformedURLException sollte diesen Zustand
				// abfangen, wenn der ResourcePath Bestandteil einer URL in
				// einem java.net.URL-Objekt ist
				
				status = true;
			}
		}
		
		return status;
	}
}
