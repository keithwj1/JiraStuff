package custom.proj
import custom.global.approval
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import custom.proj.MrbScrap
public class MrbApproval extends custom.global.approval {
    MrbApproval() {          
        this.bQualityEngRequired = false;
        this.bDesignEngRequired = false;
        this.bMfgEngRequired = false;
        this.bMaterialsRequired = false;
        this.bOperationsRequired = false;
    }
    MrbApproval(Issue issue) {
        def oDisposition = this.GetDisposition(issue)
        if (oDisposition == null){return;}
        String sDisposition = oDisposition.toString();
        if (sDisposition == "Use As Is"){
            this.bQualityEngRequired = true;
            this.bDesignEngRequired = true;
            this.bMfgEngRequired = true;
            this.bMaterialsRequired = true;
            this.bOperationsRequired = false;
        }
        else if (sDisposition == "Repair"){
            this.bQualityEngRequired = true;
            this.bDesignEngRequired = true;
            this.bMfgEngRequired = true;
            this.bMaterialsRequired = true;
            this.bOperationsRequired = false;
        }
        else if (sDisposition == "Scrap"){
            this.bQualityEngRequired = true;
            this.bDesignEngRequired = false;
            this.bMfgEngRequired = true;
            this.bMaterialsRequired = true;
            this.bOperationsRequired = true;
        }
        else if (sDisposition == "Rework"){
            this.bQualityEngRequired = true;
            this.bDesignEngRequired = false;
            this.bMfgEngRequired = true;
            this.bMaterialsRequired = false;
            this.bOperationsRequired = false;
        }
        else if (sDisposition == "Return to Vendor"){
            this.bQualityEngRequired = true;
            this.bDesignEngRequired = false;
            this.bMfgEngRequired = false;
            this.bMaterialsRequired = true;
            this.bOperationsRequired = false;
        }
    }
    public static def GetDisposition(Issue issue){
        return GetFieldValue(issue,"customfield_10601");
    }
    //Overload Ops Permission for Scrap Authority
    public static boolean HasOperationsPermission(Issue issue,ApplicationUser aUser){
        if (ComponentAccessor.getGroupManager().isUserInGroup(aUser, 'jira-operations')){return true;}
        if (MrbScrap.ScrapWithinLimit(issue,aUser) == 1){return true;}
        return false;
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
                else if (QualityEngReq(issue))
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
                else if (OperationsReq(issue))
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
                else if (DesignEngReq(issue))
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
                else if (MfgEngReq(issue))
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
                else if (MaterialsReq(issue))
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