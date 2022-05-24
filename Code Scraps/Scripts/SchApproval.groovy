package custom.proj
import custom.global.approval
public class SchApproval extends custom.global.approval {
    SchApproval() {          
        this.bQualityEngRequired = true;
        this.bDesignEngRequired = true;
        this.bMfgEngRequired = false;
        this.bMaterialsRequired = true;
        this.bOperationsRequired = false;
    }    
}