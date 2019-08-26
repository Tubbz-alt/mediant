package io.dt42.mediant;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.textile.pb.Model.Block;
import io.textile.pb.Model.Peer;
import io.textile.pb.Model.Thread;
import io.textile.pb.Model.ThreadList;
import io.textile.pb.QueryOuterClass.ContactQuery;
import io.textile.pb.QueryOuterClass.QueryOptions;
import io.textile.pb.View.AddThreadConfig;
import io.textile.pb.View.Files;
import io.textile.pb.View.FilesList;
import io.textile.pb.View.InviteViewList;
import io.textile.textile.Handlers;
import io.textile.textile.Handlers.BlockHandler;
import io.textile.textile.Textile;
import io.textile.textile.TextileLoggingListener;
import mobile.SearchHandle;

public class TextileWrapper {
    private static final String TAG = "TEXTILE_WRAPPER";

    /*-------------------------------------------------------------------------
     * Construction and Destruction
     *------------------------------------------------------------------------*/

    public static void initTextile(Context ctx) {
        try {
            final File filesDir = ctx.getFilesDir();
            final String path = new File(filesDir, "textile-go").getAbsolutePath();

            if (!Textile.isInitialized(path)) {
                String phrase = Textile.initializeCreatingNewWalletAndAccount(path, true, false);
                System.out.println(phrase);
            }

            Textile.launch(ctx, path, true);

            Textile.instance().addEventListener(new TextileLoggingListener());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void destroyTextile() {
        Textile.instance().destroy();
    }


    /*-------------------------------------------------------------------------
     * Thread
     *------------------------------------------------------------------------*/

    public static void createThread() {
        Log.i(TAG, "Create a thread");

        AddThreadConfig.Schema schema = AddThreadConfig.Schema.newBuilder()
                .setPreset(AddThreadConfig.Schema.Preset.BLOB)
                .build();
        AddThreadConfig config = AddThreadConfig.newBuilder()
                .setKey("your.bundle.id.version.Basic")
                .setName("Meimei")
                .setType(Thread.Type.READ_ONLY)
                .setSharing(Thread.Sharing.SHARED)
                .setSchema(schema)
                .build();
        try {
            Textile.instance().threads.add(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listThread() {
        Log.i(TAG, "List threads");

        try {
            ThreadList tlist = Textile.instance().threads.list();
            Log.i(TAG, tlist.toString());

            Log.i(TAG, "Meimei thread ID: " + getThreadIdByName("Meimei"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Get the ID of a thread by its name.
     *
     * param threadName: The targeting thread name.
     * return: The ID of the thread whose name matches the given thread name.
     */
    private static String getThreadIdByName(String threadName) {
        ThreadList tlist = null;
        try {
            tlist = Textile.instance().threads.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Targeting thread name: " + threadName);
        for (int i = 0; i < tlist.getItemsCount(); i++) {
            Thread t = tlist.getItems(i);
            Log.d(TAG, "Thread name: " + t.getName());
            Log.d(TAG, "Thread ID: " + t.getId());
            if (threadName.equals(t.getName())) {
                Log.d(TAG, String.format("%s (%d) == %s (%d)",
                        t.getName(), t.getName().length(),
                        threadName, threadName.length()));
                return t.getId();
            } else {
                Log.d(TAG, String.format("%s (%d) != %s (%d)",
                        t.getName(), t.getName().length(),
                        threadName, threadName.length()));
            }
        }
        Log.e(TAG, "Should NOT be here!!!!!");
        return null;
    }

    private static void addThreadFileByFilepath(String filePath, String threadId, String caption) {
        Textile.instance().files.addFiles(filePath, threadId, caption, new BlockHandler() {
            @Override
            public void onComplete(Block block) {
                Log.i(TAG, "Add file to thread successfully");
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "Add file to thread with error: " + e.toString());
            }
        });
    }

    /*-------------------------------------------------------------------------
     * Files
     *------------------------------------------------------------------------*/

    public static void addImage(String filePath) {
        addThreadFileByFilepath(
                filePath,
                getThreadIdByName("nbsdev"),
                getTimestamp()
        );
    }

    public static void addImageDev() {
        // changed thread name from Meimei to nbsdev
        addThreadFileByFilepath(
                "/storage/emulated/0/DCIM/100MEDIA/IMAG0976.jpg",
                getThreadIdByName("nbsdev"),
                getTimestamp()
        );
    }

    public static void listImages() {
        FilesList filesList;
        try {
            filesList = Textile.instance().files.list(
                    getThreadIdByName("nbsdev"),
                    null,
                    5);
            for (int i = 0; i < filesList.getItemsCount(); i++) {
                Files f = filesList.getItems(i);
                Log.d(TAG, "File string: " + f.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static List<Files> getImageList() {
        FilesList filesList;
        try {
            filesList = Textile.instance().files.list(
                    getThreadIdByName("nbsdev"),
                    null,
                    5
            );
            return filesList.getItemsList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void fetchImageContent(String hash, Handlers.DataHandler dataHandler) {
        Textile.instance().files.content(hash, dataHandler);
    }

    /*-------------------------------------------------------------------------
     * Contacts
     *------------------------------------------------------------------------*/

    /*
    private void addContactByName(String address) {
    }
    */

    public static void getContactByName() {
        QueryOptions options = QueryOptions.newBuilder()
                .setWait(10)
                .setLimit(1)
                .build();
        ContactQuery query = ContactQuery.newBuilder()
                .setName("NBS")
                .build();
        try {
            SearchHandle handle = null;
            handle = Textile.instance().contacts.search(query, options);
            Log.i(TAG, "handle string: " + handle.toString());

            //Textile.instance().contacts.add(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*-------------------------------------------------------------------------
     * Invites
     *------------------------------------------------------------------------*/

    // This function gets nothing
    public static void listInvitation() {
        InviteViewList invites = null;
        try {
            invites = Textile.instance().invites.list();
            for (int i = 0; i < invites.getItemsCount(); i++) {
                Log.d(TAG, "Invite: " + invites.getItems(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Invitation was sent by Textile Photo
     */
    public static void acceptExternalInvitation(String inviteId, String key) {
        try {
            String threadId = Textile.instance().invites.acceptExternal(
                    inviteId, key
            );
            Log.i(TAG, "Accept invitation of thread " + threadId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inviteJonathan() {
        // share Meimei to Jonathan
        try {
            Textile.instance().invites.add(
                    getThreadIdByName("Meimei"),
                    "P7HfibZvGWznbhYDT4LD8csDDiyaWCdLgMbrj7L7MCaksRKY"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * param address: Peer address (public key with leading 'P' char)
     */
    /*
    private SearchHandle addContactByAddress(String address) {
        QueryOptions options = QueryOptions.newBuilder()
                .setWait(10)
                .setLimit(1)
                .build();
        ContactQuery query = ContactQuery.newBuilder()
                .setAddress("P8rW2RCMn75Dcb96Eiyg8mirb8nL4ruCumvJxKZRfAdpE5fG")
                .build();
        try {
            return Textile.instance().contacts.search(query, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    /*-------------------------------------------------------------------------
     * Others
     *------------------------------------------------------------------------*/

    public static void getProfile() {
        /* Get local profile. */
        Log.i(TAG, "Get profile");

        Peer peer = null;
        try {
            peer = Textile.instance().profile.get();
            Log.i(TAG, peer.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*-------------------------------------------------------------------------
     * Utilities
     *------------------------------------------------------------------------*/
    public static String getTimestamp() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.TAIWAN);
        return df.format(new Date());
    }
}
