/**
 * @file custom.proj.MrbScrap.groovy
 *
 * @brief Holds functions that interact with the Administration Project to allow users to approve MRB's for scrap if it is within a certain limit
 *
 * @ingroup custom.proj.MrbScrap
 * 
 *
 * @author Keith Jones
 * Contact: keithwj1@gmail.com
 *
 */

package custom.proj
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue

import com.atlassian.jira.bc.issue.search.SearchService 
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.search.SearchQuery

import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.user.ApplicationUser

import custom.global.DateFunctions

public class MrbScrap 
{
    //Get an adminstration user
    public static ApplicationUser getAdminUser()
    {
        def usrmgr = ComponentAccessor.userManager
        return usrmgr.getUserByName("Admin")
    }
    //copies curscarp to history and sets to 0.0
    public static void CopyScrapToHistory()
    {
        MrbScrap.UpdateScrapAmounts();
        def cfManager = ComponentAccessor.getCustomFieldManager();
        def fcuramt = cfManager.getCustomFieldObject("customfield_14704");
        def fuser = cfManager.getCustomFieldObject("customfield_13106");
        def fstartdate = cfManager.getCustomFieldObject("customfield_14706");
        def fenddate = cfManager.getCustomFieldObject("customfield_14707");
        def fhistory = cfManager.getCustomFieldObject("customfield_14705");

        def issueManager = ComponentAccessor.issueManager
        def MrbAccounting = "AD-1";
        def accountingissue = issueManager.getIssueObject(MrbAccounting)         
        def usrmgr = ComponentAccessor.userManager      
        def ostartdate = accountingissue.getCustomFieldValue(fstartdate);
        def oenddate = accountingissue.getCustomFieldValue(fenddate);
        if (ostartdate == null || oenddate == null)
        {
            MrbScrap.SetStartAndEndDates();
            ostartdate = accountingissue.getCustomFieldValue(fstartdate);
            oenddate = accountingissue.getCustomFieldValue(fenddate);
        }
        def denddate = oenddate as Date
        def dstartdate = ostartdate as Date
        def senddate = denddate.format('yyyy-MMM-dd');
        def sstartdate = dstartdate.format('yyyy-MMM-dd');

        //update main project
		/*
        def ocuramt = accountingissue.getCustomFieldValue(fcuramt);
        String scuramt = "";
        if (ocuramt != null)
        {
            scuramt = ocuramt.toString();      
        }

        def ohistory = accountingissue.getCustomFieldValue(fhistory);
        String shistory = ""
        if (ohistory != null){shistory = ohistory.toString()}
        shistory = shistory + "Between ${sstartdate} and ${senddate} The Scrap Cost Was ${scuramt}"+"\r\n";
        accountingissue.setCustomFieldValue(fhistory,shistory);
        accountingissue.setCustomFieldValue(fcuramt,null);
		*/
        Collection subTasks = accountingissue.getSubTaskObjects()
		subTasks.add(accountingissue);
        if (!subTasks.empty) 
        {
            subTasks.each
            {
                subissue ->
                def ocuramt = subissue.getCustomFieldValue(fcuramt);
                String scuramt = "";
                if (ocuramt != null)
                {
                    scuramt = ocuramt.toString();      
                }
                def ohistory = subissue.getCustomFieldValue(fhistory);
                String shistory = ""
                if (ohistory != null){shistory = ohistory.toString()}
                shistory = shistory + "Between ${sstartdate} and ${senddate} The Scrap Cost Was ${scuramt}"+"\r\n";
                def msubissue = issueManager.getIssueObject(subissue.getId())
                msubissue.setCustomFieldValue(fcuramt,null);
                msubissue.setCustomFieldValue(fhistory,shistory);
                issueManager.updateIssue(getAdminUser(), msubissue, EventDispatchOption.ISSUE_UPDATED, false)
            }
        }
    }
    //Sets Fiscal Month
    public static void SetStartAndEndDates()
    {
        def issueManager = ComponentAccessor.issueManager
		def MrbAccounting = "AD-1";
		def accountingissue = issueManager.getIssueObject(MrbAccounting) 
		def cfManager = ComponentAccessor.getCustomFieldManager()
		def fStartDate = cfManager.getCustomFieldObject("customfield_14706");
		def fEndDate = cfManager.getCustomFieldObject("customfield_14707");

		def oStartDate = accountingissue.getCustomFieldValue(fStartDate)
		def oEndDate = accountingissue.getCustomFieldValue(fEndDate)
		if (oEndDate != null && oStartDate != null)
		{
			Date dEndDate = oEndDate as Date;
			def today = new Date()
            def yesterday = new Date().plus(-1)
			//today is the end of the month
			if (yesterday > dEndDate)
			{
				//set new dates
				Calendar firstday = DateFunctions.getFirstWorkingDay();
				Calendar lastday = DateFunctions.getLastWorkingDay();
				if (lastday.getTime() > dEndDate)
				{
					lastday = DateFunctions.getLastWorkingDay(1);
					firstday = DateFunctions.getFirstWorkingDay(1);
				}
				accountingissue.setCustomFieldValue(fStartDate, firstday.getTime().toTimestamp());
				accountingissue.setCustomFieldValue(fEndDate, lastday.getTime().toTimestamp());
				issueManager.updateIssue(MrbScrap.getAdminUser(), accountingissue, EventDispatchOption.ISSUE_UPDATED, false)
			}
		}
		else
		{
			Calendar firstday = DateFunctions.getFirstWorkingDay();
			Calendar lastday = DateFunctions.getLastWorkingDay();
			accountingissue.setCustomFieldValue(fStartDate, firstday.getTime());
			accountingissue.setCustomFieldValue(fEndDate, lastday.getTime());
			issueManager.updateIssue(MrbScrap.getAdminUser(), accountingissue, EventDispatchOption.ISSUE_UPDATED, false)
		}
    }
    //Update Scrap amounts for each user in the AD-1 Project
	public static void UpdateScrapAmounts()
    {
        MrbScrap.UpdateTotalScrapAmounts();
        def usrmgr = ComponentAccessor.userManager

        def cfManager = ComponentAccessor.getCustomFieldManager()
        def fcuramt = cfManager.getCustomFieldObject("customfield_14704");
        def fuser = cfManager.getCustomFieldObject("customfield_13106");
        def fscrapcost = cfManager.getCustomFieldObject("customfield_13217");

        def fstartdate = cfManager.getCustomFieldObject("customfield_14706");
        def fenddate = cfManager.getCustomFieldObject("customfield_14707");
        def issueManager = ComponentAccessor.issueManager
        def MrbAccounting = "AD-1";
        def accountingissue = issueManager.getIssueObject(MrbAccounting) 
        def ostartdate = accountingissue.getCustomFieldValue(fstartdate);
        def oenddate = accountingissue.getCustomFieldValue(fenddate);

        def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
        def searchService = ComponentAccessor.getComponent(SearchService.class)

        def searchuser = MrbScrap.getAdminUser()

        def denddate = oenddate as Date
        def dstartdate = ostartdate as Date 

        def senddate = denddate.format('yyyy-MM-dd');
        def sstartdate = dstartdate.format('yyyy-MM-dd');  
        Collection subTasks = accountingissue.getSubTaskObjects()
        if (!subTasks.empty) 
        {
            subTasks.each
            {
                topissue ->
                def ocurUser = topissue.getCustomFieldValue(fuser);       
                String scurUser = "NotRequired"
                if (ocurUser != null)
                {
                    def aUser = ocurUser as ApplicationUser
                    scurUser = aUser.getName()
                }
                def queryString = "project = MRB AND issuetype = Defect AND Disposition = Scrap AND 'Scrap Cost' IS NOT EMPTY AND 'Ops Approver' = ${scurUser} AND 'Operations Approval Date' >= ${sstartdate} AND 'Operations Approval Date' <= ${senddate}"
                def query = jqlQueryParser.parseQuery(queryString)
                def results = searchService.search(searchuser, query, PagerFilter.getUnlimitedFilter())
                double dtotalscrap = 0.0
                results.getResults().each 
                {
                    scrapissue ->
                    def oscrapcost = scrapissue.getCustomFieldValue(fscrapcost);
                    if (oscrapcost != null)
                    {
                        double dscrapcost = oscrapcost as double
                        dtotalscrap = dtotalscrap + dscrapcost;
                    }
                }
                //alog.debug dtotalscrap + " For $scurUser"
                def mtopissue = issueManager.getIssueObject(topissue.getId())
                mtopissue.setCustomFieldValue(fcuramt, dtotalscrap)
                issueManager.updateIssue(searchuser, mtopissue, EventDispatchOption.ISSUE_UPDATED, false)
            }
        }
    }
    //Updates the Total Scrap amount in the AD-1 Project
    public static void UpdateTotalScrapAmounts()
    {
        def cfManager = ComponentAccessor.getCustomFieldManager()
        def fcuramt = cfManager.getCustomFieldObject("customfield_14704");
        def fuser = cfManager.getCustomFieldObject("customfield_13106");
        def fscrapcost = cfManager.getCustomFieldObject("customfield_13217");
        
        def fstartdate = cfManager.getCustomFieldObject("customfield_14706");
        def fenddate = cfManager.getCustomFieldObject("customfield_14707");
        def issueManager = ComponentAccessor.issueManager
        def MrbAccounting = "AD-1";
        def accountingissue = issueManager.getIssueObject(MrbAccounting) 
        def ostartdate = accountingissue.getCustomFieldValue(fstartdate);
        def oenddate = accountingissue.getCustomFieldValue(fenddate);
        
        def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
        def searchService = ComponentAccessor.getComponent(SearchService.class)
        
        def searchuser = getAdminUser()
        
        def denddate = oenddate as Date
        def dstartdate = ostartdate as Date 

        def senddate = denddate.format('yyyy-MM-dd');
        def sstartdate = dstartdate.format('yyyy-MM-dd');    
        def queryString = "project = MRB AND issuetype = Defect AND Disposition = Scrap AND 'Scrap Cost' IS NOT EMPTY AND 'Operations Approval Date' >= ${sstartdate} AND 'Operations Approval Date' <= ${senddate}"
        def query = jqlQueryParser.parseQuery(queryString)
        def results = searchService.search(searchuser, query, PagerFilter.getUnlimitedFilter())
        double dtotalscrap = 0.0
        results.getResults().each 
        {
            scrapissue ->
            def oscrapcost = scrapissue.getCustomFieldValue(fscrapcost);
            if (oscrapcost != null)
            {
                double dscrapcost = oscrapcost as double
                dtotalscrap = dtotalscrap + dscrapcost;
            }
        }
        //def mtopissue = issueManager.getIssueObject(topissue.getId())
        accountingissue.setCustomFieldValue(fcuramt, dtotalscrap)
        issueManager.updateIssue(searchuser, accountingissue, EventDispatchOption.ISSUE_UPDATED, false)
    }
    //provide username
    //Updates scrap for specific user in AD-1 project
    public static void UpdateUserScrapAmounts(ApplicationUser ocurUser)
    {
        def cfManager = ComponentAccessor.getCustomFieldManager()
        def fcuramt = cfManager.getCustomFieldObject("customfield_14704");
        def fuser = cfManager.getCustomFieldObject("customfield_13106");
        def fscrapcost = cfManager.getCustomFieldObject("customfield_13217");

        def fstartdate = cfManager.getCustomFieldObject("customfield_14706");
        def fenddate = cfManager.getCustomFieldObject("customfield_14707");
        def issueManager = ComponentAccessor.issueManager
        def MrbAccounting = "AD-1";
        def accountingissue = issueManager.getIssueObject(MrbAccounting) 
        def ostartdate = accountingissue.getCustomFieldValue(fstartdate);
        def oenddate = accountingissue.getCustomFieldValue(fenddate);

        def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
        def searchService = ComponentAccessor.getComponent(SearchService.class)

        def usrmgr = ComponentAccessor.userManager
        def searchuser = MrbScrap.getAdminUser()
        String scurUser = "NotRequired"
        if (ocurUser != null)
        {
            scurUser = ocurUser.getName();
        }
        def denddate = oenddate as Date
        def dstartdate = ostartdate as Date 

        def senddate = denddate.format('yyyy-MM-dd');
        def sstartdate = dstartdate.format('yyyy-MM-dd');
        def topqueryString = "project = 'AD' AND issuetype = 'Limit' AND 'Pick User' = ${scurUser} AND issueFunction in subtasksOf('project = AD AND issuetype = Accounting And Key = AD-1')"  
        def topquery = jqlQueryParser.parseQuery(topqueryString)                                                 
        def topresults = searchService.search(searchuser, topquery, PagerFilter.getUnlimitedFilter())
        for (topissue in topresults.getResults())
        {     
            //def curUser = topissue.getCustomFieldValue(fuser);
            def queryString = "project = 'MRB' AND issuetype = 'Defect' AND 'Disposition' = 'Scrap' AND 'Scrap Cost' IS NOT EMPTY AND 'Ops Approver' = ${scurUser} AND 'Operations Approval Date' >= ${sstartdate} AND 'Operations Approval Date' <= ${senddate}"
            def query = jqlQueryParser.parseQuery(queryString)
            def results = searchService.search(searchuser, query, PagerFilter.getUnlimitedFilter())
            double dtotalscrap = 0.0
            for (scrapissue in results.getResults())
            {       
                def oscrapcost = scrapissue.getCustomFieldValue(fscrapcost);
                if (oscrapcost != null)
                {
                    double dscrapcost = oscrapcost as double  
                    dtotalscrap = dtotalscrap + dscrapcost;
                }
            }
            def mtopissue = issueManager.getIssueObject(topissue.getId())
            mtopissue.setCustomFieldValue(fcuramt, dtotalscrap)
            issueManager.updateIssue(searchuser, mtopissue, EventDispatchOption.ISSUE_UPDATED, false)
        }
    }
    //1 for specific user
    //2 for NotRequired
    //Returns 1 if the current user is able to approve the current scrap amount.
    public static int ScrapWithinLimit(Issue issue, ApplicationUser aUser)
    {
        def issueManager = ComponentAccessor.issueManager
        def cfManager = ComponentAccessor.getCustomFieldManager()
        def ftotallimit = cfManager.getCustomFieldObject("customfield_14701");
        def fperlimit = cfManager.getCustomFieldObject("customfield_14702");
        def fcuramt = cfManager.getCustomFieldObject("customfield_14704");
        def fhistamt = cfManager.getCustomFieldObject("customfield_14705");
        def fuser = cfManager.getCustomFieldObject("customfield_13106");
        def fscrapcost = cfManager.getCustomFieldObject("customfield_13217");

        def oscrapcost = issue.getCustomFieldValue(fscrapcost);
        //no scrap cost on defect. skip
        if (oscrapcost == null) {return 0}


        def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
        def searchService = ComponentAccessor.getComponent(SearchService.class)

        //def usrmgr = ComponentAccessor.userManager
        //def loginuser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        def loginuser = aUser;
        def searchuser = MrbScrap.getAdminUser()
        def MrbAccounting = "AD-1";

        def accountingissue = issueManager.getIssueObject(MrbAccounting)
        def ototallimit = accountingissue.getCustomFieldValue(ftotallimit);
        def ocuramt = accountingissue.getCustomFieldValue(fcuramt);
        double dscrapcost = 0.0;
        if (oscrapcost != null) {dscrapcost = oscrapcost as double}

        //Total approval limit too high
        if (ototallimit != null)
        {
            if (ocuramt == null) {ocuramt = 0.0}
            double dcuramt = ocuramt as double

                double dtotallimit = ototallimit as double
                if ((dcuramt + dscrapcost) >= dtotallimit){return 0;}
        }

        //General limits (not required) depreciated as General Limits were not approved
        if (1==2)
        {
            def generalissue = issueManager.getIssueObject("AD-4");
            def ogencuramt = generalissue.getCustomFieldValue(fcuramt);
            def ogentotallimit = generalissue.getCustomFieldValue(ftotallimit);

            def ogenperlimit = generalissue.getCustomFieldValue(fperlimit);   
            if (ogentotallimit != null)
            {
                if (ogencuramt == null) {ogencuramt = 0.0}
                if (ogenperlimit == null) {ogenperlimit = 0.0}
                double dcuramt = ogencuramt as double
                double dperlimit = ogenperlimit as double
                double dtotallimit = ogentotallimit as double
                if (dscrapcost <= dperlimit) 
                {
                    if ((dscrapcost + dcuramt) <= dtotallimit)
                    {
                        return 2;
                    }
                }
            }  
        }
        String scurUser = ""
        if (aUser != null)
        {
            scurUser = aUser.getName()
        }
        //def queryString = "project = AD AND issuetype = Accounting AND Related Project ~ 'MRB'"
        //def queryString = "project = AD AND issuetype = 'Limit' AND status = Open AND 'Pick User' in (currentUser()) AND issueFunction in subtasksOf('project = AD AND issuetype = Accounting And Key = ${MrbAccounting}')"
        def queryString = "project = AD AND issuetype = 'Limit' AND status = Open AND 'Pick User' = ${scurUser} AND issueFunction in subtasksOf('project = AD AND issuetype = Accounting And Key = ${MrbAccounting}')"
        def query = jqlQueryParser.parseQuery(queryString)
        def results = searchService.search(searchuser, query, PagerFilter.getUnlimitedFilter())
        //Should only return one project
        for (limitissue in results.getResults())
        {
            def ousercuramt = limitissue.getCustomFieldValue(fcuramt);
            def ousertotallimit = limitissue.getCustomFieldValue(ftotallimit);
            def ouserperlimit = limitissue.getCustomFieldValue(fperlimit);
            if (ousertotallimit != null)
            {
                if (ousercuramt == null) {ousercuramt = 0.0}
                if (ouserperlimit == null) {ouserperlimit = 0.0}
                double dcuramt = ousercuramt as double
                double dperlimit = ouserperlimit as double
                double dtotallimit = ousertotallimit as double
                if (dscrapcost <= dperlimit) 
                {
                    double dtemp = (dscrapcost + dcuramt)
                    if(dtotallimit >= dtemp)
                    {
                        return 1
                    }
                }
            }
        }
        return 0
    }
}