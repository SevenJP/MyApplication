// ActivatesDeviceServiceAidl.aidl
package newland.com.authentication;
import newland.com.authentication.OnStatusListener;

// Declare any non-default types here with import statements

interface ActivatesDeviceServiceAidl {

    void activatesDevice (String sn,OnStatusListener listener);
    void checkPassword (String password, int type ,String userNo, OnStatusListener listener);
}
