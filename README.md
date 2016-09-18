# AndroidMPermission (...Still in development)

Easy to handle permission changes introduced in android M

Android M introduce some changes regarding app permission. Now user have to request dangerous permission by own.
What if someone help you to get that permission without worrying that which permission is needed to ask.  
This code will do everything for you from beginning to end. Which permissions are dangerous in your manifest and for which you need permission from user. It will decide ask and give you callback based on that. Simple !!!

Project contains -

1. How to use ( Activity class )
2. PermissionManager class. 

How to use –

1. Get PermissionManager instance. 

permissionManager = PermissionManager.getInstance(this);

2. Execute it. 

permissionManager.execute(new PermissionManager.PermissionListener() {
    
    @Override
    public void onPermissionGranted(String permission) {
        Toast.makeText(MainActivity.this, "Permission granted " + permission, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionDenied(String permission) {
        Toast.makeText(MainActivity.this, "Permission denied " + permission, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorWhileGettingPermission(String error) {
        Toast.makeText(MainActivity.this, "Error " + error, Toast.LENGTH_LONG).show();
    }
});

3. Don’t forget to add in your activity. 

@Override
public void onRequestPermissionsResult(int requestCode,
                                       String permissions[], int[] grantResults) {
    permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
}

That’s it.

It also provide some utility methods -

1. hasPermissions(String permission) - To check app has given permission.
2. askPermission(String.. permissions) - Ask specific permission of your choice. 
3. getManifestPermissions() - Get your permission from manifest file.
4. getDangerousPermissions() - Get list of dangerous permissions from android. 


