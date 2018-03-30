LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := gpuimage-library
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \

LOCAL_SRC_FILES := \
	D:\Srtp_project\library\jni\yuv-decoder.c \

LOCAL_C_INCLUDES += D:\Srtp_project\library\jni
LOCAL_C_INCLUDES += D:\Srtp_project\library\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
