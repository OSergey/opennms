import org.opennms.netmgt.provision.persist.requisition.RequisitionAsset
import org.opennms.netmgt.provision.persist.requisition.RequisitionNode
import org.opennms.ocs.inventory.client.response.Computer

def mappingModelData(Computer computer) {
    RequisitionNode reqNode = new RequisitionNode()
    if (computer.bios != null) {
        reqNode.putAsset(new RequisitionAsset("BiosModel", computer.bios.SModel))
    }
    return reqNode;
}