LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := OpenCVTest
LOCAL_SRC_FILES := OpenCVTest.cpp

include $(BUILD_SHARED_LIBRARY)
