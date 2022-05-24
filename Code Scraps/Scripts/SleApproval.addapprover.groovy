import custom.proj.SleApproval
import com.atlassian.jira.component.ComponentAccessor
def Approval = new SleApproval();

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
def fapprovegroup = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_13016");
def oapprovegroup = issue.getCustomFieldValue(fapprovegroup);
if (oapprovegroup == null){return;}
String approvegroup = oapprovegroup.toString();

if (approvegroup == "Quality Engineering"){
    Approval.CheckPermissionsAndApprove(issue,user,true);
}
else if (approvegroup == "Design Engineering"){
    Approval.CheckPermissionsAndApprove(issue,user,false,true);
}
else if (approvegroup == "Materials"){
    Approval.CheckPermissionsAndApprove(issue,user,false,false,true);
}
else if (approvegroup == "Manufacturing Engineering"){
    Approval.CheckPermissionsAndApprove(issue,user,false,false,false,true);
}
else if (approvegroup == "Operations"){
    Approval.CheckPermissionsAndApprove(issue,user,false,false,false,false,true);
}
