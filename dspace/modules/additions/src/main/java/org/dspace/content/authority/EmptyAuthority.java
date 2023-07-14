package org.dspace.content.authority;

import org.dspace.content.Collection;

/**
 * This authority will be used if you want to use and edit the authority hidden inputs,
 * without using a set of authorities predefined in some place. In combination
 * with the 'raw' presentation mode (in the 'dspace.cfg'), use this class to
 * modify 'at hand' the authority value input during the submission.
 * @author facundo@sedici.unlp.edu.ar
 */
public class EmptyAuthority implements ChoiceAuthority {

	/**
	 * This method returns an empty Choices for every value of 'text' received.
	 * @return empty Choices.
	 */
	@Override
	public Choices getMatches(String text, int start, int limit, String locale) {
		Choice v[]= new Choice[1];
		v[0]= new Choice(text, text, text);
		return new Choices(v,0,v.length,Choices.CF_ACCEPTED,false,0);
	}

	@Override
	public Choices getBestMatch(String text, String locale) {
		return getMatches(text, 0, 0, locale);
	}

	@Override
	public String getLabel(String key, String locale) {
		
		return new String();
	}

	@Override
	public String getPluginInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPluginInstanceName(String name) {
		// TODO Auto-generated method stub
	}
}
