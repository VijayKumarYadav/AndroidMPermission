package vijay.apps.personal.androidmpermission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to manager permissions.
 */
@TargetApi(23)
public class PermissionManager {

    private Context mContext;

    private static volatile PermissionManager instance = null;

    private static LinkedList<String> dangerousPermission = null;

    private PermissionListener mListener;

    private PermissionManager(Context context) {
        mContext = context;
    }

    /**
     * Get instance
     *
     * @param context - context
     * @return PermissionManager instance
     */
    public static PermissionManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PermissionManager.class) {
                if (instance == null)
                    instance = new PermissionManager(context);
            }
        }
        return instance;
    }

    /**
     * Get the list of all dangerous permission from android system.
     *
     * @param context - Activity context
     * @return - List of all dangerous permission
     */
    private static LinkedList<String> getDangerousPermission(Context context) {

        LinkedList<String> permissionList = new LinkedList<>();
        // Get the permissions for the core android package
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("android", PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (packageInfo.permissions != null) {
            // For each defined permission
            for (PermissionInfo permission : packageInfo.permissions) {
                if (permission.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS) {
                    permissionList.add(permission.name);
                }
            }
        }
        return permissionList;
    }

    /**
     * Execute to get permission.
     *
     * @param listener - listener to get call for sucess or fail.
     */
    public void execute(PermissionListener listener) {

        if (listener == null) {
            return;
        }

        mListener = listener;

        if (dangerousPermission == null) {
            dangerousPermission = getDangerousPermission(mContext);
            if (dangerousPermission == null) {
                listener.onErrorWhileGettingPermission("Error in getting Dangerous Permission");
            }
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            listener.onErrorWhileGettingPermission(e.getMessage());
        }
        String[] requestedPermissions = packageInfo.requestedPermissions;
        for (String permission : requestedPermissions) {
            int index = dangerousPermission.indexOf(permission);
            if (index != -1) {
                checkPermission(permission, index);
            }
        }
    }

    private void checkPermission(String permission, int index) {
        if (!hasPermissions(permission)) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission},
                    index);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted,
            if (mListener != null) {
                mListener.onPermissionGranted(dangerousPermission.get(requestCode));
            }

        } else {
            if (mListener != null) {
                mListener.onPermissionDenied(dangerousPermission.get(requestCode));
            }
        }
    }

    /**
     * Check app has given permission.
     * @param perms -  permission to check.
     * @return boolean indicating that app has given permission or not.
     */
    public boolean hasPermissions(String perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        return ContextCompat.checkSelfPermission(mContext, perms) ==
                PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Permission even listener
     */
    public interface PermissionListener {
        /**
         * Permission granted listener
         *
         * @param permission - name of granted permission
         */
        void onPermissionGranted(String permission);

        /**
         * Permission denied listener
         *
         * @param permission - name of denied permission
         */
        void onPermissionDenied(String permission);

        /**
         * Error listener while getting Permission
         *
         * @param error - Possible error.
         */
        void onErrorWhileGettingPermission(String error);
    }


}
