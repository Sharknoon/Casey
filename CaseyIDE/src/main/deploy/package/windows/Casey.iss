;This file will be executed next to the application bundle image
;I.e. current directory will contain folder Casey with application files
[Setup]
AppId={{sharknoon.casey.ide}}
AppName=Casey
AppVersion=0.1
AppVerName=Casey 0.1
AppPublisher=Shark Industries
AppComments=Casey
AppCopyright=Copyright (C) 2018
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={userappdata}\Casey
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Shark Industries
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=Casey-0.1
Compression=lzma
ShowLanguageDialog=auto
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=Casey.ico
UninstallDisplayIcon={app}\Casey.ico
UninstallDisplayName=Casey
WizardImageStretch=No
WizardSmallImageFile=Casey-setup-icon.bmp
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"

[Files]
Source: "Casey\Casey.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "Casey\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Casey"; Filename: "{app}\Casey.exe"; IconFilename: "{app}\Casey.ico"; Check: returnTrue()
Name: "{commondesktop}\Casey"; Filename: "{app}\Casey.exe";  IconFilename: "{app}\Casey.ico"; Check: returnFalse()


[Run]
Filename: "{app}\Casey.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\Casey.exe"; Description: "{cm:LaunchProgram,Casey}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\Casey.exe"; Parameters: "-install -svcName ""Casey"" -svcDesc ""Casey"" -mainExe ""Casey.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\Casey.exe "; Parameters: "-uninstall -svcName Casey -stopOnUninstall"; Check: returnFalse()

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
