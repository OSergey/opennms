import org.opennms.netmgt.provision.persist.requisition.RequisitionNode 
import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset
import org.opennms.netmgt.provision.persist.requisition.RequisitionCategory
import org.opennms.ocs.inventory.client.response.Software
 
RequisitionNode reqNode = new RequisitionNode()
java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("mysql", java.util.regex.Pattern.CASE_INSENSITIVE)

if (!computer.softwares.isEmpty()){
 for (Software software: computer.softwares){
  if (pattern.matcher(software.name).find()){
    reqNode.putCategory(new RequisitionCategory("DB_Server"))
  }
 }
}

return reqNode
