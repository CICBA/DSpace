function create_filter_badgets (){
    if (window.DSpace.discovery.filters.length != 0) {
        renderFilterSection();
    };        
    

	function renderFilterSection(){
		filters_list=$('#filters-overview');
		filters_list.removeClass("hidden");
		filters_list.find('hr').remove();
		for (var i = 0; i < DSpace.discovery.filters.length; i++) {
		    filter_div=createFilterDiv(i);
		    filters_list.append(filter_div);
		}
	}
	
	function eventsHandler(index){
       $('#aspect_discovery_SimpleSearch_field_remove-filter_'+index).click();
       $('#aspect_discovery_SimpleSearch_div_search-filters').submit();

	}
	
	function removeFilterAtIndex(index){
		DSpace.discovery.filters.splice(index,1);	
	}
	
	function createFilterDiv(index){
		 filter = DSpace.discovery.filters[index];
		 filter_div = document.createElement("div");
		 filter_div.id="used_filter_"+index;
		 filter_div.title=filter.type+": "+filter.query;
		 closeButton=document.createElement("span");
		 closeButton.className="glyphicon glyphicon-remove";
		 filter_div.append(closeButton);
		 text=document.createElement("p");
		 if (filter.relational_operator.startsWith("not"))
			 text.innerHTML=filter.type+" &#8800 "+filter.query;
		 else
			 text.innerHTML=filter.type+": "+filter.query;
		 filter_div.append(text);
		 filter_div.className="btn btn-info";
		 filter_div.setAttribute("index", index);
		
		 filter_div.onclick=function (e) {
		    	removeFilterAtIndex(e.currentTarget.attributes.index.nodeValue);
		    	eventsHandler(parseInt(e.currentTarget.attributes.index.nodeValue)+1);
		    	return true;
	     };
	     
	     return filter_div;
	}
	

	
	
	
	
};