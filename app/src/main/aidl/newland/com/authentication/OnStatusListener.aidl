// OnStatusListener.aidl
package newland.com.authentication;

// Declare any non-default types here with import statements

interface OnStatusListener {
    void onSucceed(String json);
    /**
      * 失败时回调
      */
    void onError(int errorCode, String errMsg);
}
