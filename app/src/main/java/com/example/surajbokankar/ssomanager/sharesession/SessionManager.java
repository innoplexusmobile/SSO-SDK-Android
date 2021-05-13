package com.example.surajbokankar.ssomanager.sharesession;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.prefrence.PreferenceManager;
import com.scottyab.aescrypt.AESCrypt;
import com.snatik.storage.Storage;

import net.ralphpina.permissionsmanager.PermissionsManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.ContentValues.TAG;

/**
 * Created by suraj.bokankar on 03/01/18.
 */

public class SessionManager {

    static SessionManager manager=null;
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    static final int READ_BLOCK_SIZE = 1000;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static SessionManager getManager(){
        if(manager==null){
            manager=new SessionManager();
        }
        return manager;
    }


    public String encryptKey(String plainText, int shiftKey)
    {
        plainText = plainText.toLowerCase();
        String cipherText = "";
        for (int i = 0; i < plainText.length(); i++)
        {
            int charPosition = ALPHABET.indexOf(plainText.charAt(i));
            int keyVal = (shiftKey + charPosition) % 26;
            char replaceVal = ALPHABET.charAt(keyVal);
            cipherText += replaceVal;
        }
        return cipherText;
    }


    public String decryptKey(String cipherText, int shiftKey)
    {
        cipherText = cipherText.toLowerCase();
        String plainText = "";
        for (int i = 0; i < cipherText.length(); i++)
        {
            int charPosition = ALPHABET.indexOf(cipherText.charAt(i));
            int keyVal = (charPosition - shiftKey) % 26;
            if (keyVal < 0)
            {
                keyVal = ALPHABET.length() + keyVal;
            }
            char replaceVal = ALPHABET.charAt(keyVal);
            plainText += replaceVal;
        }
        return plainText;
    }


    public String encryptFileContent(JSONObject jsonObject, String keyString){
        String encryptedJson=null;
        String response=jsonObject.toString();
        byte[] ivBytes=null;
        keyString=keyString+Constant.SHARE_SESSION.VALUE;
        keyString=encryptKey(keyString,3);
        try {
            encryptedJson= AESCrypt.encrypt(keyString, response);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return encryptedJson;

    }


    public JSONObject decryptFileContent(String encryptedString, String key){
        String strData="";
        JSONObject jsonObject=null;
        key=encryptKey(key,3);
        try {
            strData=AESCrypt.decrypt(key, encryptedString);
            Log.i(TAG, "decryptFileContent: Values="+strData);
            jsonObject=new JSONObject(strData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "decryptFileContent: Error="+e.getMessage());
        }

        return jsonObject;
    }





    // write text to file
    public void writeContentToFile(String key, JSONObject json, String fileName, String path) {
        // add-write text into file

        String response=encryptFileContent(json,key);
        try {
            File file=new File(path,fileName);
            FileOutputStream fileout=new FileOutputStream(file);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(key+"&");
            outputWriter.write(response);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read text from file
    public JSONObject readSharedContent(String fileName, String path, Context context) {
        //reading text from file
        JSONObject jsonObject=null;
        try {
            Activity activity= (Activity) context;
            if(PermissionsManager.get().isStorageGranted()){
                if(!checkShareSession(path,fileName)){
                    createFile(context,path,fileName);
                }
                File file=new File(path,fileName);
                FileInputStream fileIn=new FileInputStream(file);  //context.openFileInput(fileName)
                InputStreamReader InputRead= new InputStreamReader(fileIn);
                char[] inputBuffer= new char[READ_BLOCK_SIZE];
                String s="";
                int charRead;
                while ((charRead=InputRead.read(inputBuffer))>0) {
                    // char to string conversion
                    String readString= String.copyValueOf(inputBuffer,0,charRead);
                    s +=readString;
                }
                if(!TextUtils.isEmpty(s)){
                    String[] values=s.split("&");

                    String key=values[0]+ Constant.SHARE_SESSION.VALUE;
                    jsonObject=decryptFileContent(values[1],key);
                    InputRead.close();
                }else{
                    return jsonObject;
                }

            }


        } catch (Exception e) {
            Log.i(TAG, "readSharedContent: Error="+e.getMessage());
            e.printStackTrace();
        }
        return  jsonObject;
    }



    public  boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        boolean isPermissionGranted=false;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
            isPermissionGranted=true;
        }
        return  isPermissionGranted;
    }

    public boolean checkShareSession(String path, String fileName) {
        boolean isShared=false;

        try {
            if(PermissionsManager.get().isStorageGranted()){
                File file = new File(path, fileName);
                if (file.exists()) {
                    isShared=true;

                } else {
                    isShared=false;
                }
            }else{
                isShared=false;
            }


        }catch (Exception e){
            Log.i(TAG, "checkShareSession: Error="+e.getMessage());
        }
        return  isShared;
    }


    public String getPath(){
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + Constant.SHARE_SESSION.DIRECTORY_NAME;
        return path;
    }

    public String getFileName(){
        return Constant.SHARE_SESSION.FILE_NAME;
    }


    public void onLogoutDestroySession(Context context,String path,String fileName){

        if(checkShareSession(path,fileName)){
            File file = new File(path, fileName);
            deleteFile(file);
            SessionModel sessionModel=new SessionModel();
            PreferenceManager.getInstance(context).setSessionModel(sessionModel);
        }
    }



    public File createFile(Context context,String directoryPath,String fileName){
// init
        Activity activity= (Activity) context;
        File file=null;
        if(PermissionsManager.get().isStorageGranted()){
            Storage storage = new Storage(context);
            storage.createDirectory(directoryPath);
            file = new File(directoryPath, fileName);
            try {
                if(file.exists()){
                    Log.i(TAG, "createFile: Exist");
                    //file.delete();

                }else{
                    boolean isCreated=file.createNewFile();
                    Log.i(TAG, "createFile: ="+isCreated);

                }

            } catch (Exception e) {
                Log.i(TAG, "createFile: Error="+e.getMessage());
                e.printStackTrace();
            }
        }

        return file;
    }

    public void deleteFile(File file){
        file.delete();
    }



}
