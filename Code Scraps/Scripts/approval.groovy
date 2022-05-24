/**
 * @file custom.global.approval.groovy
 *
 * @brief A base class for approvals
 *
 * @ingroup custom.global.approval
 * 
 *
 * @author Keith Jones
 * Contact: keithwj1@gmail.com
 *
 */


package custom.global
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import java.sql.Timestamp
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption


/**
 * Implementation of an approval class
 *
 * Class is intended to be inherited and not used on its own
 * Overload functions when needed for additional approval control
 * 
 *
 */
public class approval {
    public static boolean bQualityEngRequired = false;
    public static boolean bDesignEngRequired = false;
    public static boolean bMfgEngRequired = false;
    public static boolean bMaterialsRequired = false;
    public static boolean bOperationsRequired = false;
    /**
    * Returns object value of a custom field
    *
    * @param  	Issue issue   				Issue
    * @param  	String custom_field 		custom field ID (customfield_XXXXXXXX)
    * @return   object
    */
    public static def GetFieldValue(Issue issue, String custom_field){
        def customFieldManager = ComponentAccessor.getCustomFieldManager();
        def fField = customFieldManager.getCustomFieldObject(custom_field);
        return issue.getCustomFieldValue(fField);
    }
    /**
    * Returns string value of a custom field
    *
    * @param  	Issue issue   				Issue
    * @param  	String custom_field 		custom field ID (customfield_XXXXXXXX)
    * @return   String
    */
    public static String GetFieldString(Issue issue, String custom_field){
        def oValue = GetFieldValue(issue,custom_field);
        if (oValue == null){return "";}
        return oValue.toString();
    }
    /**
    * Returns true if the field has a value
    *
    * @param  	Issue issue   				Issue
    * @param  	String custom_field 		custom field ID (customfield_XXXXXXXX)
    * @return   boolean
    */
    public static boolean FieldHasValue(Issue issue, String custom_field){
        if (GetFieldValue(issue,custom_field) == null){
            return false;
        }
        return true;
    }
    /**
    * Sets the approval fields in the specified issue
    *
    * @param  	MutableIssue issue   		Issue
    * @param  	ApplicationUser aUser  		User to send the even update as
    * @param  	String custom_field_user 	custom field ID (customfield_XXXXXXXX)
    * @param  	String custom_field_date	custom field ID (customfield_XXXXXXXX)
    * @return   null
    */
    public void SetApprovalField(MutableIssue issue,ApplicationUser aUser, String custom_field_user,String custom_field_date){
        def customFieldManager = ComponentAccessor.getCustomFieldManager();
        def fuser = customFieldManager.getCustomFieldObject(custom_field_user);
        def fdate = customFieldManager.getCustomFieldObject(custom_field_date);
        def date = new Timestamp(new Date().getTime());
        issue.setCustomFieldValue(fuser,aUser);
        issue.setCustomFieldValue(fdate, date);
        ComponentAccessor.getIssueManager().updateIssue(aUser, issue, EventDispatchOption.DO_NOT_DISPATCH, false);
    }
    /**
    * Clears the value in the specified custom field
    *
    * @param  	MutableIssue issue   		Issue
    * @param  	ApplicationUser aUser  		User to send the even update as
    * @param  	String custom_field		   	custom field ID (customfield_XXXXXXXX)
    * @return   null
    */
    public void ClearField(MutableIssue issue, ApplicationUser aUser,String custom_field){
        def customFieldManager = ComponentAccessor.getCustomFieldManager();
        def f = customFieldManager.getCustomFieldObject(custom_field);
        issue.setCustomFieldValue(f,null);
        ComponentAccessor.getIssueManager().updateIssue(aUser, issue, EventDispatchOption.DO_NOT_DISPATCH, false);
    }
    public void SetQualityEngRequired(boolean value){
        this.bQualityEngRequired = value;
    }
    public void SetDesignEngRequired(boolean value){
        this.bDesignEngRequired = value;
    }
    public void SetMfgEngRequired(boolean value){
        this.bMfgEngRequired = value;
    }
    public void SetMaterialsRequired(boolean value){
        this.bMaterialsRequired = value;
    }
    public void SetOperationsRequired(boolean value){
        this.bOperationsRequired = value;
    }
    public static boolean HasQualityEngPermission(ApplicationUser aUser){
        return ComponentAccessor.getGroupManager().isUserInGroup(aUser, 'jira-mrb-quality');
	}
    public static boolean HasDesignEngPermission(ApplicationUser aUser){
        return ComponentAccessor.getGroupManager().isUserInGroup(aUser, 'jira-mrb-designeng');
	}
    public static boolean HasMaterialsPermission(ApplicationUser aUser){
        return ComponentAccessor.getGroupManager().isUserInGroup(aUser, 'jira-mrb-materials');
	}
    public static boolean HasMfgEngPermission(ApplicationUser aUser){
        return ComponentAccessor.getGroupManager().isUserInGroup(aUser, 'jira-mrb-mfgeng');
	}
    public static boolean HasOperationsPermission(Issue issue,ApplicationUser aUser){
        return ComponentAccessor.getGroupManager().isUserInGroup(aUser, 'jira-operations');
	}
    public void SetDesignEngApproved(MutableIssue issue,ApplicationUser aUser){
        SetApprovalField(issue,aUser,"customfield_13326","customfield_13331");
    }
    public void SetQualityEngApproved(MutableIssue issue,ApplicationUser aUser){
        SetApprovalField(issue,aUser,"customfield_13327","customfield_13330");
    }
    public void SetMaterialsApproved(MutableIssue issue,ApplicationUser aUser){
        SetApprovalField(issue,aUser,"customfield_13329","customfield_13332");
    }
    public void SetMfgEngApproved(MutableIssue issue,ApplicationUser aUser){
        SetApprovalField(issue,aUser,"customfield_13325","customfield_13334");
    }
    public void SetOperationsApproved(MutableIssue issue,ApplicationUser aUser){
        SetApprovalField(issue,aUser,"customfield_13328","customfield_13333");
    }
    public static boolean DesignEngApproved(Issue issue){
        return FieldHasValue(issue,"customfield_13326");
    }
    public static boolean QualityEngApproved(Issue issue){
        return FieldHasValue(issue,"customfield_13327");
	}
    public static boolean MaterialsApproved(Issue issue){
        return FieldHasValue(issue,"customfield_13329");
	}
    public static boolean MfgEngApproved(Issue issue){
        return FieldHasValue(issue,"customfield_13325");
	}
    public static boolean OperationsApproved(Issue issue){   
        return FieldHasValue(issue,"customfield_13328");
	}
    public static boolean DesignEngReq(Issue issue){
        if (this.bDesignEngRequired){return !DesignEngApproved(issue);}
        return false;
    }
    public static boolean QualityEngReq(Issue issue){
        if (this.bQualityEngRequired){return !QualityEngApproved(issue);}
        return false;
    }
    public static boolean MaterialsReq(Issue issue){
        if (this.bMaterialsRequired){return !MaterialsApproved(issue);}
        return false;
    }
    public static boolean MfgEngReq(Issue issue){
        if (this.bMfgEngRequired){return !MfgEngApproved(issue);}
        return false;
    }
    public static boolean OperationsReq(Issue issue){
        if (this.bOperationsRequired){return !OperationsApproved(issue);}
        return false;
    }
    public static boolean QualityEngReqAndPermission(Issue issue, ApplicationUser aUser){
        if (QualityEngReq(issue)){
            if (HasQualityEngPermission(aUser)){
                return true;
            }
        }
    }
    /**
    * Checks permission and sets the approver in the fields for the provided MutableIssue
    * Designed this way to allow for multiple approvals in the same function
    *
    * @param  	MutableIssue issue 					Issue to be modified
    * @param  	ApplicationUser aUser   			User that is approving
    * @param  	Boolean bQuality   					Approve for Quality
    * @param  	Boolean bDesign   					Approve for Design
    * @param  	Boolean bMaterials 					Approve for Materials
    * @param  	Boolean bManufacturingEngineering   Approve for Manufacturing Engineering
    * @param  	Boolean bOperations   				Approve for Operations
    * @return   null
    */
    public void CheckPermissionsAndApprove(MutableIssue issue, ApplicationUser aUser,bQuality = false,bDesign = false, bMaterials = false, bManufacturingEngineering = false, bOperations = false){
        if (bQuality){
            if (HasQualityEngPermission(aUser)){
                SetQualityEngApproved(issue,aUser);
            }
        }
        if (bDesign){
            if (HasDesignEngPermission(aUser)){
                SetDesignEngApproved(issue,aUser);
            }
        }
        if (bMaterials){
            if (HasMaterialsPermission(aUser)){
                SetMaterialsApproved(issue,aUser);
            }
        }
        if (bManufacturingEngineering){
            if (HasMfgEngPermission(aUser)){
                SetMfgEngApproved(issue,aUser);
            }
        }
        if (bOperations){
            if (HasOperationsPermission(issue,aUser)){
                SetOperationsApproved(issue,aUser);
            }
        }
        
    }
    /**
    * Returns true if the user has permissions to approve for the specified department
    *
    * @param  	Issue issue   				Issue
    * @param  	ApplicationUser aUser  		User trying to approve
    * @param  	String sChoice		   		Approval Group (should be the same as the dropdown field)
    * @return   boolean
    */
    public static boolean UserHasPermission(Issue issue,ApplicationUser aUser,String sChoice){
        if (sChoice == "Quality Engineering"){return HasQualityEngPermission(aUser);}
        if (sChoice == "Design Engineering"){return HasDesignEngPermission(aUser);}
        if (sChoice == "Manufacturing Engineering"){return HasMfgEngPermission(aUser);}
        if (sChoice == "Operations"){return HasOperationsPermission(issue,aUser);}
        if (sChoice == "Materials"){return HasMaterialsPermission(aUser);}
        return false;
    }
    /**
    * Returns true if all approvals are complete
    *
    * @param  	Issue issue   		Issue
    * @return   boolean
    */
    public static boolean ApprovalsComplete(Issue issue){
        if (QualityEngReq(issue)){return false;}
        if (DesignEngReq(issue)){return false;}
        if (MfgEngReq(issue)){return false;}
        if (MaterialsReq(issue)){return false;}
        if (OperationsReq(issue)){return false;}
        return true;
    }
    /**
    * Sets the approver in the fields for the provided MutableIssue
    * Designed this way to allow for multiple approvals in the same function
    *
    * @param  	MutableIssue issue 					Issue to be modified
    * @param  	ApplicationUser aUser   			User that is approving
    * @param  	Boolean bQuality   					Approve for Quality
    * @param  	Boolean bDesign   					Approve for Design
    * @param  	Boolean bMaterials 					Approve for Materials
    * @param  	Boolean bManufacturingEngineering   Approve for Manufacturing Engineering
    * @param  	Boolean bOperations   				Approve for Operations
    * @return   null
    */
    public void SetApproved(MutableIssue issue,ApplicationUser aUser, bQuality = false,bDesign = false, bMaterials = false, bManufacturingEngineering = false, bOperations = false){
        if (bQuality){SetQualityEngApproved(issue,aUser);}
        if (bDesign){SetDesignEngApproved(issue,aUser);}
        if (bMaterials){SetMaterialsApproved(issue,aUser);}
        if (bManufacturingEngineering){SetMfgEngApproved(issue,aUser);}
        if (bOperations){SetOperationsApproved(issue,aUser);}
    }
    /**
    * Returns a string with the current approvals status
    *
    * @param  	Issue issue   		Issue
    * @return   String
    */
    public static String GetApprovalsNeeded(Issue issue)
    {
        String sreturn = ""
        if (QualityEngReq(issue)){sreturn = sreturn + "Quality: REQUIRED; "}
        if (OperationsReq(issue)){sreturn = sreturn + "Operations: REQUIRED; "}
        if (MfgEngReq(issue)){sreturn = sreturn + "Mfg: REQUIRED; "}
        if (DesignEngReq(issue)){sreturn = sreturn + "Design: REQUIRED; "}
        if (MaterialsReq(issue)){sreturn = sreturn + "Mat: REQUIRED; "}
        if (sreturn != "")
        {
            return sreturn;
        }
        return null;
    }
    /**
    * Returns a string with the current approvals needed
    *
    * @param  	Issue issue   		Issue
    * @return   String
    */
    public static String GetApprovalList(Issue issue)
    {
        String sreturn = ""
        if (QualityEngReq(issue)){sreturn = sreturn + "Quality; "}
        if (OperationsReq(issue)){sreturn = sreturn + "Operations; "}
        if (MfgEngReq(issue)){sreturn = sreturn + "Mfg; "}
        if (DesignEngReq(issue)){sreturn = sreturn + "Design; "}
        if (MaterialsReq(issue)){sreturn = sreturn + "Mat; "}
        if (sreturn != "")
        {
            return sreturn;
        }
        return null;
    }
    /**
    * Adds the approver and date to the correct field
    *
    * @param  	MutableIssue issue   		Mutable Issue to be Modified
    * @param  	ApplicationUser aUser   	User that is trying to approve
    * @return   null
    */
    public void AddApprover(MutableIssue issue,ApplicationUser user = null){
        if (user == null){user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();}
        String approvegroup = GetFieldString(issue,"customfield_13016")
        if (approvegroup == "Quality Engineering"){
            CheckPermissionsAndApprove(issue,user,true);
        }
        else if (approvegroup == "Design Engineering"){
            CheckPermissionsAndApprove(issue,user,false,true);
        }
        else if (approvegroup == "Materials"){
            CheckPermissionsAndApprove(issue,user,false,false,true);
        }
        else if (approvegroup == "Manufacturing Engineering"){
            CheckPermissionsAndApprove(issue,user,false,false,false,true);
        }
        else if (approvegroup == "Operations"){
            CheckPermissionsAndApprove(issue,user,false,false,false,false,true);
        }

    }
    /**
    * Returns a list of options that the current user has to approve.
    *
    * @param  	Issue issue   				Issue
    * @param  	ApplicationUser aUser   	User that is trying to approve
    * @return   List
    */
    public static List GetApprovalOptions(Issue issue, ApplicationUser aUser){
        List options = []
        if (QualityEngReq(issue)){ 
            if (HasQualityEngPermission(aUser)){
                options.add("Quality Engineering");
            }
        }
        if (DesignEngReq(issue)){ 
            if (HasDesignEngPermission(aUser)){
                options.add("Design Engineering");
            }
        }
        if (MfgEngReq(issue)){ 
            if (HasMfgEngPermission(aUser)){
                options.add("Manufacturing Engineering");
            }
        }
        if (MaterialsReq(issue)){ 
            if (HasMaterialsPermission(aUser)){
                options.add("Materials");
            }
        }
        if (OperationsReq(issue)){ 
            if (HasOperationsPermission(issue,aUser)){
                options.add("Operations");
            }
        }
        if (options.isEmpty()){
            options.add("No Permissions");
        }
        return options;
    }
    /**
    * Clears All Approval Fields used by this class
    *
    * @param  	MutableIssue issue   		Mutable Issue to be Modified
    * @param  	ApplicationUser aUser   	User to send the event update as
    * @return  	null
    */
    public void ClearFields(MutableIssue issue, ApplicationUser aUser){
        def customFieldManager = ComponentAccessor.getCustomFieldManager();
        def fuser = customFieldManager.getCustomFieldObject("customfield_13326");
        def fdate = customFieldManager.getCustomFieldObject("customfield_13331");
        issue.setCustomFieldValue(fuser,null);
        issue.setCustomFieldValue(fdate,null);
        fuser = customFieldManager.getCustomFieldObject("customfield_13327");
        fdate = customFieldManager.getCustomFieldObject("customfield_13330");
        issue.setCustomFieldValue(fuser,null);
        issue.setCustomFieldValue(fdate,null);
        fuser = customFieldManager.getCustomFieldObject("customfield_13329");
        fdate = customFieldManager.getCustomFieldObject("customfield_13332");
        issue.setCustomFieldValue(fuser,null);
        issue.setCustomFieldValue(fdate,null);
        fuser = customFieldManager.getCustomFieldObject("customfield_13325");
        fdate = customFieldManager.getCustomFieldObject("customfield_13334");
        issue.setCustomFieldValue(fuser,null);
        issue.setCustomFieldValue(fdate,null);
        fuser = customFieldManager.getCustomFieldObject("customfield_13328");
        fdate = customFieldManager.getCustomFieldObject("customfield_13333");
        issue.setCustomFieldValue(fuser,null);
        issue.setCustomFieldValue(fdate,null);
        ComponentAccessor.getIssueManager().updateIssue(aUser, issue, EventDispatchOption.DO_NOT_DISPATCH, false);
    }
}