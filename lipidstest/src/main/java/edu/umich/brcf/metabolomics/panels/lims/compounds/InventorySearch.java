// Revisited : September 2016 (JW)

package edu.umich.brcf.metabolomics.panels.lims.compounds;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import edu.umich.brcf.metabolomics.layers.domain.Compound;
import edu.umich.brcf.metabolomics.layers.service.CompoundService;
import edu.umich.brcf.metabolomics.layers.service.InventoryService;
import edu.umich.brcf.shared.layers.domain.Inventory;


public class InventorySearch extends WebPage 
	{
	@SpringBean
	InventoryService invService;
	
	@SpringBean
	CompoundService cmpdService;
	
	public InventorySearch(Page backPage) 
		{
		add(new FeedbackPanel("feedback"));
		add(new InventorySearchForm("inventorySearchForm"));
		add( new EmptyPanel("inventoryDetailPanel"));
		}
	
	public InventorySearch(Page backPage, List<Inventory> invList, Compound cmpd) 
		{
		add(new FeedbackPanel("feedback"));
		add(new InventorySearchForm("inventorySearchForm"));
		add(new InventoryDetailPanel("inventoryDetailPanel", cmpd));
		}
	
	public final class InventorySearchForm extends Form 
		{
		public InventorySearchForm(final String id)
			{
			super(id);
			final TextField cidSrch, canSrch, invIdSrch, catnumSrch;
			add(cidSrch = new TextField("cidSrch", new Model()));
			add(canSrch = new TextField("canSrch", new Model()));
			add(invIdSrch = new TextField("invIdSrch", new Model()));
			add(catnumSrch = new TextField("catnumSrch", new Model()));
			
			add(new Button("srchByCid")
				{
				@Override
				public void onSubmit() 
					{
					String cid=cidSrch.getInput();
					try
						{
						Compound cmpd= cmpdService.loadCompoundById(cid);
						List<Inventory> invList=invService.loadByCid(cid);
						setResponsePage(new InventorySearch(getPage(),invList, cmpd));
						}
					catch(Exception e)
						{
						InventorySearch.this.error("CID not found. Please try again......");
						setResponsePage(new InventorySearch(getPage()));
						}
					}
				});
			
			add(new Button("srchByCan")
				{
				@Override
				public void onSubmit() {
					String can=canSrch.getInput();
					try{
					Compound cmpd= cmpdService.loadCompoundByCan(can);
					List<Inventory> invList=invService.loadByCan(can);
					setResponsePage(new InventorySearch(getPage(),invList, cmpd));
					}catch(Exception e){
						InventorySearch.this.error("CAS Number not found. Please try again......");
						setResponsePage(new InventorySearch(getPage()));
					}
				}
			});
			
			add(new Button("srchByInvId"){
				@Override
				public void onSubmit() {
					String invId=invIdSrch.getInput();
					try{
					Compound cmpd= (invService.loadById(invId)).getCompound();
					List<Inventory> invList = new ArrayList();
					invList.add(invService.loadById(invId));
					setResponsePage(new InventorySearch(getPage(),invList, cmpd));
					}catch(Exception e){
						InventorySearch.this.error("Inventory ID not found. Please try again......");
						setResponsePage(new InventorySearch(getPage()));
					}
				}
			});
			
			add(new Button("srchByCatnum")
				{
				@Override
				public void onSubmit() 
					{
					String catnum=catnumSrch.getInput();
					try
						{
						Compound cmpd= cmpdService.loadByCatnum(catnum);
						List<Inventory> invList=invService.loadByCid(cmpd.getCid());
						setResponsePage(new InventorySearch(getPage(),invList, cmpd));
						}
					catch(Exception e)
						{
						InventorySearch.this.error("Catalog # not found! Please try again......");
						setResponsePage(new InventorySearch(getPage()));
						}
					}
				});
			}
		}
	}
