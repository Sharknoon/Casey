;This file will be executed next to the application bundle image
;I.e. current directory will contain folder DualIDE with application files
[Setup]
AppId={{sharknoon.dualide}}
AppName=DualIDE
AppVersion=0.1
AppVerName=DualIDE 0.1
AppPublisher=Shark Industries
AppComments=DualIDE
AppCopyright=Copyright (C) 2017
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\DualIDE
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Shark Industries
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=DualIDE-0.1
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=DualIDE\DualIDE.ico
UninstallDisplayIcon={app}\DualIDE.ico
UninstallDisplayName=DualIDE
WizardImageStretch=No
WizardSmallImageFile=DualIDE-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"

[Files]
Source: "DualIDE\DualIDE.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "DualIDE\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\DualIDE"; Filename: "{app}\DualIDE.exe"; IconFilename: "{app}\DualIDE.ico"; Check: returnTrue()
Name: "{commondesktop}\DualIDE"; Filename: "{app}\DualIDE.exe";  IconFilename: "{app}\DualIDE.ico"; Check: returnFalse()


[Run]
Filename: "{app}\DualIDE.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\DualIDE.exe"; Description: "{cm:LaunchProgram,DualIDE}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\DualIDE.exe"; Parameters: "-install -svcName ""DualIDE"" -svcDesc ""DualIDE"" -mainExe ""DualIDE.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\DualIDE.exe "; Parameters: "-uninstall -svcName DualIDE -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
