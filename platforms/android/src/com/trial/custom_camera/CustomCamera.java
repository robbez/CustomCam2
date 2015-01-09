package com.trial.custom_camera;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;

public class CustomCamera extends CordovaPlugin {
	
	private CallbackContext mCallContext;
	
	@Override
	public boolean execute(String action, CordovaArgs args,
			CallbackContext callbackContext) throws JSONException {
			
		mCallContext = callbackContext;
		Context context = cordova.getActivity().getApplicationContext();
		Intent mIntent = new Intent(context, CameraActivity.class);
		cordova.startActivityForResult(this, mIntent, 0);
		
		return true;
	}
}
