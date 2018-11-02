function embargo_toggle (){
	checkBox=document.getElementsByName('publicBitstream')[0];
	embargoDisabled=document.querySelector("[id$='embargo-disabled']");
	if (!checkBox.checked){
        document.getElementsByName("embargo_until_date")[0].disabled = true;
    	document.getElementsByName("reason")[0].disabled = true;
    	embargoDisabled.style.display="block";
    	embargoDisabled.style.color="red";
    	embargoDisabled.style.marginBottom="0px";
    }
	else{
		embargoDisabled.style.display="none";
	}
	checkBox.onclick= function(e){
    	embargoDisabled=document.querySelector("[id$='embargo-disabled']");
		if (checkBox.checked){
	        document.getElementsByName("embargo_until_date")[0].disabled = false;
	        document.getElementsByName("reason")[0].disabled = false;
	        embargoDisabled.style.display="none";
	    } else {
	    	document.getElementsByName("embargo_until_date")[0].disabled = true;
	    	document.getElementsByName("reason")[0].disabled = true;
	    	embargoDisabled.style.display="block";
	    	embargoDisabled.style.color="red";
	    	embargoDisabled.style.marginBottom="0px";
	    }
	}
}
