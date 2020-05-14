# epf-plugin-spem
Plugin for Eclipse Process Framework composer to export process definition in a format digestable by other tools.

## How to install Eclipse IDE and EPF Composer
* download Eclipse IDE from https://www.eclipse.org/downloads/
  - run install via downloaded installator
  - choose Eclipse IDE for Eclipse Commiters distribution
* download EPF Composer v1.5.2 from https://www.eclipse.org/epf/downloads/tool/epf1.5.0_downloads.php
  - unzip downloaded archive
  
## How to open and build this project in Eclipse IDE
### Open project
* run Eclipse IDE
* select _File_ -> _Open Projects from File System_
* specify plug-in folder to _Import Source_ and finish the wizard
* do the last two steps again for second plug-in
* open `platform.target` file and edit location to current EPF Composer installation folder
* select _Set as Target Platform_ and _Reload Target Platform_

### Build project
* select _File_ -> _Export..._
* select _Plug-in Development_ -> _Deployable plug-ins and fragments_ -> _Next >_
* check functional and UI plug-in, specify directory and finish the wizard
* check for built plug-ins in the directory
* move/copy these built plug-ins to \<EPF instalation folder\>\plugins

## How to use this project
### Download and open example project
* download example EPF project from https://gitlab.com/simeckol/epf_antipatterns
* open EPF Composer
* to open the example project, select _File_ -> _Open_ -> _Method Library_
* specify path do downloaded example project and finish the wizard, the example library should open
### Export anti-patterns
* to export example anti-patterns, select _File_ -> _Export..._
* select _Pattern_ -> _Next >_
* select anti-patterns you want to export -> _Next >_
* specify export directory and finish the wizard
* check for exported SQL scripts in the directory
* logs from export are in \<EPF instalation folder\>\logs\export\pattern
