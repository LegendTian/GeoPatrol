#include <jni.h>
#include <android/log.h>


#define LOG_TAG "NDK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG "_DEG", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG "_INF", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG "_ERR", __VA_ARGS__)


JNIEXPORT void JNICALL
Java_com_al_app_geopatrol_App_appSetup(JNIEnv *env, jobject instance) {

    // TODO
    LOGI("call appSetup()...");
}
