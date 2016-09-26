package org.dspace.app.xmlui.aspect.ELProcessor;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.MetadataField;

public class TransformationManager extends Manager{


	public static List<Item> modifyItemsFromHandle(String handle, String metadata, String newValue) throws Exception{
		
		MetadataField metadataField=metadataManager.getMetadataFieldFromString(metadata);
		if(metadataField == null){
			throw new Exception("There is no such metadata");
		}		
		handleManager.handleResolver(handle);		
		if((dso instanceof Item)){
			 updateItem((Item)dso, metadataField, newValue);			 
		}else if(dso instanceof Collection){
			 updateItemsFromCollection((Collection)dso, metadataField, newValue);
		}else if(dso instanceof Community){
			updateItemsFromCommunity((Community)dso, metadataField, newValue);
		}
		
		return (List<Item>)(List<?>) result;
		

	}
	
	private static void updateItem(Item item, MetadataField metadataField, String newValue) throws SQLException, AuthorizeException{
		itemService.clearMetadata(MainProcessor.getContext(), item, metadataField.getMetadataSchema().getName(), metadataField.getElement(), metadataField.getQualifier(), "es");
		itemService.addMetadata(MainProcessor.getContext(), item, metadataField, "es", newValue);		
		itemService.update(MainProcessor.getContext(), item);
		result.add(dso);
	}
	
	private static void updateItemsFromCollection(Collection collection, MetadataField metadataField, String newValue) throws SQLException, AuthorizeException{
		Iterator<Item> iterator=itemService.findAllByCollection(MainProcessor.getContext(), collection);		
		while(iterator.hasNext()){
			Item item = iterator.next();
			updateItem(item, metadataField, newValue);
		}
	}
	
	private static void updateItemsFromCommunity(Community community, MetadataField metadataField, String newValue) throws SQLException, AuthorizeException{
		for(Community com: community.getSubcommunities()){
					updateItemsFromCommunity(com, metadataField, newValue);
		}
		for(Collection col: community.getCollections()){
			updateItemsFromCollection(col, metadataField, newValue);
		}
	}
	
	
	
}
