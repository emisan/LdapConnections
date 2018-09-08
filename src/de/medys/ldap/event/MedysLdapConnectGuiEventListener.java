package de.medys.ldap.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import de.medys.ldap.ui.MedysLdapConnectTestSuite;
import de.medys.ldap.utils.LdapConnectionFactory;
import de.medys.ldap.utils.MedysLdapConnectDataTransfer;

/**
 * Klasse f&uuml;r das Handling der UI-Komponenten
 * 
 * @author Hayri Emrah Kayaman, MEDYS GmbH 2015
 */
public class MedysLdapConnectGuiEventListener implements ActionListener, ItemListener, FocusListener { 
	
	private MedysLdapConnectTestSuite parentFrame;
	
	private LdapConnectionFactory factory;
	
	/**
	 * Erstellt eine neue Instanz von MedysLdapConnectionGuiEventListener 
	 * und bindet sich an die Eltern-Komponente
	 * 
	 * @param parentFrame die Eltern-Komponente vom Typ MedysLdapConnectionTestSuite
	 */
	public MedysLdapConnectGuiEventListener(MedysLdapConnectTestSuite parentFrame) 
	{
		setParentFrameAndFactory(parentFrame);
	}
	
	private void setParentFrameAndFactory(MedysLdapConnectTestSuite parentFrame)
	{
		this.parentFrame = parentFrame;
		factory = parentFrame.getLdapConnectionFactory();
	}
	
	@Override
	public void actionPerformed(ActionEvent acevt) {
		
		String actionCommand = acevt.getActionCommand();
		
		if ((actionCommand.compareTo("button_verbinde_ldap") == 0) || 
		    (actionCommand.compareTo("menuitem_verbinde_ldap") == 0)) 
		{	
			if(parentFrame != null)
			{
				parentFrame.clearText();
				
				MedysLdapConnectDataTransfer.clearMedysTextFileContent();
				
				String userName = parentFrame.getDeveloperCnNamesComboBox().getSelectedItem().toString();
				String userPass = parentFrame.getTxtfUserPassword().getText();
				
				String searchGivenName = parentFrame.getTxtfGivenName().getText();
				String searchSN = parentFrame.getTxtfSN().getText();
				
				if(userName.toLowerCase().compareTo("anonymous") == 0)
				{
					parentFrame.getTxtfUserPassword().setText("");
					parentFrame.repaint();
					
					userPass = parentFrame.getTxtfUserPassword().getText();
				}
				
				// Developer-Version
				if((searchGivenName != null) && (searchSN != null))
				{
					factory.retrieveLdapInformationDEVELOPER(
							parentFrame
							 	.getLdapServerUrlComboBox()
							 	.getSelectedItem()
							 	.toString(),
						    parentFrame
						    	.getLdapServerPortsComboBox()
						    	.getSelectedItem()
						    	.toString(),
						    parentFrame
						    	.getConnectionProtocolsComboBox()
						    	.getSelectedItem()
						    	.toString(),
						    	userName,
							    userPass,
							    searchGivenName,
							    searchSN);
				}
			}
		}
		if(actionCommand.compareTo("button_bereinige_inforarea") == 0)
		{
			parentFrame.clearText();
			
			MedysLdapConnectDataTransfer.clearMedysTextFileContent();
		}
		if(actionCommand.compareTo("menuitem_schliesse_ldap")==0)
		{
			System.exit(0);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent itemEvt) {
		
		Object component = itemEvt.getSource();
		
		if(component == parentFrame.getDeveloperCnNamesComboBox())
		{
			String name = parentFrame.getDeveloperCnNamesComboBox().getSelectedItem().toString();
			
			if(name.toLowerCase().compareTo("anonymous") == 0) 
			{
				parentFrame.setConnectingUserName("none");
			}
			else
			{
				parentFrame.setConnectingUserName(name);
			}
		}
	}

	/**
	 * Liefert die ItemListener-Schnittstelle
	 * 
	 * @return java.awt.event.ItemListener
	 */
	public ItemListener getItemListener() {
		return this;
	}


	@Override
	public void focusGained(FocusEvent arg0) {
	}


	@Override
	public void focusLost(FocusEvent focusEvt) {
		
		Object component = focusEvt.getSource();
		
		if(component == parentFrame.getTxtfUserPassword())
		{
			String password = parentFrame.getTxtfUserPassword().getText();
			
			// irgend etwas wurde eingetragen ?
			//
			if(password.length() > 0)
			{
				parentFrame.setConnectingUserPassword(password);
			}
			
		}
	}
	
	/**
	 * Liefert den FocusListener
	 * 
	 * @return java.awt.event.FocusListener
	 */
	public FocusListener getFocusListener()
	{
		return this;
	}
}
