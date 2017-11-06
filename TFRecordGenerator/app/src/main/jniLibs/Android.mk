LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE:=SHARED
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on

include /home/e/Apps/opencv_android/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES  := native-lib.cpp
LOCAL_C_INCLUDES += /home/e/Apps/opencv_android/OpenCV-android-sdk/sdk/native/jni/include
LOCAL_LDLIBS     += -llog -ldl
LOCAL_CFLAGS     += -DOPENCV_OLDER_VISION

LOCAL_CPP_FEATURES += exceptions (Recommended)
LOCAL_CPPFLAGS     += -fexceptions


LOCAL_MODULE     := native-lib

include $(BUILD_SHARED_LIBRARY)