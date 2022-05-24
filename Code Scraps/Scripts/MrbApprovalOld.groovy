package custom.proj
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue

import com.atlassian.jira.bc.issue.search.SearchService 
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.search.SearchQuery

import com.atlassian.jira.user.ApplicationUser

import custom.proj.MrbScrap

public class Mrbapproval 
{
    public static String sayHello() 
    {
        "hello MRBAPPROVAL!!! HOPEFULLY"
    }
    public static boolean UserHasPermission(Issue issue,ApplicationUser aUser,String sChoice)
    {
        def groupManager = ComponentAccessor.getGroupManager()
        if (sChoice == "Quality Engineering"){if (groupManager.isUserInGroup(aUser, 'jira-mrb-quality')){return true}}
        if (sChoice == "Design Engineering"){if (groupManager.isUserInGroup(aUser, 'jira-mrb-designeng')){return true}}
        if (sChoice == "Manufacturing Engineering"){if (groupManager.isUserInGroup(aUser, 'jira-mrb-mfgeng')){return true}}
        if (sChoice == "Operations")
        {
            if (groupManager.isUserInGroup(aUser, 'jira-operations'))
            {
                return true
            }
          	else
            {
                def nApprovalCode = MrbScrap.ScrapWithinLimit(issue,aUser)
                if (nApprovalCode == 1)
    			{
                    return true
                }
            }
        }
        if (sChoice == "Materials"){if (groupManager.isUserInGroup(aUser, 'jira-mrb-materials')){return true}}
        return false;
    }
    public static boolean DesignEngReq(Issue issue)
    {
        def status = issue.getStatus().getName();
        if (status == "Approvals")
        {
            def customFieldManager = ComponentAccessor.getCustomFieldManager()
            def fdispo = customFieldManager.getCustomFieldObject("customfield_10601");//dispo
            def odispo = issue.getCustomFieldValue(fdispo)
            def fSRMRANumber = customFieldManager.getCustomFieldObject("customfield_15200");
            def oSRMRANumber;
            if (issue.getIssueType().getName() == "Defect")
            {
                oSRMRANumber = issue.getParentObject().getCustomFieldValue(fSRMRANumber)
            }
            else
            {
            	oSRMRANumber = issue.getCustomFieldValue(fSRMRANumber)
            }
            if (odispo != null)
            {
                String sdispo = odispo.toString();
                if (sdispo in ["Use As Is","Repair"] || (sdispo == "Return to Vendor" && oSRMRANumber != null))
                {
                    def fdesuser = customFieldManager.getCustomFieldObject("customfield_13326")
                    def ftextdesigneng = customFieldManager.getCustomFieldObject("customfield_11056")
                    String stext = ""
                    def text = issue.getCustomFieldValue(ftextdesigneng)
                    if (text != null) {stext = text.toString()}
                    if (issue.getCustomFieldValue(fdesuser) == null && (stext == "REQUIRED" || stext == ""))
                    {
                        return true;
                    }
               }
            }
        }
        return false;
    }
    public static boolean QualityEngReq(Issue issue)
	{
        def status = issue.getStatus().getName();
        if (status == "Approvals")
        {
            def customFieldManager = ComponentAccessor.getCustomFieldManager()
            def fdispo = customFieldManager.getCustomFieldObject("customfield_10601");//dispo
            def odispo = issue.getCustomFieldValue(fdispo)
            if (odispo != null)
            {
                String sdispo = odispo.toString();
                if (sdispo in ["Use As Is","Return to Vendor","Scrap","Repair","Rework"])
                {
                    def fqualuser = customFieldManager.getCustomFieldObject("customfield_13327")
                    def ftextquality = customFieldManager.getCustomFieldObject("customfield_11057")
                    String stext = ""
                    def text = issue.getCustomFieldValue(ftextquality)
                    if (text != null) {stext = text.toString()}
                    if (issue.getCustomFieldValue(fqualuser) == null && (stext == "REQUIRED" || stext == ""))
                    {
                        //sreturn = "REQUIRED"
                        return true;
                    }
               }
            }
        }
        return false;
	}
    public static boolean MaterialsReq(Issue issue)
	{   
        def status = issue.getStatus().getName();
        if (status == "Approvals")
        {
            def customFieldManager = ComponentAccessor.getCustomFieldManager()
            def fdispo = customFieldManager.getCustomFieldObject("customfield_10601");//dispo
            def odispo = issue.getCustomFieldValue(fdispo)
            if (odispo != null)
            {
                String sdispo = odispo.toString();
                if (sdispo in ["Use As Is","Return to Vendor","Scrap","Repair"])
                {
                    def fmatuser = customFieldManager.getCustomFieldObject("customfield_13329")
                    def ftextmaterial = customFieldManager.getCustomFieldObject("customfield_11054")
                    String stext = ""
                    def text = issue.getCustomFieldValue(ftextmaterial)
                    if (text != null) {stext = text.toString()}
                    if (issue.getCustomFieldValue(fmatuser) == null && (stext == "REQUIRED" || stext == ""))
                    {
                        return true;
                    }
               }
            }
        }
        return false;
	}
    public static boolean MfgEngReq(Issue issue)
	{   
        def status = issue.getStatus().getName();
        if (status == "Approvals")
        {
            def customFieldManager = ComponentAccessor.getCustomFieldManager()
            def fdispo = customFieldManager.getCustomFieldObject("customfield_10601");//dispo
            def odispo = issue.getCustomFieldValue(fdispo)
            if (odispo != null)
            {
                String sdispo = odispo.toString();
                if (sdispo in ["Use As Is","Scrap","Repair","Rework"])
                {
                    def fmfguser = customFieldManager.getCustomFieldObject("customfield_13325")
                    def ftextmfgeng = customFieldManager.getCustomFieldObject("customfield_11060")
                    String stext = ""
                    def text = issue.getCustomFieldValue(ftextmfgeng)
                    if (text != null) {stext = text.toString()}
                    if (issue.getCustomFieldValue(fmfguser) == null && (stext == "REQUIRED" || stext == ""))
                    {
                        //sreturn = "REQUIRED"
                        return true;
                    }
               }
            }
        }
        return false;
	}
    public static boolean OperationsReq(Issue issue)
	{   
        def status = issue.getStatus().getName();
        if (status == "Approvals")
        {
            def customFieldManager = ComponentAccessor.getCustomFieldManager()
            def fdispo = customFieldManager.getCustomFieldObject("customfield_10601");//dispo
            def fSRMRA = customFieldManager.getCustomFieldObject("customfield_15200");
            def oSRMRA = issue.getCustomFieldValue(fSRMRA)
            def odispo = issue.getCustomFieldValue(fdispo)
            if (odispo != null)
            {
                String sdispo = odispo.toString()
                if (sdispo == "Scrap" || (sdispo == "Rework" && oSRMRA != null))
                {
                    def fopsuser = customFieldManager.getCustomFieldObject("customfield_13328")
                    def ftextops = customFieldManager.getCustomFieldObject("customfield_11058")
                    String stext = ""
                    def text = issue.getCustomFieldValue(ftextops)
                    if (text != null) {stext = text.toString()}
                    if (issue.getCustomFieldValue(fopsuser) == null && (stext == "REQUIRED" || stext == ""))
                    {
                        return true;
                    }
               }
            }
        }
        return false;       
	}
    public static boolean ApprovalsComplete(Issue issue)
    {
        if (!OperationsReq(issue) && !MfgEngReq(issue) && !MaterialsReq(issue) && !QualityEngReq(issue) && !DesignEngReq(issue))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
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
    public static String GenerateApprovalString(Issue issue)
    {
        def cfManager = ComponentAccessor.getCustomFieldManager();
        def fdispo = cfManager.getCustomFieldObject("customfield_10601")//dispo
        def odispo = issue.getCustomFieldValue(fdispo);
        if (odispo != null)
        {
            String dispo = odispo as String;
            if (dispo != "Pending")
            {
                def bnew = false
                def fqualuser = cfManager.getCustomFieldObject("customfield_13327")
                def fdesuser = cfManager.getCustomFieldObject("customfield_13326")    
                def fmfguser = cfManager.getCustomFieldObject("customfield_13325")    
                def fopsuser = cfManager.getCustomFieldObject("customfield_13328")    
                def fmatuser = cfManager.getCustomFieldObject("customfield_13329")

                /*
                def fqreq = customFieldManager.getCustomFieldObject("customfield_13338")
                def fopsreq = customFieldManager.getCustomFieldObject("customfield_13339")
                def fmfgreq = customFieldManager.getCustomFieldObject("customfield_13340")
                def fdesreq = customFieldManager.getCustomFieldObject("customfield_13341")
                def fmatreq = customFieldManager.getCustomFieldObject("customfield_13342")
                */
                def oquser = issue.getCustomFieldValue(fqualuser);
                def oduser = issue.getCustomFieldValue(fdesuser);
                def omfguser = issue.getCustomFieldValue(fmfguser);
                def oopsuser = issue.getCustomFieldValue(fopsuser);
                def omatuser = issue.getCustomFieldValue(fmatuser);
                String squal = ""
                if (oquser != null)
                {
                    bnew = true
                    def fdate = cfManager.getCustomFieldObject("customfield_13330")
                    def odate = issue.getCustomFieldValue(fdate)
                    def auser = oquser as ApplicationUser
                    squal = auser.displayName.toString()
                    if (odate != null)
                    {
                        def date = odate as Date
                        squal = squal + " " + date.format("MM/dd/yyyy")
                    }   
                }
                else if (Mrbapproval.QualityEngReq(issue))
                {
                    squal = "REQUIRED"
                }
                String sops = ""
                if (oopsuser != null)
                {
                    bnew = true
                    def fdate = cfManager.getCustomFieldObject("customfield_13333")
                    def odate = issue.getCustomFieldValue(fdate)
                    def auser = oopsuser as ApplicationUser
                    sops = auser.displayName.toString()
                    if (odate != null)
                    {
                        def date = odate as Date
                        sops = sops + " " + date.format("MM/dd/yyyy")
                    }   
                }
                else if (Mrbapproval.OperationsReq(issue))
                {
                    sops = "REQUIRED"
                }
                String sdes = ""
                if (oduser != null)
                {
                    bnew = true
                    def fdate = cfManager.getCustomFieldObject("customfield_13331")
                    def odate = issue.getCustomFieldValue(fdate)
                    def auser = oduser as ApplicationUser
                    sdes = auser.displayName.toString()
                    if (odate != null)
                    {
                        def date = odate as Date
                        sdes = sdes + " " + date.format("MM/dd/yyyy")
                    }   
                }
                else if (Mrbapproval.DesignEngReq(issue))
                {
                    sdes = "REQUIRED"
                }
                String smfg = ""
                if (omfguser != null)
                {
                    bnew = true
                    def fdate = cfManager.getCustomFieldObject("customfield_13334")
                    def odate = issue.getCustomFieldValue(fdate)
                    def auser = omfguser as ApplicationUser
                    smfg = auser.displayName.toString()
                    if (odate != null)
                    {
                        def date = odate as Date
                        smfg = smfg + " " + date.format("MM/dd/yyyy")
                    }   
                }
                else if (Mrbapproval.MfgEngReq(issue))
                {
                    smfg = "REQUIRED"
                }
                String smat = ""
                if (omatuser != null)
                {
                    bnew = true
                    def fdate = cfManager.getCustomFieldObject("customfield_13332")
                    def odate = issue.getCustomFieldValue(fdate)
                    def auser = omatuser as ApplicationUser
                    smat = auser.displayName.toString()
                    if (odate != null)
                    {
                        def date = odate as Date
                        smat = smat + " " + date.format("MM/dd/yyyy")
                    }   
                }
                else if (Mrbapproval.MaterialsReq(issue))
                {
                    smat = "REQUIRED"
                }
                //if (bnew == false)
                //Backwards Compatibility
                if (1==1)
                {

                    def fmaterial = cfManager.getCustomFieldObject("customfield_11054")// Field
                    def fmfgeng = cfManager.getCustomFieldObject("customfield_11060")// Field
                    def fdesigneng = cfManager.getCustomFieldObject("customfield_11056")// Field
                    def fquality = cfManager.getCustomFieldObject("customfield_11057")// Field
                    def fops = cfManager.getCustomFieldObject("customfield_11058")// Field
                    //def fapprovaltext = customFieldManager.getCustomFieldObject("customfield_11205")// Field

                    def dmat = issue.getCustomFieldValue(fmaterial);
                    def dmfg = issue.getCustomFieldValue(fmfgeng);
                    def ddes = issue.getCustomFieldValue(fdesigneng);
                    def dqual = issue.getCustomFieldValue(fquality);
                    def dops = issue.getCustomFieldValue(fops);
                    if (dmat != null && smat == "") {smat = dmat.toString()}
                    if (dmfg != null && smfg == "") {smfg = dmfg.toString()}
                    if (ddes != null && sdes == "") {sdes = ddes.toString()}
                    if (dqual != null && squal == "") {squal = dqual.toString()}
                    if (dops != null && sops == "") {sops = dops.toString()}
                }
                if (smat == "") {smat = 'N/A'}
                if (smfg == "") {smfg = 'N/A'}
                if (sdes == "") {sdes = 'N/A'}
                if (squal == "") {squal = 'N/A'}
                if (sops == "") {sops = 'N/A'}

                String apprtext = 'Materials: ' + smat + 
                                ';     Mfg Eng: ' + smfg + 
                                ';     Design Eng: ' + sdes + 
                                ';     Quality: ' + squal + 
                                ';     Operations: ' + sops;
                return apprtext;
            }
        }
        return null;
    }
}