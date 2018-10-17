/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.administrative.item;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.jxpath.ri.compiler.Constant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dspace.app.xmlui.aspect.submission.submit.AccessStepUtil;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.CheckBox;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Select;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Bitstream;
import org.dspace.content.BitstreamFormat;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamFormatService;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.xml.sax.SAXException;

/**
 * 
 * Show a form allowing the user to edit a bitstream's metadata, the description
 * and format.
 *
 * @author Scott Phillips
 */
public class EditBitstreamForm extends AbstractDSpaceTransformer
{

	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_submit_save = message("xmlui.general.save");
	private static final Message T_submit_cancel = message("xmlui.general.cancel");
	private static final Message T_item_trail = message("xmlui.administrative.item.general.item_trail");
	
	private static final Message T_title = message("xmlui.administrative.item.EditBitstreamForm.title");
	private static final Message T_trail = message("xmlui.administrative.item.EditBitstreamForm.trail");
	private static final Message T_head1 = message("xmlui.administrative.item.EditBitstreamForm.head1");
	private static final Message T_file_label = message("xmlui.administrative.item.EditBitstreamForm.file_label");
	private static final Message T_primary_label = message("xmlui.administrative.item.EditBitstreamForm.primary_label");
	private static final Message T_primary_option_yes = message("xmlui.administrative.item.EditBitstreamForm.primary_option_yes");
	private static final Message T_primary_option_no = message("xmlui.administrative.item.EditBitstreamForm.primary_option_no");
	private static final Message T_description_label = message("xmlui.administrative.item.EditBitstreamForm.description_label");
	private static final Message T_description_help = message("xmlui.administrative.item.EditBitstreamForm.description_help");
	private static final Message T_para1 = message("xmlui.administrative.item.EditBitstreamForm.para1");
	private static final Message T_format_label = message("xmlui.administrative.item.EditBitstreamForm.format_label");
	private static final Message T_format_default = message("xmlui.administrative.item.EditBitstreamForm.format_default");
	private static final Message T_para2 = message("xmlui.administrative.item.EditBitstreamForm.para2");
	private static final Message T_user_label = message("xmlui.administrative.item.EditBitstreamForm.user_label");
	private static final Message T_user_help = message("xmlui.administrative.item.EditBitstreamForm.user_help");
    private static final Message T_filename_label = message("xmlui.administrative.item.EditBitstreamForm.name_label");
    private static final Message T_filename_help = message("xmlui.administrative.item.EditBitstreamForm.name_help");
    private static final Message T_open_access_label = message("xmlui.administrative.item.EditBitstreamForm.open_access_label");
    private static final Message T_open_access_help = message("xmlui.administrative.item.EditBitstreamForm.open_access_help");
    private static final Message T_open_access_checkBox_help = message("xmlui.administrative.item.EditBitstreamForm.open_access_checkBox_help");
    private static final Message T_open_access_title = message("xmlui.administrative.item.EditBitstreamForm.open_access_title");
    private static final Message T_open_access_embargo_disabled = message("xmlui.administrative.item.EditBitstreamForm.open_access_embargo_disabled");



    private boolean isAdvancedFormEnabled=true;

	protected AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
	protected BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();
	protected BitstreamFormatService bitstreamFormatService = ContentServiceFactory.getInstance().getBitstreamFormatService();

	private static final String[] DEFAULT_BUNDLE_LIST = new String[]{"ORIGINAL", "METADATA", "THUMBNAIL", "LICENSE", "CC-LICENSE"};
	private static final Message T_bundle_label = message("xmlui.administrative.item.AddBitstreamForm.bundle_label");
	protected ItemService itemService = ContentServiceFactory.getInstance().getItemService();

    @Override
	public void addPageMeta(PageMeta pageMeta) throws WingException
	{
		pageMeta.addMetadata("title").addContent(T_title);

		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrailLink(contextPath + "/admin/item",T_item_trail);
		pageMeta.addTrail().addContent(T_trail);
        pageMeta.addMetadata("javascript", "static").addContent("static/js/editItemUtil.js");
	}

    @Override
	public void addBody(Body body) throws SAXException, WingException,
	UIException, SQLException, IOException, AuthorizeException
	{

        isAdvancedFormEnabled= DSpaceServicesFactory.getInstance().getConfigurationService().getBooleanProperty("webui.submission.restrictstep.enableAdvancedForm", false);

		// Get our parameters
		UUID bitstreamID = UUID.fromString(parameters.getParameter("bitstreamID", null));
        UUID itemID = UUID.fromString(parameters.getParameter("itemID", null));
        org.dspace.content.Item item = itemService.find(context, itemID);
		// Get the bitstream and all the various formats
                // Administrator is allowed to see internal formats too.
		Bitstream bitstream = bitstreamService.find(context, bitstreamID);
		BitstreamFormat currentFormat = bitstream.getFormat(context);
                java.util.List<BitstreamFormat> bitstreamFormats = authorizeService.isAdmin(context) ?
					bitstreamFormatService.findAll(context) :
					bitstreamFormatService.findNonInternal(context);
		
		boolean primaryBitstream = false;
		java.util.List<Bundle> bundles = bitstream.getBundles();
		if (bundles != null && bundles.size() > 0)
		{
			if (bitstream.equals(bundles.get(0).getPrimaryBitstream()))
			{
				primaryBitstream = true;
			}
		}

		boolean isPrivateBitstream = this.isPrivateBitstream(bitstream);

		// File name & url
		String fileUrl = contextPath + "/bitstream/id/" +bitstream.getID() + "/" + bitstream.getName();
		String fileName = bitstream.getName();




		// DIVISION: main
		Division div = body.addInteractiveDivision("edit-bitstream", contextPath+"/admin/item", Division.METHOD_MULTIPART, "primary administrative item");    	
		div.setHead(T_head1);

		// LIST: edit form
		List edit = div.addList("edit-bitstream-list", List.TYPE_FORM);
        edit.addLabel(T_file_label);
        edit.addItem(null,"break-all").addXref(fileUrl, fileName);

        int bundleCount = 0; // record how many bundles we are able to upload too.
        Select select = edit.addItem().addSelect("bundle");
        select.setLabel(T_bundle_label);

        // Get the list of bundles to allow the user to upload too. Either use the default
        // or one supplied from the dspace.cfg.
        String[] bundlesNames = DSpaceServicesFactory.getInstance().getConfigurationService().getArrayProperty("xmlui.bundle.upload");
        if (ArrayUtils.isEmpty(bundlesNames))
        {
            bundlesNames = DEFAULT_BUNDLE_LIST;
        }
        for (String part : bundlesNames)
        {
            if (part.equals(bitstream.getBundles().get(0).getName())){
                select.addOption(true,part, message("xmlui.administrative.item.AddBitstreamForm.bundle." + part));
                bundleCount++;
            }
            else if (addBundleOption(item, select, part.trim()))
            {
                bundleCount++;
            }
        }

        Text bitstreamName = edit.addItem().addText("bitstreamName");
        bitstreamName.setLabel(T_filename_label);
        bitstreamName.setHelp(T_filename_help);
        bitstreamName.setValue(fileName);
		
		Select primarySelect = edit.addItem().addSelect("primary");
		primarySelect.setLabel(T_primary_label);
		primarySelect.addOption(primaryBitstream,"yes",T_primary_option_yes);
		primarySelect.addOption(!primaryBitstream,"no",T_primary_option_no);
		
		Text description = edit.addItem().addText("description");
		description.setLabel(T_description_label);
		description.setHelp(T_description_help);
		description.setValue(bitstream.getDescription());


		// System supported formats
		Select format = edit.addItem().addSelect("formatID");
		format.setHelp(T_para1);
		format.setLabel(T_format_label);

                // load the options menu, skipping the "Unknown" format since "Not on list" takes its place
                int unknownFormatID = bitstreamFormatService.findUnknown(context).getID();
		format.addOption(-1,T_format_default);
		for (BitstreamFormat bitstreamFormat : bitstreamFormats)
		{
            if (bitstreamFormat.getID() == unknownFormatID)
            {
                continue;
            }
			String supportLevel = "Unknown";
			if (bitstreamFormat.getSupportLevel() == BitstreamFormat.KNOWN)
            {
                supportLevel = "known";
            }
			else if (bitstreamFormat.getSupportLevel() == BitstreamFormat.SUPPORTED)
            {
                supportLevel = "Supported";
            }
			String name = bitstreamFormat.getShortDescription()+" ("+supportLevel+")";
            if (bitstreamFormat.isInternal())
            {
                name += " (Internal)";
            }
			int id = bitstreamFormat.getID();

			format.addOption(id,name);
		}
		if (currentFormat != null)
		{
			format.setOptionSelected(currentFormat.getID());
		}
		else
		{
			format.setOptionSelected(-1);
		}

		edit.addItem(T_para2);

		// User supplied format
		Text userFormat = edit.addItem().addText("user_format");
		userFormat.setLabel(T_user_label);
		userFormat.setHelp(T_user_help);
		userFormat.setValue(bitstream.getUserFormatDescription());


		//Open access fields
		List openAccessSection= edit.addList("openAccessSection",List.TYPE_FORM);
		openAccessSection.setHead(T_open_access_title);
		CheckBox privateBitstream = openAccessSection.addItem().addCheckBox("publicBitstream");
		privateBitstream.setLabel(T_open_access_label);
		privateBitstream.setHelp(T_open_access_help);
		privateBitstream.addOption(!isPrivateBitstream, "public-bitstream").addContent(T_open_access_checkBox_help);
		openAccessSection.addItem("embargo-disabled",null).addContent(T_open_access_embargo_disabled);
		// EMBARGO FIELD
		// if AdvancedAccessPolicy=false: add Embargo Fields.
		if(!isAdvancedFormEnabled){
			AccessStepUtil asu = new AccessStepUtil(context);
			// if the item is embargoed default value will be displayed.
			//asu.addEmbargoDateDisplayOnly(bitstream, edit);
			asu.addEmbargoDateSimpleForm(bitstream, openAccessSection, -1);
			asu.addReason(bitstream.getDetails(), openAccessSection, -1);

		}


		// ITEM: form actions
		org.dspace.app.xmlui.wing.element.Item actions = edit.addItem();
		actions.addButton("submit_save").setValue(T_submit_save);
		actions.addButton("submit_cancel").setValue(T_submit_cancel);

		div.addHidden("administrative-continue").setValue(knot.getId()); 

	}

	private boolean isPrivateBitstream(Bitstream bitstream) throws SQLException {
		GroupService groupService = EPersonServiceFactory.getInstance().getGroupService();
		Group anonymous=groupService.findByName(context, Group.ANONYMOUS);
		ResourcePolicy policy=authorizeService.findByTypeGroupAction(context, bitstream, anonymous, Constants.READ);
		return policy==null;
	}

	public boolean addBundleOption(org.dspace.content.Item item, Select select, String bundleName) throws SQLException, WingException
	{

            java.util.List<Bundle> bundles = itemService.getBundles(item, bundleName);
            if (bundles == null || bundles.size() == 0)
            {
                // No bundle, so the user has to be authorized to add to item.
                if(!authorizeService.authorizeActionBoolean(context, item, Constants.ADD))
                {
                    return false;
                }
            } else
            {
                // At least one bundle exists, does the user have privileges to upload to it?
                Bundle bundle = bundles.get(0);
                if (!authorizeService.authorizeActionBoolean(context, bundle, Constants.ADD))
                {
                    return false; // you can't upload to this bundle.
                }

                // You also need the write privlege on the bundle.
                if (!authorizeService.authorizeActionBoolean(context, bundle, Constants.WRITE))
                {
                    return false;  // you can't upload
                }
            }

            // It's okay to upload.
            select.addOption(bundleName, message("xmlui.administrative.item.AddBitstreamForm.bundle." + bundleName));
            return true;
        }
}
