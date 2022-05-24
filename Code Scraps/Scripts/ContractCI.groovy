package custom.proj
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.bc.issue.search.SearchService 
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.search.SearchQuery
import com.atlassian.jira.event.type.EventDispatchOption

public class ContractCI 
{
    public static String sayHello() 
    {
        "hello CONTRACT CI!!!"
    }
    public static void UpdateQualityReview(Issue contractissue)
    {
        
        def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)

        def searchService = ComponentAccessor.getComponent(SearchService.class)
        def issueManager = ComponentAccessor.issueManager
		def mcontractissue = issueManager.getIssueObject(contractissue.getId())
        def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        def customFieldManager = ComponentAccessor.getCustomFieldManager()    
        def fcustomer = customFieldManager.getCustomFieldObject("customfield_11061");
        //def fqcode = customFieldManager.getCustomFieldObject("customfield_14100");//14100
        //def fqcode = customFieldManager.getCustomFieldObject("customfield_14100");//14100
        def ocustomer = contractissue.getCustomFieldValue(fcustomer)

        if (ocustomer != null)
        {
            //def alog = Logger.getLogger("com.acme.CreateSubtask")
            //alog.setLevel(Level.DEBUG)

            String sReturn = "";
            String sRej = "";
            String sFlowdown = "";
            String sQCFlowdown = "";
            String sPKGFlowdown = "";
            String sFoundCodes = "";
            String sTop = "";
            String scustomer = ocustomer.toString();
            //This allows searching of mutliple customers if a ; is present
            String scustomersearch = "Customer ~ '$scustomer'"
            if (scustomer.contains(";"))
            {
                scustomer = scustomer.replaceAll("; ",";")
                scustomer = scustomer.toUpperCase();
                //alog.debug sallcodes
                def scustomers = scustomer.split(";");
                def nsize = scustomers.size()
                if (nsize > 0)
                {
                    scustomersearch = "("
                    for (int i=0; i < nsize;i++)
                    {
                        scustomersearch = scustomersearch + "Customer ~ '${scustomers[i]}'"
                        if ((i+1) != nsize)
                        {
                            scustomersearch = scustomersearch + " OR "
                        }
                        else
                        {
                            scustomersearch = scustomersearch + ")"
                        }
                    }
                }
            }
			def bChange = false;
            def queryString = "project = CI AND issuetype = Customer AND " + scustomersearch
            def query = jqlQueryParser.parseQuery(queryString)

            def results = searchService.search(user, query, PagerFilter.getUnlimitedFilter())
            //14301
            def fgeneric = customFieldManager.getCustomFieldObject("customfield_14301");//14301
            def fqcode = customFieldManager.getCustomFieldObject("customfield_14100");//14100
            def fqcodetitle = customFieldManager.getCustomFieldObject("customfield_14800");
            def fflowdown = customFieldManager.getCustomFieldObject("customfield_14200");//14200
            def fqcflowdown = customFieldManager.getCustomFieldObject("customfield_14204");//14204
            def fpkgflowdown = customFieldManager.getCustomFieldObject("customfield_14201");//14204
            def fmrbauthority = customFieldManager.getCustomFieldObject("customfield_14202");
            def fchangerequest = customFieldManager.getCustomFieldObject("customfield_16500");
            def fdeviationrequest = customFieldManager.getCustomFieldObject("customfield_16501");
            def fiso9001 = customFieldManager.getCustomFieldObject("customfield_12101");
            def fas9100 = customFieldManager.getCustomFieldObject("customfield_12100");
            def opqcode = contractissue.getCustomFieldValue(fqcode)
            String sallcodes = ""       
            if (opqcode != null) 
            {
                if (sallcodes != ""){sallcodes = sallcodes +","}
                sallcodes = sallcodes + opqcode.toString()
            }
            def searchresults = results.getResults()
            if (searchresults.size() > 0)
            {
                searchresults.each 
                {
                    searchissue ->
                    //Copy Fields Directly
                    def oas9100 = searchissue.getCustomFieldValue(fas9100)
                    if (oas9100 != null && oas9100 != contractissue.getCustomFieldValue(fas9100)){mcontractissue.setCustomFieldValue(fas9100,oas9100); bChange = true;}                   
                    def oiso9001 = searchissue.getCustomFieldValue(fiso9001)
                    if (oiso9001 != null && oiso9001 != contractissue.getCustomFieldValue(fiso9001)){mcontractissue.setCustomFieldValue(fiso9001,oiso9001); bChange = true;} 
                    def odeviationrequest = searchissue.getCustomFieldValue(fdeviationrequest)
                    if (odeviationrequest != null && odeviationrequest != contractissue.getCustomFieldValue(fdeviationrequest)){mcontractissue.setCustomFieldValue(fdeviationrequest,odeviationrequest); bChange = true;}
                    def ochangerequest = searchissue.getCustomFieldValue(fchangerequest)
                    if (ochangerequest != null && ochangerequest != contractissue.getCustomFieldValue(fchangerequest)){mcontractissue.setCustomFieldValue(fchangerequest,ochangerequest); bChange = true;}
            		def omrbauthority = searchissue.getCustomFieldValue(fmrbauthority)
                    if (omrbauthority != null && omrbauthority != contractissue.getCustomFieldValue(fmrbauthority)){mcontractissue.setCustomFieldValue(fmrbauthority,omrbauthority); bChange = true;}
                    
                    //Search QualityCodes
                    def ogeneric = searchissue.getCustomFieldValue(fgeneric)
                    String sgeneric = ""
                    if (ogeneric != null) 
                    {
                        if (sallcodes != ""){sallcodes = sallcodes +","}
                        sgeneric = ogeneric.toString();
                        sallcodes = sallcodes + sgeneric

                    }
                    if (sallcodes != "")
                    {
                        sallcodes = sallcodes.replaceAll(";",",")
                        sallcodes = sallcodes.replaceAll(", ",",")
                        sallcodes = sallcodes.toUpperCase();
                        //alog.debug sallcodes
                        def scodes = sallcodes.split(",");

                        Collection subTasks = searchissue.getSubTaskObjects()
                        //Check for subtasks
                        if (!subTasks.empty) 
                        {
                            subTasks.each 
                            {
                                subtask ->
                                //log.debug subtask
                                def oqcode = subtask.getCustomFieldValue(fqcode)
                                if (oqcode != null)
                                {
                                    String sqcode = oqcode.toString();
                                    String sqcodtite = ""
                                    def oqcodetitle = subtask.getCustomFieldValue(fqcodetitle)
                                    if (oqcodetitle != null)
                                    {
                                        sqcodtite = " " + oqcodetitle.toString();
                                    }
                                    //alog.debug "sqcode = ${sqcode}"
                                    for (code in scodes) 
                                    {
                                        //alog.debug "code = ${code}"
                                        boolean bgen = false;
                                        def sgenerics = sgeneric.split(",")
                                        for (sgen in sgenerics)
                                        {
                                            if (code.startsWith(sgen) && sqcode.contains(code))                                    
                                            {
                                                bgen = true;
                                                if (sqcode.contains("_GENERIC_"))
                                                {
                                                    sqcode = sqcode.replaceAll("_GENERIC_","");
                                                }
                                            }
                                        }
                                        if (sqcode.equals(code) || bgen)
                                        {
                                            //alog.debug "code=${code} equals sqcode=${sqcode}"
                                            if (!sFoundCodes.contains(code)) {sFoundCodes = code+","+sFoundCodes;}
                                            def statusname = subtask.getStatus().getName();
                                            if (statusname == "Reject")
                                            {
                                                sRej = sRej + sqcode + sqcodtite + ":\r\n" + subtask.getDescription() + "\r\n\r\n"                                       
                                            }
                                            def oflowdown = subtask.getCustomFieldValue(fflowdown)
                                            if (oflowdown != null)
                                            {
                                                sFlowdown = sFlowdown + sqcode + sqcodtite + ":\r\n" + oflowdown.toString() + "\r\n\r\n"
                                            }
                                            oflowdown = subtask.getCustomFieldValue(fqcflowdown)
                                            if (oflowdown != null)
                                            {
                                                sQCFlowdown = sQCFlowdown + sqcode + sqcodtite + ":\r\n" + oflowdown.toString() + "\r\n\r\n"
                                            }
                                            oflowdown = subtask.getCustomFieldValue(fpkgflowdown)
                                            if (oflowdown != null)
                                            {
                                                sPKGFlowdown = sPKGFlowdown + sqcode + sqcodtite + ":\r\n" + oflowdown.toString() + "\r\n\r\n"
                                            }
                                            if (code == "DISCLAMER"){sTop = sTop + subtask.getDescription() + "\r\n\r\n"}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            String sUnknownCodes = "";
            if (sallcodes != "")
            {
                def scodes = sallcodes.split(",");
                for (code in scodes)
                {
                    if (!sFoundCodes.contains(code))
                    {
                        sUnknownCodes = code + ", " + sUnknownCodes
                    }
                }
            }
            if (sUnknownCodes != "") 
            {
                sReturn = "UNKNOWN CODE(S): " + sUnknownCodes + "\r\n PLEASE ADD THEM \r\n\r\n" + sReturn
            }
            if (sTop != "")
            {
                sReturn = sReturn + "DISCLAMER:\r\n------------------------------------------------------\r\n" + sTop + "\r\n\r\n"
            }
            if (sRej != "") 
            {
                sReturn = sReturn + "Exceptions to purchase order are as follows:\r\n------------------------------------------------------\r\n" + sRej + "\r\n\r\n"
            }
            if (sFlowdown != "") 
            {
                sReturn = sReturn + "Specific Flowdowns are as follows:\r\n------------------------------------------------------\r\n" + sFlowdown + "\r\n\r\n"
            }
            if (sQCFlowdown != "") 
            {
                sReturn = sReturn + "Specific Quality Flowdowns are as follows:\r\n------------------------------------------------------\r\n" + sQCFlowdown + "\r\n\r\n"
            }
            if (sPKGFlowdown != "") 
            {
                sReturn = sReturn + "Specific Packaging Flowdowns are as follows:\r\n------------------------------------------------------\r\n" + sPKGFlowdown + "\r\n\r\n"
            }

            if (sReturn == "")
            {
                sReturn = null;
            }
            def fReviewField = customFieldManager.getCustomFieldObject("customfield_14900")
            def oReviewField = contractissue.getCustomFieldValue(fReviewField);
            String sReviewField = ""
            if (oReviewField != null) {sReviewField = oReviewField.toString()}
            if (sReviewField != sReturn)
            {
                bChange = true;
                mcontractissue.setCustomFieldValue(fReviewField,sReturn);            
            }
            //if (bChange){issueManager.updateIssue(user, mcontractissue, EventDispatchOption.ISSUE_UPDATED, false)}
            if (bChange){issueManager.updateIssue(user, mcontractissue, EventDispatchOption.DO_NOT_DISPATCH, false)}
        }
        return;
    }
}