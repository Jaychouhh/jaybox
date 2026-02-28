// IBPackageInstallerService.aidl
package com.jaybox.core.core.system.pm;

import com.jaybox.core.core.system.pm.BPackageSettings;
import com.jaybox.core.entity.pm.InstallOption;

// Declare any non-default types here with import statements

interface IBPackageInstallerService {
    int installPackageAsUser(in BPackageSettings file, int userId);
    int uninstallPackageAsUser(in BPackageSettings file, boolean removeApp, int userId);
    int updatePackage(in BPackageSettings file);
}
