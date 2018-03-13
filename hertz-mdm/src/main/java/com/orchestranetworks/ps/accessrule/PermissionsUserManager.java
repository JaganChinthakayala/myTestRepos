/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.accessrule;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.service.*;

public interface PermissionsUserManager
{
	SessionPermissions getSessionPermissions(Repository repo, UserReference user);
}
