
package edu.umich.brcf.metabolomics.panels.admin.system_info;


import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.InstrumentRegistry;
import edu.umich.brcf.metabolomics.layers.service.InstrumentService;

public class SystemInfoPanel extends Panel {
	@SpringBean
	InstrumentService instrumentService;

	ListView systemView;

	//@SpringBean
	//METWorksRemoteJmsMessage remoteJmsMessage;

	public SystemInfoPanel(String id) {
		super(id);
		setOutputMarkupId(true);

		IModel instrumentModel = new LoadableDetachableModel() {
			protected Object load() {
				return instrumentService.loadInstrumentRegistry();
			}
		};

		systemView = new ListView("systems", instrumentModel) {
			protected void populateItem(ListItem item) {
				InstrumentRegistry entry = (InstrumentRegistry) item.getModelObject();
				item.add(new Label("instrumentName", entry.getInstrument().getName()));
				item.add(new Label("instrumentId", entry.getInstrument().getInstrumentID()));
				item.add(new Label("instrumentStatus", entry.getCurrentStatus()));
				item.add(new Label("instrumentIPAddr", entry.getIpAddress()));
				item.add(new Label("instrumentQueue", entry.getQueueName()));
				item.add(new Label("instrumentHostName", entry.getHostName()));
				WebMarkupContainer container = new WebMarkupContainer("info");
				item.add(container);
				if (entry.getCurrentStatus().equals("OFFLINE"))
					container.setVisible(false);
				else {
					container.setVisible(true);
					// try {
					// SystemInformationMessage message =
					// (SystemInformationMessage) remoteJmsMessage
					// .getMessageFromRemote(new
					// SystemInformationQueryMessage(), entry.getQueueName());
					//
					// container.add(new Label("osVersion",
					// message.getOsVersion()));
					// container.add(new Label("osName", message.getOsName()));
					// container.add(new Label("jvmInitialMemory",
					// message.getJvmInitialMemory().toString()));
					// container.add(new Label("jvmMaxMemory",
					// message.getJvmMaxMemory().toString()));
					// container.add(new Label("jvmTotalMemory",
					// message.getJvmTotalMemory().toString()));
					// container.add(new Label("freePhysicalMemory",
					// message.getSystemFreePhyicalMemory().toString()));
					// container.add(new Label("physicalMemory",
					// message.getSystemPhysicalMemory().toString()));
					// ListView fileSystems = new ListView("fileSystems",
					// message.getFileSystemDetails()) {
					// protected void populateItem(ListItem fileSystemItem) {
					// FileSystemDetails details = (FileSystemDetails)
					// fileSystemItem.getModelObject();
					// fileSystemItem.add(new Label("drive",
					// details.getDriveLetter()));
					// fileSystemItem.add(new Label("volume",
					// details.getDriveVolumeName()));
					// fileSystemItem.add(new Label("totalSpace",
					// details.getTotalSpace().toString()));
					// fileSystemItem.add(new Label("freeSpace",
					// details.getFreeSpace().toString()));
					// fileSystemItem.add(new Label("usableSpace",
					// details.getUsableSpace().toString()));
					// }
					// };
					// container.add(fileSystems);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
				}
			}
		};
		add(systemView);
	}
}
