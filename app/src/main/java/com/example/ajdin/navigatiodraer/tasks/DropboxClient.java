package com.example.ajdin.navigatiodraer.tasks;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Created by ajdin on 17.3.2018..
 */

public class DropboxClient {

    public static DbxClientV2 getClient(String ACCESS_TOKEN) {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/AjdinovProjekt", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        return client;
    }
}