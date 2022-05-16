/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Comparator;

/**
 * Compares the names of two {@link Collection}s by parent Community.
 */
public class CollectionNameComparatorByParent 
    implements Comparator<Collection>, Serializable {
    @Override
    public int compare(Collection collection1, Collection collection2) {
    	Community community1 = new Community();
    	Community community2 = new Community();
		try {
			community1 = collection1.getCommunities().get(0);
			community2 = collection2.getCommunities().get(0);
		} catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
		}
        return community1.getName().compareTo(community2.getName());
    }
}
