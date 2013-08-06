import org.opennms.ocs.inventory.client.response.Computer

def mappingModelData(Computer cmp) {
    return cmp.bios.SModel
}