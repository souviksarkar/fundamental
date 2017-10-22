package com.identityhub.oim.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oracle.iam.platform.Platform;
import Thor.API.tcResultSet;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Exceptions.tcInvalidLookupException;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcLookupOperationsIntf;


import com.thortech.util.logging.Logger;

/***
 * 
 * @author souvsarkar
 * @created 16-10-2017
 */

public class IDCSAdapters {
	
	private static Logger logger = Logger.getLogger(IDCSAdapters.class.getName());
	private static tcFormInstanceOperationsIntf formInstanceIntf = Platform.getService(tcFormInstanceOperationsIntf.class);
	private static tcLookupOperationsIntf lookupOperationsIntf = Platform.getService(tcLookupOperationsIntf.class);

	/***
	 * @param pKey
	 * 
	 */
	public void AddProcessChildData(long pKey){
		
		   try {
		      
		      tcResultSet childFormDef = formInstanceIntf.getChildFormDefinition(formInstanceIntf.getProcessFormDefinitionKey(pKey),formInstanceIntf.getProcessFormVersion(pKey));

		      long childKey = childFormDef.getLongValue("Structure Utility.Child Tables.Child Key");
		      
		      Map attrChildData = new HashMap();
		      String IDHUB_IDCS_GROUP_NAME=getLookupValueForDecode("Identity Hub IDCS Group Name","Lookup.IDCS.Custom");
		      String IDHUB_IDCS_GROUP_ID=getLookupValueForDecode(IDHUB_IDCS_GROUP_NAME,"Lookup.IDCS.Groups");
		      attrChildData.put("UD_IDCS_UGP_GROUP",IDHUB_IDCS_GROUP_ID);
		      formInstanceIntf.addProcessFormChildData(childKey,pKey,attrChildData);
		   
		   }catch (Exception e){
		      e.printStackTrace();
		   } 
		}
	/**
     * Retrieves the lookup decode value based on lookup code in a lookup
     *
     * @param lookupCode
     * @param lookupName
     * @return lookup decode value
     * @throws Exception
     */
    private String getLookupValueForDecode(String lookupDecode, String lookupName) {

        final String prefix = getClass().getName().concat(" : ").concat("getLookupValueForDecode : ");
        tcResultSet rs;

        String decodeValue = null;
        String codeValue;

        try {
            rs = lookupOperationsIntf.getLookupValues(lookupName);
            for (int i = 0; i < rs.getRowCount(); i++) {
                rs.goToRow(i);
                codeValue = rs.getStringValue("Lookup Definition.Lookup Code Information.Code Key");
                decodeValue = rs.getStringValue("Lookup Definition.Lookup Code Information.Decode");
                if (decodeValue.equals(lookupDecode)) return codeValue;
            }

        } catch (Exception e) {
            logger.error(prefix + "Exception : " + e.getMessage());
        }
        return null;
    }
}
