package de.medys.ldap;

import de.medys.ldap.console.MedysLdapConnectConsoleSuite;
import de.medys.ldap.https.MedysLdapHttpsConnect;
import de.medys.ldap.ui.MedysLdapConnectTestSuite;

/**
 * Startup-TASK f&uuml;r die LDAP-Abfrage aus MEDYS heraus.
 * <br><br>
 * Rufen Sie diese Klasse wie folgt auf:
 * <blockquoote>
 * <b>java -jar MedysLdapConnection.jar 
 *    &lt;param1&gt; &lt;param2&gt; &lt;param3&gt; &lt;param4&gt; &lt;param5&gt; &lt;param6&gt;</b>
 *</blockquot>
 * <u><b>Parameter:</b></u>
 * <blockquot>
 * <ul>
 *  <li>&lt;param1&gt; = URL oder Name des Servers (falls man &uuml;ber eine Namensaufl&ouml;sung den Server erreichen kann)</li>
 *  <li>&lt;param2&gt; = Port-Nummer des LDAP-Servers</li>
 *  <li>&lt;param3&gt; = Verbindungsprotokoll: LDAP v3 / LDAP v2 (optional) oder SSL (wenn Angabe=SSL, dann sollte connectingUserName/-Password angegeben werden!)</li>
 *  <li>&lt;param4&gt; = registierter LDAP-Benutzername</li>
 *  <li>&lt;param5&gt; = Passwort des Benutzernamen</li>
 * </ul>
 * </blockquot>
 * <u>weitere notwendige Informationen für den LDAP-Prozess</u>
 * <blockquot>
 * <ul>
 *  <li>der Wohnort einer Person/Unternehmen des LDAP-Eintrags (kann leer sein, LDAP-Atttribute:l=location)</li>
 *  <li>Nachname einer Person/Unternehmens des LDAP-Eintrags (kann leer sein,LDAP-Attribute:sn=surname)</li>
 * </ul>
 * </blockquot>
 * <br>
 * Diese Information kommen aus einer Textdatei &quot;<i>MedysLdapConnection_Suchparameter.txt</i><&quot;,<br />
 * welche in dem gleichen Verzeichnis wie die ausführbare JAR &quot;<i>MedysLdapConnection.jar</i>&quot; liegt.
 * <br><br>
 * <u><b>Aufruf eines Programms:</b></u>
 * <blockquot>
 * 	Beispielaufruf f&uuml;r die Ermittelung der Daten von &quot;Hans M&uuml;ller&quot;
 * 	<br>
 * 	<u>ohne SSL-Verbindung:</u>
 *  	<blockquot>
 * 			<b>java -jar MedysLdapConnection.jar &apos;kvc-1.kvtg.kbv.de&apos; &apos;10389&apos; &apos;ldapv3&apos; &lt;ein ldap benutzername&gt; &lt;ein ldap benutzername password &gt;</b>
 * 		</blockquot>
 * </blockquot>
 * Hans M&uuml;ller Name wird in MEDYS in der Library <i>KVConnect.oKVCOnnectOmnisLdapConnection</i> in der Methode
 * <i>$initLdapSuche()->_schreibeInHilfsdatei(..)</i> gesetzt und über diese Java-Anwendung ausgelesen (an LDAP-Server durchgereicht).
 * <br><br>
 * <b><u>Achtung</u></b>:
 * <blockquot>
 * 	wenn der dritte Parameter mit &quot;STREMPTY&quot; vorbelegt ist 
 *  <blockquot>
 *  das gleiche Verfahren gilt für die folgenden Konditionen 
 *  <u>mit SSL-Verbindung (lediglich den PORT anpassen und dritten Parameter belegen!):</u><br><br>
 *  <i>java -jar MedysLdapConnection.jar &apos;kvc-1.kvtg.kbv.de&apos; <u>&apos;636&apos;</u> <b>&apos;ssl&apos;</b> &lt;ein ldap benutzername&gt; &lt;ein ldap benutzername password &gt;</i>
 *  </blockquot>
 *  <u>nur im DEVELOPER-MODUS:</u>
 * 	<blockquot>
 * 		<i>java -jar MedysLdapConnection.jar &quot;developer&quot;</i>
 * 	</blockquot>
 *  <br>
 * Sind keine Personendaten vergeben, so wird <u>die Suche kein Resultat liefern</u>.
 * <br><br>
 * Ist kein registrierter LDAP-Benutzername und das Benutzer-Passwort angegeben (also leer), so startet die LDAP-Suche im Anonymous-Modus <br />
 * und liefert je nach Einstellung des LDAP-Servers n-Anzahl an Suchergebnisse f&uuml;r den anonymen Anmelder.
 * <br><br>
 * Im schlimmsten Fall: gar kein Ergebnis, da anonym angemeldete Benutzer beschr&auml;nkte Rechte haben.
 * 
 * @see MedysLdapConnectConsoleSuite
 * @see MedysLdapConnectTestSuite
 * 
 * @author Hayri Emrah Kayaman, Medys GmbH, 26 June 2015
 * @version 0.1
 */
public class MedysLdapConnection {
	
	public static void main(String[] args) {
		
		// mindestens ServerUrl und Port angeben
		//
		// startup Arguments (args[5] = Parameter 6) prüft, ob der DEBUGGER MODUS aktiviert ist 
		//
		// also die DEVELOPER Version (GUI) gestartet werden soll
		//
		if (args.length > 0) 
		{
			if(args[0] != null)
			{
				if(args[0].toLowerCase().compareTo("developer") == 0)
				{
					new MedysLdapConnectTestSuite();
				}
				if(args[0].toLowerCase().startsWith("https://"))
				{
					new MedysLdapHttpsConnect(args[0], args[1], args[2], args[3], args[4], args[5]);
				}
				else 
				{
					// Verbindungsprotokoll ist "ldap/ldaps"
					//
					new MedysLdapConnectConsoleSuite(args);
				}
			}
		}
		else
		{
			usage();
		}
	}
	
	/*
     * Beschreibt, wie man das Programm starten muß.
     * 
     * Ausgabe erfolgt in der Konsole/Shell!
     */
    private static void usage()
    {
        System.out.println( "Starten Sie das Programm wie folgt:\n\n"
        		+ "Um das Programm im Developer-Modus zu starten\n\n"
        		+ "java \"" + MedysLdapConnection.class.getName() + " \"developer\"\n\n"
        		+ "Um das Programm ohne Developer-Modus zu starten\n\n"
        		+ "java \"" + MedysLdapConnection.class.getName()
        		+ " \"Ldap-Server-Name (nur die Angabe hinter http:// oder ldap://)\""
        		+ " \"Ldap-Serverport\" \"Verbindungsprotokoll (wie SSL oder LDAP v3) \"Benutzername für Login\" \"Benutzerpasswort\"");
    }
}
