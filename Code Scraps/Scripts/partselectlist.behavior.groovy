import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.option.Option
import org.apache.log4j.Logger

import custom.global.Part
import custom.global.PartList

def clog = Logger.getLogger("custom.global.classes")

def bPartNumber = getFieldById('customfield_10705');
def oPartNumber = bPartNumber.getValue()
String sbPartNumber = ""
if (oPartNumber != null){sbPartNumber = oPartNumber.toString()}
def bFill = true

def bPartSelectList = getFieldById('customfield_15700');
def bPartInformation = getFieldById('customfield_15800');
def oPartInformation = bPartInformation.getValue()
def oPartSelectList = bPartSelectList.getValue()
if (oPartSelectList != null)
{
	String sPartSelectList = oPartSelectList.toString();
    if (sPartSelectList == "None")
    {
        bFill = false
    }
}
if (oPartSelectList == null)
{
    oPartSelectList = oPartNumber
}
if (oPartInformation != null && oPartSelectList != null)
{
    String sPartInformation = oPartInformation.toString();
    PartList oPartList = new PartList()
    oPartList.AddPartsFromString(sPartInformation)
    String sPartSelectList = oPartSelectList.toString();
    

    //Now Limit the options.
    List<String> values = []
    def fPartSelectList = customFieldManager.getCustomFieldObject('customfield_15700')
    def PartSelectConfig = fPartSelectList.getRelevantConfig(getIssueContext())
    def optionsManager = ComponentAccessor.getOptionsManager()
    def options = optionsManager.getOptions(PartSelectConfig)
    List<Part> aPart = oPartList.GetPartList()
    
    for (Part in aPart)
    {
        if (Part != null)
        {
            String sCurNumber = Part.GetPartNumber()
            if (sCurNumber != '')
            {
                if (!sCurNumber.startsWith('*') || sCurNumber.contains('OSP') || 1==1)
                {
                    def option = options.find {it.value == sCurNumber}
                    if (option == null)
                    {
                        def SeqMax = options*.sequence.max()
                        def newSeqId = 0
                        if (SeqMax != null)
                        {
                            newSeqId = SeqMax - 1
                        }
                        Option newOption = optionsManager.createOption(PartSelectConfig, null, newSeqId, sCurNumber)
                    }
                    values.add(sCurNumber)
                }
            }
        }
    }
    if (!values.isEmpty())
    {
        if (1==2)
        {
            java.util.Map map = values.inject(['':'None']){ map, item ->
                map[item]= item
                map
            }
            def bPartNum = getFieldById("customfield_10705");
            bPartNum.convertToSingleSelect().setFieldOptions(map)
        }
        PartSelectConfig = fPartSelectList.getRelevantConfig(getIssueContext())
        options = optionsManager.getOptions(PartSelectConfig)
        values.add("None")
        bPartSelectList.setFieldOptions(options.findAll{it.value in values})
        //Changed from == to in
        def noneoption = options.find {it.value == "None"}
        bPartSelectList.setFormValue(noneoption.optionId)   
        def defaultoption = options.find {it.value in sPartSelectList}
        if (defaultoption != null)
        {
        	bPartSelectList.setFormValue(defaultoption.optionId)      
        }
    }
    
    //Set Values of Corresponding Fields
    Part oPart = oPartList.FindPart(sPartSelectList)
    if (oPart != null && bFill)
    {
        String sPartNumber = oPart.GetPartNumber()
        String sRevision = oPart.GetRevision()
        String sEngNo = oPart.GetEngNumber()
        String sDescription = oPart.GetPartDescription()
        if (sPartNumber != '')
        {
            bPartNumber.setFormValue(sPartNumber);
        }
		if (sEngNo != '')
        {
        	def bEngNo = getFieldById('customfield_14001');
            bEngNo.setFormValue(sEngNo);
        }
        if (sRevision != '')
        {
            def bRevision = getFieldById('customfield_11003');
            bRevision.setFormValue(sRevision);
        }
        if (sDescription != '')
        {
            def bDescription = getFieldById('customfield_11062');
            bDescription.setFormValue(sDescription);
        }
    }
}