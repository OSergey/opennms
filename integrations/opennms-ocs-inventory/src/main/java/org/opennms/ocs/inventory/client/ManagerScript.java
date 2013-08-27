package org.opennms.ocs.inventory.client;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.ocs.inventory.client.response.Computer;

final public class ManagerScript {
	
	/** The manager. */
	private BSFManager m_manager = new BSFManager();
	
	private static final String s_computer = "computer";

	/**
	 * Execute script.
	 *
	 * @param lang the lang
	 * @param script the script
	 * @param source the source
	 * @param computer the computer
	 * @return the object
	 * @throws BSFException the bSF exception
	 */
	public Object executeScript(String lang, String script, String source,
			Computer computer) {
		log().debug(
				"Start execute Script language= " + lang + " content script= "
						+ script + "source= " + source + "computer="
						+ computer.toString());
		Object obj = null;
		try {
			m_manager.declareBean(s_computer, computer, Computer.class);
			obj= m_manager.eval(lang, source, 0, 0, script);
		} catch (BSFException e) {
			log().error(String.format("Error while executing script, error message =%s", e.getMessage()));
		}
		log().debug(String.format("Result object=%s", obj));
		return obj;
	}
	
    protected static ThreadCategory log() {
        return ThreadCategory.getInstance(OcsInventoryUtils.class);
    }

}
