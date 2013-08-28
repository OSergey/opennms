import org.opennms.netmgt.provision.persist.requisition.RequisitionNode 
import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset 
RequisitionNode reqNode = new RequisitionNode()
if (computer.bios != null) { 
reqNode.putAsset(new RequisitionAsset("BiosModel", computer.bios.SModel));
} 

return reqNode
