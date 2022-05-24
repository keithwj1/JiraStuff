import custom.proj.SleApproval
import com.atlassian.jira.component.ComponentAccessor
def approval = new SleApproval();
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
approval.ClearFields(issue,user);