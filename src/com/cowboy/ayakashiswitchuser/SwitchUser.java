package com.cowboy.ayakashiswitchuser;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.stericson.RootTools.*;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SwitchUser extends Activity {
	private final String systemBin = "/system/bin/";
	private final String systemXBin = "/system/xbin/";

	private final String externalZyngaOfficialStoragePath =  Environment.getExternalStorageDirectory() + "/data/com.zynga/";
	private final String internalZyngaStoragePath = "/data/data/com.zynga.zjayakashi/"; 
	private final String zyngaPackage = "com.zynga.zjayakashi";
	private final String externalZyngaDataStoragePath = Environment.getExternalStorageDirectory() + "/data/com.cowboy.AyakashiSwitchUser/";
	private final String officialName = "zynga.properties";
	private final String sOfficialUserPath = externalZyngaOfficialStoragePath + officialName;
	private String sRenameCurrentUserToPath = "";
	private String sSwitchToUserPath = "";
	
	private TextView tvResult;
	private EditText etSwitchToUser;
	private EditText etRenameCurrentUserTo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_ui);
		
		Button btnSwtich = (Button) findViewById(R.id.btnSwitch);
		btnSwtich.setOnClickListener(new OnClickListener() {
			
		    @Override
		    public void onClick(View arg0) {
				// TODO Auto-generated method stub
		    	startSwitchUser();
		    }
	
		});
		
		etSwitchToUser = (EditText) findViewById(R.id.etSwitchToUser);
		etSwitchToUser.setText(this.officialName);
		etRenameCurrentUserTo = (EditText) findViewById(R.id.etRenameCurrentUserTo);
		etRenameCurrentUserTo.setText(this.officialName);
		
		tvResult = (TextView) findViewById(R.id.tvResult);
		
		try {
			Runtime.getRuntime().exec(commandPath("su"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tvResult.setText("Cannot get root access.");
		}
	}

	public void startSwitchUser() {
		tvResult.setText("");
		
		sRenameCurrentUserToPath = externalZyngaOfficialStoragePath + etRenameCurrentUserTo.getText().toString();
		sSwitchToUserPath = externalZyngaOfficialStoragePath + etSwitchToUser.getText().toString();
		
		try {
		    // Executes the command.
			if (this.isFileExist(sRenameCurrentUserToPath)) {
				this.tvResult.setText("Rename To Current File Already Exist.");
				return;
			}else if (!this.isFileExist(sSwitchToUserPath)) {
				this.tvResult.setText("Switch To File Not Exist.");
				return;
			}
			
			String dataSwitchToUserPath = this.externalZyngaDataStoragePath + etSwitchToUser.getText().toString();
			String dataRenameCurrentUserPath = this.externalZyngaDataStoragePath + etRenameCurrentUserTo.getText().toString();			

			File createNewFolder = new File(dataRenameCurrentUserPath);
			if (!createNewFolder.exists()) {
				Log.d("CreateFolder", "..");
				createNewFolder.mkdirs();
			}
			
			CommandCapture command = null;
			
			String systemPath = "";
			String[] runCommand = {"mv", this.sOfficialUserPath + " " + this.sRenameCurrentUserToPath
//					, "mkdir", "-p " + dataRenameCurrentUserPath
					, "cp", "-R " + this.internalZyngaStoragePath + "* " + dataRenameCurrentUserPath
					, "pm", "clear " + this.zyngaPackage
					, "mv", this.sSwitchToUserPath + " " + this.sOfficialUserPath
					, "cp", "-R " + dataSwitchToUserPath + "/* " + this.internalZyngaStoragePath
//					, "rm", "-rf " + dataSwitchToUserPath
					, "chmod", "-R 777 " + this.internalZyngaStoragePath
					};

		    for (int i=0; i<runCommand.length; i=i+2) {
		    	systemPath = this.commandPath(runCommand[i]);
		    	if (runCommand[i].equals("mv")) {
		    		if (runCommand[i+1].contains(sSwitchToUserPath) || runCommand[i+1].contains(sRenameCurrentUserToPath)) {
			    		if (this.isFileExist(dataRenameCurrentUserPath)) {
					    	Log.d("Command " + runCommand[i], systemPath + " " + runCommand[i+1]);
					    	command = new CommandCapture(0, systemPath + " " + runCommand[i+1]);
							RootTools.getShell(true).add(command).waitForFinish();
			    		}
		    		}
		    	} else if (runCommand[i].equals("cp")) {
		    		if (runCommand[i+1].contains(dataRenameCurrentUserPath)) {
			    		if (this.isFileExist(dataRenameCurrentUserPath)) {
					    	Log.d("Command " + runCommand[i], systemPath + " " + runCommand[i+1]);
					    	command = new CommandCapture(0, systemPath + " " + runCommand[i+1]);
							RootTools.getShell(true).add(command).waitForFinish();
			    		}
		    		}
		    		if (runCommand[i+1].contains(dataSwitchToUserPath)) {
				    	Log.d("Command " + runCommand[i], systemPath + " " + runCommand[i+1]);
				    	command = new CommandCapture(0, systemPath + " " + runCommand[i+1]);
						RootTools.getShell(true).add(command).waitForFinish();
		    		}
		    	} else if (runCommand[i].equals("mkdir")) {
		    		if (!this.isFileExist(runCommand[i+1])) {
				    	Log.d("Command " + runCommand[i], systemPath + " " + runCommand[i+1]);
				    	command = new CommandCapture(0, systemPath + " " + runCommand[i+1]);
						RootTools.getShell(true).add(command).waitForFinish();
		    		}
		    	} else if (runCommand[i].equals("rm")) {
		    		if (runCommand[i+1].contains(dataSwitchToUserPath)) {
				    	Log.d("Command " + runCommand[i], systemPath + " " + runCommand[i+1]);
				    	command = new CommandCapture(0, systemPath + " " + runCommand[i+1]);
						RootTools.getShell(true).add(command).waitForFinish();
		    		}
		    	} else {
			    	Log.d("Command " + runCommand[i], systemPath + " " + runCommand[i+1]);
			    	command = new CommandCapture(0, systemPath + " " + runCommand[i+1]);
					RootTools.getShell(true).add(command).waitForFinish();
		    	}
		    }
		    this.tvResult.setText("Switch Success!");
		} catch (IOException e) {
		    Log.d("IOException", e.toString());
			this.tvResult.setText(e.toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.tvResult.setText(e.toString());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.tvResult.setText(e.toString());
		} catch (RootDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.tvResult.setText(e.toString());
		}
	}
	
	public boolean isFileExist(String path) {
		File folder = new File(path);
		if (folder.exists()) return true;
		else return false;
	}
	
	public boolean isDirectoryEmpty(String path) {
		File folder = new File(path);
		if (folder.isDirectory()) {
			if (folder.list().length == 0) {
				return true;
			}
		}
		return false;
	}
	
	public String commandPath(String command) throws IOException {
		String systemPath = null;
		if (this.isFileExist(systemBin + command)) {
			systemPath = this.systemBin + command;
			return systemPath;
		}
		else if (this.isFileExist(systemXBin + command)) {
			systemPath = this.systemXBin + command;
			return systemPath;
		}
		else {
			throw new IOException("Missing Command File (" + command + "). Process cannot be done.");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.switch_user, menu);
		return true;
	}
}
